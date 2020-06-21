package com.kakao.pay.coumon.authentication

import com.auth0.jwt.JWTSigner
import com.auth0.jwt.JWTVerifier
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kakao.pay.coumon.customer.Customer
import com.kakao.pay.coumon.customer.CustomerService
import com.kakao.pay.coumon.exception.AuthenticationException
import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class JwtService {

    @Autowired
    private lateinit var jwtSigner: JWTSigner

    @Autowired
    private lateinit var jwtVerifier: JWTVerifier

    @Autowired
    private lateinit var customerService: CustomerService

    private val DEFAULT_EXPIRY_SECONDS = 600
    //    private val DEFAULT_EXPIRY_SECONDS = 604800 // 7일

    private val mapper = jacksonObjectMapper()

    fun generateApiToken(customer: Customer): ApiToken {

        val payload = JwtPayload(customer.loginId,
                customer.customerId ?: throw InternalException("customer Id is null"),
                "user") // user 로 한정한다.

        return ApiToken(jwtSign(payload, DEFAULT_EXPIRY_SECONDS))
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

    @Throws(Exception::class)
    fun getPayload(token: String): MutableMap<String, Any> {
        return jwtVerifier.verify(token)
    }

    fun getTokenFromHeader(header: String): String {
        if(header.contains("Bearer")) {
            return header.split(" ")[1]
        } else {
            throw AuthenticationException("Authorization : Bearer token")
        }
    }

    fun verify(token: String) {
        try {
            val jwtPayload = getPayload(token)
            customerService.confirm(jwtPayload["loginId"] as String, jwtPayload["password"] as String)

        } catch (e: Exception) {
            throw AuthenticationException(e.toString(), e)
        }
    }
}