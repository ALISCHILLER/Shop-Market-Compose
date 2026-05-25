package com.msa.eshop.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class Currency private constructor(
    private val value: BigDecimal
) {

    constructor(value: String?) : this(parseOrZero(value))

    constructor(value: Number?) : this(parseOrZero(value?.toString()))

    fun add(currency: Currency): Currency {
        return Currency(value.add(currency.value))
    }

    fun add(number: Number?): Currency {
        return add(Currency(number))
    }

    fun subtract(currency: Currency): Currency {
        return Currency(value.subtract(currency.value))
    }

    fun multiply(currency: Currency): Currency {
        return Currency(value.multiply(currency.value))
    }

    fun multiply(number: Number?): Currency {
        return multiply(Currency(number))
    }

    fun divide(
        currency: Currency,
        scale: Int = DEFAULT_SCALE,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): Currency {
        if (currency.value.compareTo(BigDecimal.ZERO) == 0) {
            return ZERO
        }

        return Currency(
            value.divide(
                currency.value,
                scale.coerceAtLeast(0),
                roundingMode
            )
        )
    }

    fun toBigDecimal(): BigDecimal {
        return value
    }

    fun toLong(): Long {
        return value.setScale(0, RoundingMode.HALF_UP).toLong()
    }

    fun toFormattedString(): String {
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            groupingSeparator = ','
            decimalSeparator = '.'
        }

        val formatter = DecimalFormat("#,##0.##", symbols)
        return formatter.format(value)
    }

    override fun toString(): String {
        return value.stripTrailingZeros().toPlainString()
    }

    companion object {
        private const val DEFAULT_SCALE = 2

        val ZERO = Currency(BigDecimal.ZERO)
        val ONE = Currency(BigDecimal.ONE)
        val TEN = Currency(BigDecimal.TEN)
        val HUNDRED = Currency(BigDecimal(100))

        fun parseOrZero(value: String?): BigDecimal {
            val cleanedValue = value
                .orEmpty()
                .trim()
                .toEnglishDigits()
                .replace(",", "")
                .replace("٬", "")
                .replace("،", "")
                .takeIf { it.isNotBlank() }
                ?: return BigDecimal.ZERO

            return runCatching {
                BigDecimal(cleanedValue).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP)
            }.getOrDefault(BigDecimal.ZERO)
        }
    }
}