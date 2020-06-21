package com.kakao.pay.coumon.customer

import com.kakao.pay.coumon.authentication.JwtService
import com.kakao.pay.coumon.exception.InvalidRequestException
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import javax.transaction.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest
class CustomerServiceTest {

    @Autowired
    private lateinit var customerService: CustomerService

    @Autowired
    private lateinit var jwtService: JwtService


    fun signUp(loginId: String): Customer {
        val customer = Customer(
                customerId = null,
                loginId = loginId,
                password = "1234",
                createdAt = null
        )
        return customerService.create(customer)
    }

    @Test(expected = InvalidRequestException::class)
    fun `loginId 중복`() {
        val loginId = "id1"
        signUp(loginId)

        val newCustomer = Customer(
                customerId = null,
                loginId = loginId,
                password = "1234",
                createdAt = null
        )

        customerService.create(newCustomer)
    }

    @Test(expected = InvalidRequestException::class)
    fun idPasswordWrongTest() {
        val loginId = "id2"
        signUp(loginId)

        customerService.login(Customer(
                customerId = null,
                loginId = loginId,
                password = "12345678",
                createdAt = null
        ))
    }

    @Test
    @Transactional
    fun createTest() {
        val cus = customerService.create(Customer(
                customerId = null,
                loginId = "kgneng2222",
                password = "1234",
                createdAt = null
        ))

        Assert.assertNotNull(cus.customerId)
    }

    @Test
    fun `정상 로그인 테스트`() {
        val customer = Customer(
                customerId = null,
                loginId = "sss123",
                password = "1234",
                createdAt = null
        )
        customerService.create(customer)

        val apiToken = customerService.login(customer)

        Assert.assertTrue(jwtService.verify(apiToken.token))
    }
}

