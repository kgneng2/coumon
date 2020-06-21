package com.kakao.pay.coumon

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class CoumonApplication

fun main(args: Array<String>) {
    runApplication<CoumonApplication>(*args)
}
