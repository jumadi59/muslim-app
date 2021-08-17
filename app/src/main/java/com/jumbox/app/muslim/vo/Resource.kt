package com.jumbox.app.muslim.vo

/**
 * Created by Jumadi date on 29/07/20
 * Bengkulu, Indonesia
 */

data class Resource<out T>(
    val status: Status,
    val statusCode: Int = 0,
    val data: T?,
    val message: String? = null) {

    companion object {

        fun <T> success(data: T?, message: String? = null): Resource<T> = Resource(status = Status.SUCCESS, data =data, message = if (message.isNullOrEmpty()) null else message)

        fun <T> error(msg: String?, statusCode: Int = 0, data: T? = null): Resource<T> =
            Resource(status = Status.ERROR, statusCode = statusCode, data =data, message = if (msg.isNullOrEmpty()) null else msg)

        fun <T> loading(data: T? = null): Resource<T> = Resource(status = Status.LOADING, data = data)

    }
}