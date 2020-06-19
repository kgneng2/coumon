package com.kakao.pay.coumon.coupon

import com.kakao.pay.coumon.exception.InvalidRequestException
import com.kakao.pay.coumon.exception.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

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

    fun get() {
        TODO("//output : 쿠폰번호(XXXXX-XXXXXX-XXXXXXXX)")
    }

    fun getList(customerId: String): List<Coupon> {
        return couponRepository.findByCustomerIdAndDelFlagOrderByUpdatedAt(customerId, false);
    }

    fun use(id: String): Coupon {
        //TODO: customerId도 확인해서 소유중인 쿠폰인지 확인하고 사용할것.
        val coupon = findById(id)

        if (coupon.used) {
            //재사용 불가
            throw InvalidRequestException("$id is used")
        } else {
            coupon.used = true
            return couponRepository.save(coupon)
        }
    }

    fun cancel(id: String): Coupon {
        val coupon = findById(id)

        if (coupon.used) {
            coupon.used = false
            return couponRepository.save(coupon)
        } else {
            //굳이(?)사용가능한 쿠폰인데 취소를 할수 있나?
            throw InvalidRequestException("$id is unused")
        }
    }

    private fun findById(couponNumber: String): Coupon {
        return couponRepository.findById(couponNumber).orElse(null)
                ?: throw NotFoundException("$couponNumber is not existed")
    }


    fun expiredToday(): List<Coupon> {
        return couponRepository.findByExpiredAtAndDelFlagAndUsed(LocalDate.now(), delFlag = false, used = false)
    }


    //7
    @Scheduled(cron = "0 10 * * *")
    fun notifyExpired() {
//        val expired =  couponRepository.findExpiredCoupon()

//        println("쿠폰이 3일 후 만료됩니다." + expired)/**/
    }

}