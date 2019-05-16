package com.diraj.mynews.network

import android.util.Log
import retrofit2.Response

sealed class ApiResponse<T> {
    companion object {
        fun <T> create(throwable: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(throwable.localizedMessage ?: "Error")
        }

        fun <T> create(response: Response<T>): ApiResponse<T> {
            return if (response.isSuccessful) {
                val body = response.body()
                if (body == null || response.code() == 204) {
                    ApiEmptyResponse()
                } else {
                    Log.d("diraj", response.raw().request().url().toString())
                    ApiSuccessResponse(
                        body = body
                    )
                }
            } else {
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                ApiErrorResponse(errorMsg ?: "unknown error")
            }
        }
    }
}

class ApiEmptyResponse<T> : ApiResponse<T>()

data class ApiErrorResponse<T>(val errorMessage: String) : ApiResponse<T>()

class ApiSuccessResponse<T>(val body: T) : ApiResponse<T>()