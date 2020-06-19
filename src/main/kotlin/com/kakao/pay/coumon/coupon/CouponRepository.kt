package com.kakao.pay.coumon.coupon

import org.springframework.data.jpa.repository.JpaRepository

interface CouponRepository : JpaRepository<Coupon, String> {
    fun findExpiredCoupon()
}