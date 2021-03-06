package com.kakao.pay.coumon.authentication

data class JwtPayload(
        val loginId: String,
        val password: String,
        val customerId: Long,
        val issueType: String // system, api , user
)