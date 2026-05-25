package com.msa.eshop.data.remote.utills

import android.content.Context
import android.os.SystemClock
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.net.ssl.SSLException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber

class MakeSafeApiCall @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun <T> makeSafeApiCall(
        api: suspend () -> Response<T>
    ): Flow<Resource<T>> {
        return flow {
            emit(Resource.loading())

            val startedAt = SystemClock.elapsedRealtime()
            val networkSnapshot = context.networkSnapshot()

            Timber.tag(Constant.NETWORK_LOG_TAG).d(
                "API request started | network=[${networkSnapshot.description}]"
            )

            /*
             * فقط نبود شبکه فعال را block می‌کنیم.
             * validated=false در اینترنت داخلی/سازمانی نباید مانع request شود.
             */
            if (!networkSnapshot.hasActiveNetwork) {
                emitNetworkNotAvailable(networkSnapshot)
                return@flow
            }

            val response = api.invoke()
            val durationMs = SystemClock.elapsedRealtime() - startedAt

            val request = response.raw().request
            val method = request.method
            val url = request.url.toString().sanitizeUrl()
            val code = response.code()

            Timber.tag(Constant.NETWORK_LOG_TAG).d(
                "API response received | method=$method | url=$url | code=$code | success=${response.isSuccessful} | duration=${durationMs}ms"
            )

            val body = response.body()

            if (response.isSuccessful) {
                if (body != null) {
                    Timber.tag(Constant.NETWORK_LOG_TAG).d(
                        "API success | method=$method | url=$url | code=$code | duration=${durationMs}ms"
                    )

                    emit(Resource.success(body))
                } else {
                    Timber.tag(Constant.NETWORK_LOG_TAG).e(
                        "API success but body is null | method=$method | url=$url | code=$code | duration=${durationMs}ms"
                    )

                    emit(
                        Resource.error(
                            error = MsaError(
                                code = ErrorCode.NULL_RESPONSE_BODY,
                                message = "پاسخ سرور خالی است",
                                url = url,
                                method = method
                            )
                        )
                    )
                }

                return@flow
            }

            val rawError = response.errorBody()?.string()
            val serverMessage = parseErrorMessage(rawError)

            val message = serverMessage
                ?: response.message().takeIf { it.isNotBlank() }
                ?: httpStatusMessage(code)

            Timber.tag(Constant.NETWORK_LOG_TAG).e(
                "API http error | method=$method | url=$url | code=$code | message=$message | rawError=${rawError.orEmpty().take(Constant.MAX_ERROR_LOG_LENGTH)} | duration=${durationMs}ms"
            )

            emit(
                Resource.error(
                    error = MsaError(
                        code = code,
                        message = message,
                        rawError = rawError,
                        url = url,
                        method = method
                    )
                )
            )
        }
            .flowOn(Dispatchers.IO)
            .catch { throwable ->
                val networkSnapshot = context.networkSnapshot()
                val error = throwable.toMsaError(networkSnapshot)

                Timber.tag(Constant.NETWORK_LOG_TAG).e(
                    throwable,
                    "API exception | code=${error.code} | message=${error.safeMessage} | network=[${networkSnapshot.description}]"
                )

                emit(Resource.error(error = error))
            }
    }

    private suspend fun kotlinx.coroutines.flow.FlowCollector<Resource<Nothing>>.emitNetworkNotAvailable(
        networkSnapshot: NetworkSnapshot
    ) {
        Timber.tag(Constant.NETWORK_LOG_TAG).e(
            "API request blocked | reason=no_active_network | network=[${networkSnapshot.description}]"
        )

        emit(
            Resource.error(
                error = MsaError(
                    code = ErrorCode.NETWORK_NOT_AVAILABLE,
                    message = "هیچ شبکه فعالی روی دستگاه پیدا نشد"
                )
            )
        )
    }

    private fun parseErrorMessage(rawError: String?): String? {
        if (rawError.isNullOrBlank()) return null

        return runCatching {
            val trimmed = rawError.trim()

            when {
                trimmed.startsWith("{") -> parseJsonObjectMessage(JSONObject(trimmed))
                trimmed.startsWith("[") -> parseJsonArrayMessage(JSONArray(trimmed))
                else -> trimmed.takeIf { it.length <= 250 }
            }
        }.getOrElse { throwable ->
            Timber.tag(Constant.NETWORK_LOG_TAG).e(
                throwable,
                "Parse error body failed | raw=${rawError.take(Constant.MAX_ERROR_LOG_LENGTH)}"
            )
            null
        }?.takeIf { it.isNotBlank() }
    }

    private fun parseJsonObjectMessage(json: JSONObject): String? {
        return json.optString("message")
            .ifBlank { json.optString("error") }
            .ifBlank { json.optString("detail") }
            .ifBlank { json.optString("title") }
            .ifBlank {
                val errors = json.opt("errors")
                when (errors) {
                    is JSONObject -> errors.keys().asSequence()
                        .mapNotNull { key ->
                            val value = errors.opt(key)
                            when (value) {
                                is JSONArray -> value.optString(0)
                                else -> value?.toString()
                            }
                        }
                        .firstOrNull()

                    is JSONArray -> errors.optString(0)
                    else -> ""
                }
            }
            .takeIf { it!!.isNotBlank() }
    }

    private fun parseJsonArrayMessage(json: JSONArray): String? {
        if (json.length() == 0) return null

        return when (val first = json.opt(0)) {
            is JSONObject -> parseJsonObjectMessage(first)
            else -> first?.toString()
        }
    }

    private fun httpStatusMessage(code: Int): String {
        return when (code) {
            ErrorCode.HTTP_BAD_REQUEST -> "درخواست نامعتبر است"
            ErrorCode.HTTP_UNAUTHORIZED -> "نیاز به ورود مجدد دارید"
            ErrorCode.HTTP_FORBIDDEN -> "شما مجوز دسترسی به این بخش را ندارید"
            ErrorCode.HTTP_NOT_FOUND -> "آدرس سرویس پیدا نشد"
            ErrorCode.HTTP_CONFLICT -> "این درخواست با وضعیت فعلی سیستم ناسازگار است"
            ErrorCode.HTTP_UNPROCESSABLE -> "اطلاعات ارسال‌شده معتبر نیست"
            in 500..599 -> "خطای سرور رخ داد"
            else -> "خطا در ارتباط با سرور"
        }
    }

    private fun Throwable.toMsaError(
        networkSnapshot: NetworkSnapshot
    ): MsaError {
        return when (this) {
            is UnknownHostException -> {
                MsaError(
                    code = ErrorCode.NETWORK_DNS_FAILED,
                    message = "آدرس سرور پیدا نشد. اتصال شبکه، DNS یا آدرس سرور را بررسی کنید",
                    rawError = stackTraceToString()
                )
            }

            is ConnectException -> {
                MsaError(
                    code = ErrorCode.NETWORK_CONNECTION_FAILED,
                    message = "اتصال به سرور برقرار نشد. ممکن است سرور در دسترس نباشد یا شبکه به آن مسیر نداشته باشد",
                    rawError = stackTraceToString()
                )
            }

            is SocketTimeoutException -> {
                MsaError(
                    code = ErrorCode.NETWORK_TIMEOUT,
                    message = "زمان انتظار اتصال به سرور تمام شد",
                    rawError = stackTraceToString()
                )
            }

            is SSLException -> {
                MsaError(
                    code = ErrorCode.NETWORK_SSL_FAILED,
                    message = "خطای امنیتی در اتصال به سرور رخ داد",
                    rawError = stackTraceToString()
                )
            }

            is IOException -> {
                MsaError(
                    code = ErrorCode.NETWORK_CONNECTION_FAILED,
                    message = if (networkSnapshot.hasActiveNetwork) {
                        "خطای شبکه رخ داد. اتصال داخلی یا دسترسی به سرور را بررسی کنید"
                    } else {
                        "هیچ شبکه فعالی روی دستگاه پیدا نشد"
                    },
                    rawError = stackTraceToString()
                )
            }

            else -> {
                MsaError(
                    code = ErrorCode.CATCH_BODY,
                    message = message ?: "خطای نامشخص رخ داد",
                    rawError = stackTraceToString()
                )
            }
        }
    }

    private fun String.sanitizeUrl(): String {
        return substringBefore("?")
    }
}