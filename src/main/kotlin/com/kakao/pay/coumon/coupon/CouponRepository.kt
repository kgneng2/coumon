package com.kakao.pay.coumon.coupon

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface CouponRepository : JpaRepository<Coupon, String> {
    fun findByCustomerIdAndDelFlagOrderByUpdatedAt(customerId: String, delFlag: Boolean) : List<Coupon>
    fun findByExpiredAtAndDelFlagAndUsed(date: LocalDate, delFlag: Boolean, used : Boolean) : List<Coupon>
}