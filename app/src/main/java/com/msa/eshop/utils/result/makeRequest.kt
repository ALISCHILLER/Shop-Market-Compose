package com.msa.eshop.utils.result

import com.msa.eshop.data.remote.utills.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

fun <T> makeRequest(
    scope: CoroutineScope,
    request: suspend () -> kotlinx.coroutines.flow.Flow<Resource<T>>,
    onSuccess: (T) -> Unit,
    updateStateLoading: (Boolean) -> Unit,
    updateStateError: (String?) -> Unit
): Job {
    return scope.launch {
        runCatching {
            request().collectLatest { response ->
                when (response.status) {
                    Resource.Status.SUCCESS -> {
                        updateStateLoading(false)

                        val data = response.data
                        if (data != null) {
                            onSuccess(data)
                        } else {
                            val message = "پاسخ سرور خالی است"
                            Timber.tag(TAG).e(message)
                            updateStateError(message)
                        }
                    }

                    Resource.Status.LOADING -> {
                        updateStateLoading(true)
                    }

                    Resource.Status.ERROR -> {
                        val message = response.error?.safeMessage
                            ?: response.error?.message
                            ?: "خطایی رخ داد"

                        Timber.tag(TAG).e(
                            "Request failed | code=${response.error?.code} | message=$message"
                        )

                        updateStateLoading(false)
                        updateStateError(message)
                    }
                }
            }
        }.onFailure { throwable ->
            Timber.tag(TAG).e(throwable, "makeRequest exception")

            updateStateLoading(false)
            updateStateError(throwable.message ?: "خطای نامشخص رخ داد")
        }
    }
}

private const val TAG = "NetworkUtils"