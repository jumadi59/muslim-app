package com.jumbox.app.muslim.utils

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class StringConverterFactory : Converter.Factory() {


    override fun responseBodyConverter(
            type: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return if (String::class.java == type) {
            Converter<ResponseBody, String> { value -> value.string() }
        } else null
    }

    override fun requestBodyConverter(
            type: Type,
            parameterAnnotations: Array<Annotation>,
            methodAnnotations: Array<Annotation>,
            retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        return if (String::class.java == type) {
            Converter<String, RequestBody> { value -> value.toRequestBody("application/json".toMediaTypeOrNull()) }
        } else null
    }

}
