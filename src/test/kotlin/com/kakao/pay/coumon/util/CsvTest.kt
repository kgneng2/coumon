package com.kakao.pay.coumon.util

import com.kakao.pay.coumon.coupon.Coupon
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.junit.Test
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths


class CsvTest {

    private val SAMPLE_CSV_FILE = "./coupon.csv"

//    @Test
    fun `200만개 쿠폰 생성 csv`() {
        val list = mutableListOf<Coupon>()
        for (x in 0 until 2000000) {

            val c = Coupon(
                    customerId = (x + 1).toLong(),
                    couponType = "sale",
                    couponName = "coumon$x",
                    delFlag = false,
                    used = false,
                    createdAt = null
            )
            list.add(c)
        }

        writeCouponsToCSV(list)
    }


    private fun writeCouponsToCSV(coupons: List<Coupon>) {
        try {
            Files.newBufferedWriter(Paths.get(SAMPLE_CSV_FILE)).use { out ->
                CSVPrinter(out, CSVFormat.DEFAULT.withHeader("couponType", "couponName", "delFlag", "used", "customerId"))
                        .use { csvPrinter ->
                            for (coupon in coupons) {
                                val data = listOf(
                                        coupon.couponType,
                                        coupon.couponName,
                                        coupon.delFlag.toString(),
                                        coupon.used.toString(),
                                        coupon.customerId.toString()
                                )

                                csvPrinter.printRecord(data)
                            }
                            csvPrinter.flush()
                        }
            }
        } catch (e: IOException) {
            throw RuntimeException("fail to import data to CSV file: " + e.message)
        }
    }
}