package com.kakao.pay.coumon.coupon

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.time.LocalDate
import java.time.LocalDateTime


@RunWith(SpringRunner::class)
@SpringBootTest
class CouponServiceTest {

    @Autowired
    private lateinit var couponRepository: CouponRepository

    @Autowired
    private lateinit var couponService: CouponService

    @Before
    fun before() {
        couponRepository.saveAll(
                listOf(
                        Coupon(
                                delFlag = false,
                                used = false,
                                customerId = 1,
                                createdAt = LocalDateTime.now(),
                                expiredAt = LocalDate.now().minusDays(3)
                        ),
                        Coupon(
                                delFlag = false,
                                used = false,
                                customerId = 999,
                                createdAt = LocalDateTime.now(),
                                expiredAt = LocalDate.now().minusDays(3)
                        ),
                        Coupon(
                                delFlag = false,
                                used = false,
                                customerId = 333,
                                createdAt = LocalDateTime.now(),
                                expiredAt = LocalDate.now().minusDays(3)
                        ),
                        Coupon(
                                delFlag = false,
                                used = false,
                                customerId = 1,
                                createdAt = LocalDateTime.now(),
                                expiredAt = LocalDate.now().minusDays(3)
                        )
                ))
    }


    @Test
    fun `3일 후 만료되는 쿠폰 테스트`() {
        //단순히 프린트라.. 프린트 되는지만 확인.
        couponService.notifyExpiredBefore3days()
    }



//    @Test
    fun `200만개 생성한 쿠폰을 db에 업로드 테스트`() {
        val SAMPLE_CSV_FILE = "./coupon.csv"
        val initialFile = File(SAMPLE_CSV_FILE)
        val targetStream: InputStream = FileInputStream(initialFile)
        couponService.uploadCsv(targetStream)
    }

}