package com.kakao.pay.coumon.controller

import com.kakao.pay.coumon.filter.JwtRequestFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebApplicationConfiguration {

    @Autowired
    private lateinit var jwtRequestFilter: JwtRequestFilter

    @Bean
    fun jwtFilter(): FilterRegistrationBean<*> {
        val registrationBean = FilterRegistrationBean(jwtRequestFilter)
        registrationBean.order = 1
        return registrationBean
    }
}