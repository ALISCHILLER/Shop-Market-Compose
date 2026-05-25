package com.msa.eshop.data.remote.utills

import okhttp3.ResponseBody

data class Resource<out T>(
    val status: Status,
    val data: T? = null,
    val error: MsaError? = null,
    val errorBody: ResponseBody? = null
) {
    val isSuccess: Boolean
        get() = status == Status.SUCCESS

    val isLoading: Boolean
        get() = status == Status.LOADING

    val isError: Boolean
        get() = status == Status.ERROR

    val message: String?
        get() = error?.message

    fun dataOrThrow(): T {
        return data ?: throw IllegalStateException(error?.safeMessage ?: "Response data is null")
    }

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