package com.kakao.pay.coumon.authentication

import com.auth0.jwt.JWTSigner
import com.auth0.jwt.JWTVerifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class JwtConfiguration(
        @Value("\${secret.key}")
        val secretKey: String
) {

    @Bean
    fun jwtSigner(): JWTSigner {
        return JWTSigner(
                Base64.getEncoder().encodeToString(secretKey.toByteArray()))
    }

    @Bean
    fun jwtVerifier(): JWTVerifier {
        return JWTVerifier(Base64.getEncoder().encodeToString(secretKey.toByteArray()))
    }
}
