package com.msa.eshop.utils.result

data class GeneralStateModel(
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null
) {
    val hasError: Boolean
        get() = !error.isNullOrBlank()

    val hasMessage: Boolean
        get() = !message.isNullOrBlank()

    val isIdle: Boolean
        get() = !isLoading && error == null && message == null
}