package com.msa.eshop.utils

class Convert_Number {

    fun PersianToEnglish(persianStr: String): String {
        return persianStr.toEnglishDigits()
    }

    fun EnglishToPersion(englishNumber: String): String {
        return englishNumber.toPersianDigits()
    }

    fun EnglishToPersian(englishNumber: String): String {
        return englishNumber.toPersianDigits()
    }
}

fun String.toEnglishDigits(): String {
    if (isEmpty()) return this

    val builder = StringBuilder(length)

    forEach { char ->
        builder.append(char.toEnglishDigit())
    }

    return builder.toString()
}

fun String.toPersianDigits(): String {
    if (isEmpty()) return this

    val builder = StringBuilder(length)

    forEach { char ->
        builder.append(char.toPersianDigit())
    }

    return builder.toString()
}

fun Char.toEnglishDigit(): Char {
    return when (this) {
        '۰', '٠' -> '0'
        '۱', '١' -> '1'
        '۲', '٢' -> '2'
        '۳', '٣' -> '3'
        '۴', '٤' -> '4'
        '۵', '٥' -> '5'
        '۶', '٦' -> '6'
        '۷', '٧' -> '7'
        '۸', '٨' -> '8'
        '۹', '٩' -> '9'
        else -> this
    }
}

fun Char.toPersianDigit(): Char {
    return when (this) {
        '0' -> '۰'
        '1' -> '۱'
        '2' -> '۲'
        '3' -> '۳'
        '4' -> '۴'
        '5' -> '۵'
        '6' -> '۶'
        '7' -> '۷'
        '8' -> '۸'
        '9' -> '۹'
        else -> this
    }
}