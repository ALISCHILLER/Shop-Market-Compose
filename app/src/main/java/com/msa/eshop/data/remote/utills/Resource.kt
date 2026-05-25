package com.msa.eshop.data.remote.utills

import okhttp3.ResponseBody

data class Resource<out T>(
    val status: Status,
    val data: T? = null,
    val error: MsaError? = null,
    val errorBody: ResponseBody? = null
) {
    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource(
                status = Status.SUCCESS,
                data = data
            )
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(
                status = Status.LOADING,
                data = data
            )
        }

        fun <T> error(
            data: T? = null,
            error: MsaError? = null,
            errorBody: ResponseBody? = null
        ): Resource<T> {
            return Resource(
                status = Status.ERROR,
                data = data,
                error = error,
                errorBody = errorBody
            )
        }
    }

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }
}