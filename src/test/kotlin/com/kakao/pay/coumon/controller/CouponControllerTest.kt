package com.kakao.pay.coumon.controller

import io.restassured.RestAssured.given
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CouponControllerTest {

    @LocalServerPort
    private val port: Int = 0

    private val url = "/api/coupon"


    @Test
    fun createNcoupon() {
        val body = mapOf("count" to 5)
        println(body)

        given()
                .log().all()
                .port(port)
                .header("Content-Type", "application/json")
                .body(body)
                .`when`()
                .post(url)
                .prettyPeek()
                .then()
                .assertThat()
                .statusCode(201)

    }


}