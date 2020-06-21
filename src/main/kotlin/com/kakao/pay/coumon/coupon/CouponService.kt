package com.kakao.pay.coumon.coupon

import com.kakao.pay.coumon.exception.InternalServerException
import com.kakao.pay.coumon.exception.InvalidRequestException
import com.kakao.pay.coumon.exception.NotFoundException
import com.kakao.pay.coumon.util.getMapper
import com.kakao.pay.coumon.util.toJsonAsBytes
import com.leansoft.bigqueue.BigQueueImpl
import com.leansoft.bigqueue.IBigQueue
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

private val log = KotlinLogging.logger { }

@Service
class CouponService {

    @Autowired
    private lateinit var couponRepository: CouponRepository

    @Value("\${big-queue.dir}")
    private val bigQueueDir: String? = null

    private lateinit var queue: IBigQueue

    private final val BATCH_SIZE = 10000

    @PostConstruct
    @Throws(IOException::class)
    fun init() {
        queue = BigQueueImpl(bigQueueDir, "coupon")
    }

    fun create(count: Int) {
        try {
            repeat(count) {
                val coupon = Coupon(createdAt = LocalDateTime.now())
                queue.enqueue(coupon.toJsonAsBytes())
            }
        } catch (e: IOException) {
            log.error(e.toString(), e)
            throw InternalServerException()
        }
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
                false)
                ?: throw NotFoundException("There is no customer($customerId)'s coupon $couponNumber(coupon number)")
    }

    fun expiredByDate(date: LocalDate): List<Coupon> {
        return couponRepository.findByExpiredAtAndDelFlagAndUsed(
                date = date,
                delFlag = false,
                used = false)
    }

    //7 매10시에 만료 3일전 쿠폰을 확인한다.
    @Scheduled(cron = "0 10 * * * *")
    fun notifyExpiredBefore3days() {
        val expiredCoupons = expiredByDate(LocalDate.now().minusDays(3))

        expiredCoupons.groupBy { coupon: Coupon -> coupon.customerId }
                .forEach { (k, v) ->
                    val couponIdList = v.map { it.id }
                    log.info("$k 의 쿠폰이 3일후 만료 됩니다 $couponIdList")
                }
    }

    @Throws(IOException::class)
    @Scheduled(fixedDelay = 1000)
    fun insertCoupon() {
        val count = queue.size()

        if (count != 0L) {
            log.info("There is $count coupon in queue")
        }

        if(count > BATCH_SIZE) {
            repeat(BATCH_SIZE) {
                dequeue()?.let { couponRepository.save(it) }
            }
        } else {
            repeat(count.toInt()) {
                dequeue()?.let { couponRepository.save(it) }
            }
        }
    }

    @Throws(IOException::class)
    private fun dequeue(): Coupon? {
        val bytes = queue.dequeue()

        return getMapper().readValue(bytes, Coupon::class.java)
    }


    @PreDestroy
    @Throws(IOException::class)
    fun preDestory() {
        log.info("destory queue")
        queue.flush()
        queue.gc()
        queue.close()
    }
}