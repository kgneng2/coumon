package com.kakao.pay.coumon.coupon

import com.kakao.pay.coumon.exception.InternalServerException
import com.kakao.pay.coumon.exception.InvalidRequestException
import com.kakao.pay.coumon.exception.NotFoundException
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

private val log = KotlinLogging.logger {  }

@Service
class CouponService {

    @Autowired
    private lateinit var couponRepository: CouponRepository

    fun create(count: Int) {
        val list = mutableListOf<Coupon>()

        repeat(count) {
            val coupon = Coupon(createdAt = LocalDateTime.now())
            list.add(coupon)
        }

        couponRepository.saveAll(list)
    }

    fun assign(customerId: Long): Coupon {
        val coupon = couponRepository.findFirstByCustomerIdAndDelFlagAndUsed()
        if (coupon == null) {
            throw InternalServerException("No coupon available")
        } else {
            coupon.customerId = customerId
            coupon.expiredAt = LocalDate.now().plusDays(90L)

            return coupon
        }
    }

    fun getList(customerId: Long): List<Coupon> {
        return couponRepository.findByCustomerIdAndDelFlagOrderByExpiredAtDesc(customerId, false)
    }

    fun use(id: String, customerId: Long): Coupon {
        val coupon = findCouponWithOwner(id, customerId)

        if (coupon.used) {
            throw InvalidRequestException("$id is used")
        } else {
            val copy = coupon.copy(used = true)
            return couponRepository.save(copy)
        }
    }

    fun cancel(id: String, customerId: Long): Coupon {
        val coupon = findCouponWithOwner(id, customerId)

        if (coupon.used) {
            val copy = coupon.copy(used = false)
            return couponRepository.save(copy)
        } else {
            throw InvalidRequestException("$id is unused")
        }
    }

    private fun findCouponWithOwner(couponNumber: String, customerId: Long): Coupon {
        return couponRepository.findByIdAndCustomerIdAndDelFlagOrderByExpiredAtDesc(
                UUID.fromString(couponNumber),
                customerId,
                false) ?: throw NotFoundException("There is no customer($customerId)'s coupon $couponNumber(coupon number)")
    }

    fun expiredByDate(date : LocalDate): List<Coupon> {
        return couponRepository.findByExpiredAtAndDelFlagAndUsed(
                date = date,
                delFlag = false,
                used = false)
    }

    //7 매10시에 만료 3일전 쿠폰을 확인한다.
    @Scheduled(cron = "0 10 * * *")
    fun notifyExpiredBefore3days() {
        val expiredCoupons = expiredByDate(LocalDate.now().minusDays(3))

        expiredCoupons.groupBy { coupon: Coupon -> coupon.customerId }
                .forEach { (k,v) ->
                    val couponIdList = v.map { it.id }
                    log.info("$k 의 쿠폰이 3일후 만료 됩니다 $couponIdList")
                }
    }
}