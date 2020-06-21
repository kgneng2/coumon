package com.kakao.pay.coumon.filter

import com.kakao.pay.coumon.authentication.JwtService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val log = KotlinLogging.logger {  }

@Component
class JwtRequestFilter : OncePerRequestFilter() {

    @Autowired
    private lateinit var jwtService: JwtService

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse,
                                  filterChain: FilterChain) {

        println(request.requestURI)
        println(request.method)

        if (request.requestURI == "/api/coupon" && request.method == "POST") {
            log.info("exclude generate coupon")
        } else {
            val header = request.getHeader("Authorization")
            val token = jwtService.getTokenFromHeader(header)

            jwtService.verify(token)
        }

        filterChain.doFilter(request, response)
    }
}