package com.msa.eshop.data.remote.utills

object ErrorCode {

    const val UNKNOWN = -1

    const val NETWORK_NOT_AVAILABLE = 1001
    const val NETWORK_CONNECTION_FAILED = 1002
    const val NETWORK_TIMEOUT = 1003
    const val NETWORK_DNS_FAILED = 1004
    const val NETWORK_SSL_FAILED = 1005

    const val NULL_RESPONSE_BODY = 2001
    const val CATCH_BODY = 2002
    const val PARSE_ERROR_BODY_FAILED = 2003

    const val HTTP_BAD_REQUEST = 400
    const val HTTP_UNAUTHORIZED = 401
    const val HTTP_FORBIDDEN = 403
    const val HTTP_NOT_FOUND = 404
    const val HTTP_CONFLICT = 409
    const val HTTP_UNPROCESSABLE = 422
    const val HTTP_SERVER_ERROR = 500

    fun isNetworkError(code: Int): Boolean {
        return code in setOf(
            NETWORK_NOT_AVAILABLE,
            NETWORK_CONNECTION_FAILED,
            NETWORK_TIMEOUT,
            NETWORK_DNS_FAILED,
            NETWORK_SSL_FAILED
        )
    }

    fun isAuthError(code: Int): Boolean {
        return code == HTTP_UNAUTHORIZED || code == HTTP_FORBIDDEN
    }

    fun isServerError(code: Int): Boolean {
        return code >= 500
    }
}