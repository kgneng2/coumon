package com.kakao.pay.coumon.controller

import com.kakao.pay.coumon.authentication.JwtService
import com.kakao.pay.coumon.coupon.Coupon
import com.kakao.pay.coumon.coupon.CouponService
import com.kakao.pay.coumon.exception.InvalidRequestException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
class CouponController {

    @Autowired
    private lateinit var couponService: CouponService

    @Autowired
    private lateinit var jwtService: JwtService

    //1 쿠폰 생성시 인증 pass
    @PostMapping("/admin/api/coupon")
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
    @GetMapping("/api/coupon")
    fun getCoupon(@RequestHeader("Authorization") header: String?): Coupon {
        val token = jwtService.getTokenFromHeader(header)
        val payload = jwtService.getPayload(token)

        return couponService.assign((payload["customerId"] as Int).toLong())
    }

    //3
    @GetMapping("/api/coupon/user/{customerId}")
    fun getList(@RequestHeader("Authorization") header: String?,
                @PathVariable("customerId") customerId: String): List<Coupon> {
        val token = jwtService.getTokenFromHeader(header)
        val payload = jwtService.getPayload(token)

        return couponService.getList((payload["customerId"] as Int).toLong())
    }

    //4
    @PutMapping("/api/coupon/{couponNumber}")
    fun use(@RequestHeader("Authorization") header: String?,
            @PathVariable("couponNumber") couponNumber: String): Coupon {
        val token = jwtService.getTokenFromHeader(header)
        val payload = jwtService.getPayload(token)

        return couponService.use(couponNumber, (payload["customerId"] as Int).toLong())
    }

    //5
    @DeleteMapping("/api/coupon/{couponNumber}")
    fun cancel(
            @RequestHeader("Authorization") header: String?,
            @PathVariable("couponNumber") couponNumber: String): Coupon {
        val token = jwtService.getTokenFromHeader(header)
        val payload = jwtService.getPayload(token)

        return couponService.cancel(couponNumber, (payload["customerId"] as Int).toLong())
    }

    //6 전체 쿠폰 목록이라, 인증 pass
    @GetMapping("/admin/api/coupon/expiration")
    fun getExpiredCouponList(): List<Coupon> {
        return couponService.expiredByDate(LocalDate.now())
    }

}