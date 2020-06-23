package com.kakao.pay.coumon.util

import com.kakao.pay.coumon.coupon.Coupon
import com.kakao.pay.coumon.exception.InternalServerException
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


object CsvUtil {
    fun csvToCoupons(inputStream: InputStream): Iterator<Coupon> {
        try {
            BufferedReader(InputStreamReader(inputStream, "UTF-8"))
                    .use { fileReader ->
                        CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())
                                .use { csvParser ->
                                    val csvRecords = csvParser.records

                                    return csvRecords.map {
                                        Coupon(
                                                couponType = it.get("couponType"),
                                                couponName = it.get("couponName"),
                                                delFlag = it.get("delFlag")?.toBoolean() ?: false,
                                                used = it.get("used")?.toBoolean() ?: false,
                                                customerId = it.get("customerId").toLong(),
                                                createdAt = null
                                        )
                                    }.iterator()
                                }
                    }
        } catch (e: IOException) {
            throw InternalServerException("fail to parse CSV file:  ${e.message}")
        }
    }
}