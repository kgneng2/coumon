package com.kakao.pay.coumon.controller

import com.kakao.pay.coumon.coupon.Coupon
import com.kakao.pay.coumon.coupon.CouponService
import com.kakao.pay.coumon.exception.InvalidRequestException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/coupon")
class CouponController {

    @Autowired
    private lateinit var couponService: CouponService

    //1
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody counts: Map<String, Int>) {
        val count = counts["count"]

        if (count == null) {
            throw InvalidRequestException("count is null")
        } else {
            couponService.create(count)
        }
    }

    //2
    @GetMapping
    fun getCoupon() {

        return couponService.get()
    }

    //3
    @GetMapping("/user/{customerId}")
    fun getList(@PathVariable("customerId") customerId: String): List<Coupon> {
        return couponService.getList(customerId)
    }

    //4
    @PutMapping("/{couponNumber}")
    fun use(@PathVariable("couponNumber") couponNumber: String): Coupon {
        return couponService.use(couponNumber)
    }

    //5
    @DeleteMapping("/{couponNumber}")
    fun cancel(@PathVariable("couponNumber") couponNumber: String): Coupon {
        return couponService.cancel(couponNumber)
    }


    //6
    @GetMapping("/expiration")
    fun getExpiredCouponList(): List<Coupon> {
        return couponService.expiredToday()
    }

}