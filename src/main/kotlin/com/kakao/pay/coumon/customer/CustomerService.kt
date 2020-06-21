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
            val encryptedUser = newCustomer.copy(password = encryptPassword(newCustomer.password))

            return customerRepository.save(encryptedUser)
        }
    }

    fun login(loginCustomer: Customer): ApiToken {
        val customer = confirm(loginCustomer.loginId, encryptPassword(loginCustomer.password))
        log.info("success check customer info :$customer")

        return jwtService.generateApiToken(customer)
    }

    fun confirm(loginId : String, encryptedPassword :String): Customer {
        log.info("loginId : $loginId, password : $encryptedPassword")
        return customerRepository
                .findByLoginIdAndPassword(loginId, encryptedPassword)
                ?: throw InvalidRequestException("ID or PASSWORD is wrong")
    }

    fun encryptPassword(password: String): String {
        return DigestUtils.sha256Hex(password)
    }

}
