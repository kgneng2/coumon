package com.kakao.pay.coumon.coupon

data class Coupon(
        val couponId : String,
        val couponName : String,
        val delFlag :Boolean = false,
        val use : Boolean = false,
        val userId : String
)