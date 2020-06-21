package com.kakao.pay.coumon.customer

import com.kakao.pay.coumon.exception.InvalidRequestException
import com.kakao.pay.coumon.exception.LoginException
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

    @Transactional
    @Test(expected = InvalidRequestException::class)
    fun `loginId 중복`() {
        val customer = Customer(
                customerId = null,
                loginId = "kgneng2",
                password = "1234",
                createdAt = null
        )
        customerService.create(customer)

        val newCustomer = Customer(
                customerId = null,
                loginId = "kgneng2",
                password = "123456",
                createdAt = null
        )

        customerService.create(newCustomer)
    }


    @Test(expected = LoginException::class)
    @Transactional
    fun idPasswordWrongTest() {
        customerService.create(Customer(
                customerId = null,
                loginId = "kgneng2",
                password = "1234",
                createdAt = null
        ))

        customerService.check(Customer(
                customerId = null,
                loginId = "kgneng2",
                password = "12345678",
                createdAt = null
        ))
    }
}

