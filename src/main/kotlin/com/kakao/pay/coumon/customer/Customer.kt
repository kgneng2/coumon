package com.kakao.pay.coumon.customer

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime
import javax.persistence.*

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table
@Entity
data class Customer(
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        val customerId: Long?,
        val loginId : String,
        var password: String,
        @CreatedDate
        @get:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "UTC+9")
        var createdAt: LocalDateTime?,
        @LastModifiedDate
        @get:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "UTC+9")
        val updatedAt: LocalDateTime? = null
)