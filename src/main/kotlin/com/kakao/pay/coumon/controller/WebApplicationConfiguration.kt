package com.kakao.pay.coumon.controller

import com.kakao.pay.coumon.interceptor.JwtRequestInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
class WebApplicationConfiguration : WebMvcConfigurer {

    @Autowired
    private lateinit var jwtRequestInterceptor: JwtRequestInterceptor

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(jwtRequestInterceptor)
                .addPathPatterns("/api/coupon/**")
                .excludePathPatterns("/api/customer/**", "/admin/api/**")
    }
}