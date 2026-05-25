package com.msa.eshop.utils.result

import com.msa.eshop.data.remote.utills.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

fun <T> makeRequest(
    scope: CoroutineScope,
    request: suspend () -> kotlinx.coroutines.flow.Flow<Resource<T>>,
    onSuccess: (T) -> Unit,
    updateStateLoading: (Boolean) -> Unit,
    updateStateError: (String?) -> Unit
) {
    scope.launch {
        request().collectLatest { response ->
            when (response.status) {
                Resource.Status.SUCCESS -> {
                    updateStateLoading(false)
                    response.data?.let(onSuccess)
                }

                Resource.Status.LOADING -> {
                    updateStateLoading(true)
                }

                Resource.Status.ERROR -> {
                    val message = response.error?.message ?: "خطایی رخ داد"
                    Timber.tag("NetworkUtils").e(message)
                    updateStateLoading(false)
                    updateStateError(message)
                }
            }
        }
    }
}