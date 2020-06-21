package com.kakao.pay.coumon.coupon

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table
@Entity
data class Coupon(
        @Id
        @get:JsonProperty("couponNumber")
        val id: UUID = UUID.randomUUID(),
        var couponType: String? = null,
        var couponName: String? = null,
        var delFlag: Boolean = false,
        var used: Boolean = false,
        var customerId: Long? = null,
        @CreatedDate
        @get:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "UTC+9")
        var createdAt: LocalDateTime?,
        @LastModifiedDate
        @get:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "UTC+9")
        val updatedAt: LocalDateTime? = null,
        @get:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC+9")
        var expiredAt : LocalDate = LocalDate.now().plusDays(90L)
)