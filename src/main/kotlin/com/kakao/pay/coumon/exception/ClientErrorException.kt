package com.kakao.pay.coumon.exception

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

class InvalidRequestException(message: String? = "Invalid Request", cause: Throwable? = null) : CoumonException(message, cause)
class NotFoundException(message: String? = "NotFound Request", cause: Throwable? = null) : CoumonException(message, cause)


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class CoumonErrorResponse(
        val exception: String,
        val code : Int,
        val message: String? = null,
        val detailMessage : String? = null,
        val time: LocalDateTime = LocalDateTime.now()
)