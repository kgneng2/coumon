package com.kakao.pay.coumon.customer

import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository : JpaRepository<Customer, Long> {
    fun existsByLoginId(loginId: String) :Boolean
    fun findByLoginIdAndPassword(loginId :String, password : String) : Customer?

}