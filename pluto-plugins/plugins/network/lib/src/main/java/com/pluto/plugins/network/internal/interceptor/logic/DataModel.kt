package com.pluto.plugins.network.internal.interceptor.logic

import com.pluto.utilities.list.ListItem
import okhttp3.HttpUrl
import okhttp3.Protocol

internal data class RequestData(
    val url: HttpUrl,
    val method: String,
    val body: ProcessedBody?,
    val headers: Map<String, String?>,
    val timestamp: Long,
    val isGzipped: Boolean,
)

internal data class MockConfig(
    val url: String,
)

internal data class ResponseData(
    val status: Status,
    val isSuccessful: Boolean,
    val body: ProcessedBody?,
    val protocol: Protocol,
    val fromDiskCache: Boolean,
    val headers: Map<String, String?>,
    val sendTimeMillis: Long,
    val receiveTimeMillis: Long,
    val isGzipped: Boolean,
)

internal data class Status(
    val code: Int,
    val message: String,
)

internal class ApiCallData(
    val id: String,
    val request: RequestData,
    var hasResponseBody: Boolean = false,
    var response: ResponseData? = null,
    var exception: ExceptionData? = null,
    var mock: MockConfig? = null,
) : ListItem() {
    val curl: String
        get() = request.getCurl()
    val responseTime
        get() = exception?.timeStamp ?: response?.receiveTimeMillis

    override fun isEqual(other: Any): Boolean {
        if (other is ApiCallData) {
            id == other.id && response == other.response && exception == other.exception
        }
        return false
    }
}

internal data class ProcessedBody(
    val isValid: Boolean,
    val body: CharSequence,
    val mediaType: String,
    val mediaSubtype: String,
) {
    val isBinary: Boolean = mediaType == BINARY_MEDIA_TYPE
    val sizeInBytes: Long
        get() = body.length.toLong()
    val mediaTypeFull: String
        get() = "$mediaType/$mediaSubtype"
}
