package com.kakao.pay.coumon.authentication

import com.auth0.jwt.JWTSigner
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kakao.pay.coumon.customer.Customer
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException
import java.util.*

object JwtComponent {
    private val secretKey : String = "coumon@secret@key@1234"
    private var jwtSigner: JWTSigner = JWTSigner(
            Base64.getEncoder().encodeToString(secretKey.toByteArray()))

    private val DEFAULT_EXPIRY_SECONDS = 0 //일단 무한
    private val mapper = jacksonObjectMapper()

    fun generateApiToken(customer: Customer): ApiToken {

        val payload = JwtPayload(customer.loginId,
                customer.customerId ?: throw InternalException("customer Id is null"),
                "user") // user 로 한정한다.

        return ApiToken(jwtSign(payload, DEFAULT_EXPIRY_SECONDS))
    }

    fun verify() {

    }

    private fun jwtSign(jwtPayload: JwtPayload, expirySecond: Int): String {
        val claims = mapper.convertValue(jwtPayload, Map::class.java).toMutableMap()
        val options = JWTSigner.Options()
                .setJwtId(true)
                .setIssuedAt(true)
                .setNotValidBeforeLeeway(60)
                .setExpirySeconds(if (expirySecond > 0) expirySecond else null)
        return jwtSigner.sign(claims as MutableMap<String, Any>?, options)
    }
}