package com.kakao.pay.coumon.coupon

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.util.*

interface CouponRepository : JpaRepository<Coupon, String> {
    fun findByCustomerIdAndDelFlagOrderByExpiredAtDesc(customerId: Long, delFlag: Boolean) : List<Coupon>
    fun findByExpiredAtAndDelFlagAndUsed(date: LocalDate, delFlag: Boolean, used : Boolean) : List<Coupon>
    fun findFirstByCustomerIdAndDelFlagAndUsed(customerId : Long? = null, delFlag: Boolean = false, used: Boolean = false) : Coupon?
    fun findByIdAndCustomerIdAndDelFlagOrderByExpiredAtDesc(id: UUID, customerId: Long, delFlag: Boolean): Coupon?
}