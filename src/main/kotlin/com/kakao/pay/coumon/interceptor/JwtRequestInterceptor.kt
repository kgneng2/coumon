package com.kakao.pay.coumon.interceptor

import com.kakao.pay.coumon.authentication.JwtService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val log = KotlinLogging.logger { }

@Component
class JwtRequestInterceptor : HandlerInterceptor {

    @Autowired
    private lateinit var jwtService: JwtService

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val header = request.getHeader("Authorization")
        val token = jwtService.getTokenFromHeader(header)
        log.info("token1: $token")
        return jwtService.verify(token)
    }
}