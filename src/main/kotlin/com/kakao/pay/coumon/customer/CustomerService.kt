package com.kakao.pay.coumon.customer

import com.kakao.pay.coumon.authentication.ApiToken
import com.kakao.pay.coumon.authentication.JwtService
import com.kakao.pay.coumon.exception.InvalidRequestException
import mu.KotlinLogging
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class CustomerService {

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    @Autowired
    private lateinit var jwtService: JwtService

    fun create(newCustomer: Customer): Customer {
        if (customerRepository.existsByLoginId(newCustomer.loginId)) {
            throw InvalidRequestException("duplicated loginId")
        } else {
            newCustomer.password = encryptPassword(newCustomer.password)
            val customer = customerRepository.save(newCustomer)
            println(jwtService.generateApiToken(customer))

            return customer
        }
    }

    fun login(loginCustomer: Customer): ApiToken {
        loginCustomer.password = encryptPassword(loginCustomer.password)
        val customer = confirm(loginCustomer.loginId, loginCustomer.password)

        log.info("success check customer info :$customer")

        return jwtService.generateApiToken(customer)
    }

    fun confirm(loginId : String, password :String): Customer {
        return customerRepository
                .findByLoginIdAndPassword(loginId, password)
                ?: throw InvalidRequestException("ID or PASSWORD is wrong")
    }

    private fun encryptPassword(password: String): String {
        return DigestUtils.sha256Hex(password)
    }

}
