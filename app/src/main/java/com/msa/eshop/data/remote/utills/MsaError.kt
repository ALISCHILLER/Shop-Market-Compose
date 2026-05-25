package com.msa.eshop.data.remote.utills

import okhttp3.ResponseBody

data class MsaError(
    val code: Int = ErrorCode.UNKNOWN,
    val message: String? = null,
    val dataError: ResponseBody? = null,
    val rawError: String? = null,
    val url: String? = null,
    val method: String? = null
) {
    val safeMessage: String
        get() = message?.takeIf { it.isNotBlank() } ?: "خطای نامشخص رخ داد"

    val isNetworkError: Boolean
        get() = ErrorCode.isNetworkError(code)

    val isAuthError: Boolean
        get() = ErrorCode.isAuthError(code)

    val isServerError: Boolean
        get() = ErrorCode.isServerError(code)
}