package com.kakao.pay.coumon.exception

import java.lang.RuntimeException

open class CoumonException(exMessage: String?,
                           cause: Throwable? = null) : RuntimeException(exMessage, cause)