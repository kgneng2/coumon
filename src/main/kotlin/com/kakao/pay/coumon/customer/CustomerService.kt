package com.kakao.pay.coumon.customer

import com.kakao.pay.coumon.authentication.ApiToken
import com.kakao.pay.coumon.authentication.JwtComponent
import com.kakao.pay.coumon.exception.InvalidRequestException
import com.kakao.pay.coumon.exception.LoginException
import mu.KotlinLogging
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class CustomerService {

    @Autowired
    private lateinit var customerRepository: CustomerRepository

    fun create(newCustomer: Customer): Customer {
        if (customerRepository.existsByLoginId(newCustomer.loginId)) {
            throw InvalidRequestException("duplicated loginId")
        } else {
            newCustomer.password = encryptPassword(newCustomer.password)

            val customer = customerRepository.save(newCustomer)

            println(JwtComponent.generateApiToken(customer))

            return customer
        }
    }

    fun check(loginCustomer: Customer): ApiToken {
        loginCustomer.password = encryptPassword(loginCustomer.password)

        val customer = customerRepository
                .findByLoginIdAndPassword(loginCustomer.loginId, loginCustomer.password)
                ?: throw LoginException()

        log.info("success check customer info :$customer")

        return JwtComponent.generateApiToken(customer)
    }

    private fun encryptPassword(password: String): String {
        return DigestUtils.sha256Hex(password)
    }

}
