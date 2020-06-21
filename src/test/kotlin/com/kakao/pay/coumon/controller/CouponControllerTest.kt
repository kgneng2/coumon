package com.kakao.pay.coumon.controller

import com.kakao.pay.coumon.authentication.ApiToken
import com.kakao.pay.coumon.coupon.Coupon
import com.kakao.pay.coumon.coupon.CouponRepository
import com.kakao.pay.coumon.customer.Customer
import com.kakao.pay.coumon.customer.CustomerService
import io.restassured.RestAssured.given
import mu.KotlinLogging
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasItem
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate
import java.time.LocalDateTime
import javax.transaction.Transactional

private val log = KotlinLogging.logger { }

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CouponControllerTest {

    @LocalServerPort
    private val port: Int = 0
    private val url = "/api/coupon"

    @Autowired
    private lateinit var couponRepository: CouponRepository

    @Autowired
    private lateinit var customerService: CustomerService

    fun before(loginId : String): Pair<Customer, ApiToken> {
        log.info("signup")

        val user = Customer(
                customerId = null,
                loginId = loginId,
                password = "1234",
                createdAt = LocalDateTime.now()
        )

        val create = customerService.create(user)
        log.info("signin")
        val apiToken = customerService.login(user)

        return Pair(create, apiToken)
    }

    @Test
    fun `랜덤한 코드의 쿠폰을 N개 생성하여 데이터베이스에 보관하는 API`() {
        val body = mapOf("count" to 5)
        given()
                .log().all()
                .port(port)
                .header("Content-Type", "application/json")
                .body(body)
                .`when`()
                .post("/admin/api/coupon")
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(201)
    }


    @Test
    fun `생성된 쿠폰중 하나를 사용자에게 지급하는 API`() {
        val (customer, apiToken) = before("id1")
        val coupon = couponRepository.save(Coupon(createdAt = LocalDateTime.now()))

        given()
                .log().all()
                .port(port)
                .header("Authorization", "Bearer ${apiToken.token}")
                .`when`()
                .get(url)
                .prettyPeek()
                .then()
                .assertThat()
                .body("couponNumber", equalTo(coupon.id.toString()))
                .body("customerId", equalTo(customer.customerId?.toInt()))
                .statusCode(200)

    }

    @Test
    fun `사용자에게 지급된 쿠폰을 조회하는 API`() {
        val (customer, apiToken) = before("id2")

        val coupon = couponRepository.save(
                Coupon(
                        delFlag = false,
                        used = false,
                        customerId = customer.customerId,
                        createdAt = LocalDateTime.now()
                ))

        given()
                .log().all()
                .port(port)
                .header("Authorization", "Bearer ${apiToken.token}")
                .`when`()
                .get("$url/user/${customer.customerId}")
                .prettyPeek()
                .then()
                .assertThat()
                .body("size()", `is`(1))
                .body("couponNumber", hasItem(coupon.id.toString()))
                .body("customerId", hasItem(coupon.customerId?.toInt()))
                .statusCode(200)
    }

    @Test
    fun `지급된 쿠폰중 하나를 사용하는 API`() {
        val (customer, apiToken) = before("id3")

        val coupon = couponRepository.save(
                Coupon(
                        delFlag = false,
                        used = false,
                        customerId = customer.customerId,
                        createdAt = LocalDateTime.now()
                ))

        given()
                .log().all()
                .port(port)
                .header("Authorization", "Bearer ${apiToken.token}")
                .`when`()
                .put("$url/${coupon.id}")
                .prettyPeek()
                .then()
                .assertThat()
                .body("used", equalTo(true))
                .statusCode(200)
    }

    @Test
    fun `지급된 쿠폰중 하나를 사용하지만 사용됨`() {
        val (customer, apiToken) = before("id4")

        val coupon = couponRepository.save(
                Coupon(
                        delFlag = false,
                        used = true,
                        customerId = customer.customerId,
                        createdAt = LocalDateTime.now()
                ))

        given()
                .log().all()
                .port(port)
                .header("Authorization", "Bearer ${apiToken.token}")
                .`when`()
                .put("$url/${coupon.id}")
                .prettyPeek()
                .then()
                .assertThat()
                .body("exception", equalTo("InvalidRequestException"))
                .statusCode(400)
    }

    @Test
    fun `지급된 쿠폰중 하나를 사용 취소하는 API`() {
        val (customer, apiToken) = before("id5")

        val coupon = couponRepository.save(
                Coupon(
                        delFlag = false,
                        used = true,
                        customerId = customer.customerId,
                        createdAt = LocalDateTime.now()
                ))

        given()
                .log().all()
                .port(port)
                .header("Authorization", "Bearer ${apiToken.token}")
                .`when`()
                .delete("$url/${coupon.id}")
                .prettyPeek()
                .then()
                .assertThat()
                .body("used", equalTo(false))
                .statusCode(200)
    }

    @Test
    fun `내꺼 아닌 쿠폰 사용하다 걸림`() {
        val (customer, apiToken) = before("id6")

        val coupon = couponRepository.save(
                Coupon(
                        delFlag = false,
                        used = false,
                        customerId = 9999L,
                        createdAt = LocalDateTime.now()
                ))

        given()
                .log().all()
                .port(port)
                .header("Authorization", "Bearer ${apiToken.token}")
                .`when`()
                .put("$url/${coupon.id}")
                .prettyPeek()
                .then()
                .assertThat()
                .body("exception", equalTo("NotFoundException"))
                .statusCode(404)
    }

    @Test
    fun `발급된 쿠폰중 당일 만료된 전체 쿠폰 목록`() {
        val coupons = couponRepository.saveAll(
                listOf(
                        Coupon(
                                delFlag = false,
                                used = false,
                                customerId = 1,
                                createdAt = LocalDateTime.now(),
                                expiredAt = LocalDate.now()
                        ),
                        Coupon(
                                delFlag = false,
                                used = false,
                                customerId = 999,
                                createdAt = LocalDateTime.now(),
                                expiredAt = LocalDate.now()
                        ),
                        Coupon(
                                delFlag = false,
                                used = false,
                                customerId = 333,
                                createdAt = LocalDateTime.now(),
                                expiredAt = LocalDate.now()
                        ),
                        Coupon(
                                delFlag = false,
                                used = false,
                                customerId = 1,
                                createdAt = LocalDateTime.now()
                        )
                ))


        given()
                .log().all()
                .port(port)
                .`when`()
                .get("/admin/api/coupon/expiration")
                .prettyPeek()
                .then()
                .assertThat()
                .body("size()", `is`(3))
                .statusCode(200)
    }

    @Test
    fun `토큰 없이 보냄`() {
        given()
                .log().all()
                .port(port)
                .`when`()
                .get(url)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(401)
    }

    @Test
    fun `잘못된 토큰 값으로 요청`() {
        given()
                .log().all()
                .port(port)
                .header("Authorization", "Bearer error")
                .`when`()
                .get(url)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(401)
    }
}

