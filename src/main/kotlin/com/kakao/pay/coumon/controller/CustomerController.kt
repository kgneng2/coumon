package com.kakao.pay.coumon.controller

import com.kakao.pay.coumon.authentication.ApiToken
import com.kakao.pay.coumon.customer.Customer
import com.kakao.pay.coumon.customer.CustomerService
import com.kakao.pay.coumon.exception.InvalidRequestException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/customer")
class CustomerController {

    @Autowired
    private lateinit var customerService: CustomerService

    @PostMapping("/registration")
    fun signup(@RequestBody customer: Customer): Customer {
        if (customer.customerId != null) {
            throw InvalidRequestException("customerId must be null")
        }

        return customerService.create(customer)
    }

    @PostMapping("/login")
    fun signin(@RequestBody request: Customer): ApiToken {
        if (request.customerId != null) {
            throw InvalidRequestException("customerId must be null")
        }

        return customerService.login(request)
    }
}