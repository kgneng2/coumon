package com.kakao.pay.coumon.customer

import com.kakao.pay.coumon.exception.InvalidRequestException
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class CustomerServiceTest  {

    @Autowired
    private lateinit var customerService: CustomerService

    @Test(expected = InvalidRequestException::class)
    fun `loginId 중복`() {
        val customer  = Customer(
                customerId = null,
                loginId = "kgneng2",
                password = "1234",
                createdAt = null
        )
        customerService.create(customer)

        val newCustomer  = Customer(
                customerId = null,
                loginId = "kgneng2",
                password = "123456",
                createdAt = null
        )

        customerService.create(newCustomer)
    }



}

