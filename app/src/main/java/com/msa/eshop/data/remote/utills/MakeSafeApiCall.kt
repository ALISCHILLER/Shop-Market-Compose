package com.msa.eshop.data.remote.utills

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
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
            val networkSnapshot = getNetworkSnapshot()

            Timber.tag(TAG).d(
                "API request started | network=[$networkSnapshot]"
            )

            /*
             * نکته مهم:
             * اینجا عمداً از NET_CAPABILITY_VALIDATED برای بلاک کردن request استفاده نمی‌کنیم.
             * چون در اینترنت داخلی/سازمانی/ملی ممکن است شبکه فعال باشد ولی validated=false شود.
             */
            if (!hasActiveNetwork()) {
                Timber.tag(TAG).e(
                    "API request blocked | reason=no_active_network | network=[$networkSnapshot]"
                )

                emit(
                    Resource.error(
                        error = MsaError(
                            code = ErrorCode.NETWORK_NOT_AVAILABLE,
                            message = "هیچ شبکه فعالی روی دستگاه پیدا نشد"
                        )
                    )
                )
                return@flow
            }

            val response = api.invoke()
            val durationMs = SystemClock.elapsedRealtime() - startedAt
            val request = response.raw().request
            val url = sanitizeUrl(request.url.toString())
            val method = request.method
            val code = response.code()

            Timber.tag(TAG).d(
                "API response received | method=$method | url=$url | code=$code | success=${response.isSuccessful} | duration=${durationMs}ms"
            )

            val body = response.body()

            if (response.isSuccessful && body != null) {
                Timber.tag(TAG).d(
                    "API success | method=$method | url=$url | code=$code | duration=${durationMs}ms"
                )

                emit(Resource.success(body))
            } else {
                val rawError = response.errorBody()?.string()
                val serverMessage = parseErrorMessage(rawError)

                val message = serverMessage
                    ?: response.message().takeIf { it.isNotBlank() }
                    ?: "خطا در ارتباط با سرور"

                Timber.tag(TAG).e(
                    "API http error | method=$method | url=$url | code=$code | message=$message | rawError=${rawError.orEmpty().take(MAX_ERROR_LOG_LENGTH)} | duration=${durationMs}ms"
                )

                emit(
                    Resource.error(
                        error = MsaError(
                            code = code,
                            message = message
                        )
                    )
                )
            }
        }
            .flowOn(Dispatchers.IO)
            .catch { throwable ->
                val message = throwable.toUserMessage()
                val code = throwable.toErrorCode()

                Timber.tag(TAG).e(
                    throwable,
                    "API exception | code=$code | message=$message | network=[${getNetworkSnapshot()}]"
                )

                emit(
                    Resource.error(
                        error = MsaError(
                            code = code,
                            message = message
                        )
                    )
                )
            }
    }

    private fun hasActiveNetwork(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.activeNetwork != null
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.isConnected == true
        }
    }

    private fun getNetworkSnapshot(): String {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return "connectivity_manager=null"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork
                ?: return "active_network=null"

            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                ?: return "capabilities=null"

            val transports = mutableListOf<String>()

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                transports.add("wifi")
            }

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                transports.add("cellular")
            }

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                transports.add("ethernet")
            }

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                transports.add("vpn")
            }

            val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            val isCaptivePortal = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)

            "transports=${transports.joinToString().ifBlank { "unknown" }}, hasInternet=$hasInternet, validated=$isValidated, captivePortal=$isCaptivePortal"
        } else {
            @Suppress("DEPRECATION")
            val info = connectivityManager.activeNetworkInfo

            "type=${info?.typeName}, connected=${info?.isConnected}, available=${info?.isAvailable}"
        }
    }

    private fun parseErrorMessage(rawError: String?): String? {
        if (rawError.isNullOrBlank()) return null

        return runCatching {
            val json = JSONObject(rawError)

            json.optString("message")
                .ifBlank { json.optString("error") }
                .ifBlank { json.optString("detail") }
        }.getOrNull()?.takeIf { it.isNotBlank() }
    }

    private fun Throwable.toUserMessage(): String {
        return when (this) {
            is UnknownHostException -> {
                "آدرس سرور پیدا نشد. اتصال شبکه، DNS یا آدرس سرور را بررسی کنید"
            }

            is ConnectException -> {
                "اتصال به سرور برقرار نشد. ممکن است سرور در دسترس نباشد یا شبکه به آن مسیر نداشته باشد"
            }

            is SocketTimeoutException -> {
                "زمان انتظار اتصال به سرور تمام شد"
            }

            is SSLException -> {
                "خطای امنیتی در اتصال به سرور رخ داد"
            }

            is IOException -> {
                "خطای شبکه رخ داد. اتصال داخلی یا دسترسی به سرور را بررسی کنید"
            }

            else -> {
                message ?: "خطای نامشخص رخ داد"
            }
        }
    }

    private fun Throwable.toErrorCode(): Int {
        return when (this) {
            is UnknownHostException,
            is ConnectException,
            is SocketTimeoutException,
            is IOException -> ErrorCode.NETWORK_NOT_AVAILABLE

            else -> ErrorCode.CATCH_BODY
        }
    }

    private fun sanitizeUrl(url: String): String {
        /*
         * برای امنیت، query string را لاگ نمی‌کنیم.
         * چون ممکن است token، username یا داده حساس داخل query باشد.
         */
        return url.substringBefore("?")
    }

    companion object {
        private const val TAG = "NetworkUtils"
        private const val MAX_ERROR_LOG_LENGTH = 500
    }
}