package com.kakao.pay.coumon.coupon

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class CouponService {

    fun create(count: Int?) {
        TODO("랜덤한 쿠폰 N개 생성하기")
    }

    fun get() {
        TODO("//output : 쿠폰번호(XXXXX-XXXXXX-XXXXXXXX)")
    }

    fun getList(userId: String): List<Coupon> {
        TODO("사용자에게 지급된 쿠폰을 조회하는 API를 구현하세요.")
    }

    fun use(block: String) {
        TODO("Not yet implemented")
    }

    fun cancel(couponId: String) {
        TODO("Not yet implemented")
    }


    @Scheduled
    fun notifyExpired() {


    }

}