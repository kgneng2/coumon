package com.kakao.pay.coumon.exception

import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.LocalDateTime


private val log = KotlinLogging.logger { }

@ControllerAdvice
class ProblemExceptionHandler {
    @ExceptionHandler(InvalidRequestException::class)
    fun invaildRequestExceptionHandler(ex: InvalidRequestException): ResponseEntity<CoumonErrorResponse> {
        log.error(ex.message, ex.cause)
        val code = 400
        return ResponseEntity
                .status(code)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.createErrorResponse(code))
    }


    @ExceptionHandler(NotFoundException::class)
    fun notFoundExceptionError(ex: NotFoundException): ResponseEntity<CoumonErrorResponse> {
        log.error(ex.message, ex.cause)
        val code = 404
        return ResponseEntity
                .status(code)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.createErrorResponse(code))
    }

    @ExceptionHandler(InternalServerException::class)
    fun notFoundExceptionError(ex: InternalServerException): ResponseEntity<CoumonErrorResponse> {
        log.error(ex.message, ex.cause)
        val code = 500
        return ResponseEntity
                .status(code)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.createErrorResponse(code))
    }

    @ExceptionHandler(AuthenticationException::class)
    fun authenticationException(ex: AuthenticationException): ResponseEntity<CoumonErrorResponse> {
        log.error(ex.message, ex.cause)
        val code = 401
        return ResponseEntity
                .status(code)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.createErrorResponse(code))
    }

    private fun CoumonException.createErrorResponse(code : Int) =
            CoumonErrorResponse(this::class.java.simpleName,
                    message = this.localizedMessage,
                    code = code,
                    detailMessage = this.message,
                    time = LocalDateTime.now())
}