package com.kakao.pay.coumon.controller

import com.kakao.pay.coumon.coupon.Coupon
import com.kakao.pay.coumon.coupon.CouponService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/coupon")
class CouponController {

    @Autowired
    private lateinit var couponService: CouponService

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun random(@RequestBody counts: Map<String, Int>) {
        val count = counts["count"]

        return couponService.create(count)
    }

    @GetMapping
    fun getCoupon() {

        return couponService.get()
    }

    @GetMapping("/user/{userId}")
    fun getList(@PathVariable("userId") userId : String ) : List<Coupon> {
        return couponService.getList(userId)
    }

    @PutMapping("/{couponId}")
    fun use(@PathVariable("couponId") couponId : String) {
        //NOTE : 재사용 불가
        return couponService.use(couponId)
    }

    @DeleteMapping("/{couponId}")
    fun cancel(@PathVariable("couponId") couponId : String) {
        //NOTE : 재사용 불가
        return couponService.cancel(couponId)
    }


    @GetMapping("/expiration")
    fun getExpiredCouponList() : List<Coupon> {
        TODO("Pagination ")
    }

}