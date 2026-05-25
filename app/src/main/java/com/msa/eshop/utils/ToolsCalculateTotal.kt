package com.msa.eshop.utils

import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.ProductModelEntity

fun calculateTotalValue(
    value1: Int,
    value2: Int,
    convertFactor2: Int
): Int {
    val total = calculateTotalValueLong(
        value1 = value1,
        value2 = value2,
        convertFactor2 = convertFactor2
    )

    return total.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
}

fun calculateTotalValueLong(
    value1: Int,
    value2: Int,
    convertFactor2: Int
): Long {
    val safeValue1 = value1.coerceAtLeast(0).toLong()
    val safeValue2 = value2.coerceAtLeast(0).toLong()
    val safeConvertFactor2 = convertFactor2.coerceAtLeast(0).toLong()

    return (safeValue2 * safeConvertFactor2 + safeValue1).coerceAtLeast(0L)
}

fun calculateSalePrice(
    totalValue: Int,
    price: Int
): Float {
    return calculateSalePriceLong(
        totalValue = totalValue,
        price = price
    ).toFloat()
}

fun calculateSalePriceLong(
    totalValue: Int,
    price: Int
): Long {
    val safeTotalValue = totalValue.coerceAtLeast(0).toLong()
    val safePrice = price.coerceAtLeast(0).toLong()

    return (safeTotalValue * safePrice).coerceAtLeast(0L)
}

fun createOrderEntity(
    productModelEntity: ProductModelEntity,
    totalValue: Int,
    value1: Int,
    value2: Int
): OrderEntity {
    return with(productModelEntity) {
        OrderEntity(
            id = id,
            convertFactor1 = convertFactor1,
            convertFactor2 = convertFactor2,
            fullNameKala1 = fullNameKala1,
            fullNameKala2 = fullNameKala2,
            productCode = productCode,
            productGroupCode = productGroupCode,
            productName = productName,
            unit1 = unit1,
            unit2 = unit2,
            unitid1 = unitid1,
            unitid2 = unitid2,
            price = price,
            productImage = productImage,
            numberOrder = totalValue.coerceAtLeast(0),
            numberOrder1 = value1.coerceAtLeast(0),
            numberOrder2 = value2.coerceAtLeast(0),
            unitOrder = unit1
        )
    }
}

fun OrderEntity.totalPrice(): Float {
    return calculateSalePrice(
        totalValue = numberOrder,
        price = price
    )
}

fun OrderEntity.totalPriceLong(): Long {
    return calculateSalePriceLong(
        totalValue = numberOrder,
        price = price
    )
}