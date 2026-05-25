package com.msa.eshop.ui.screen.paymentMethod

import com.msa.eshop.data.local.entity.OrderAddressEntity
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.PaymentMethodEntity
import com.msa.eshop.utils.Currency

const val PAYMENT_TITLE_CASH = "قیمت نقدی"
const val PAYMENT_TITLE_CHEQUE = "قیمت چک"
const val PAYMENT_TITLE_RECEIPT = "قیمت عرفی"

const val RECEIVE_TITLE_CASH = "پرداخت به صورت نقدی"
const val RECEIVE_TITLE_PLACE = "پرداخت در محل"

private const val DEFAULT_PAYMENT_TITLE = PAYMENT_TITLE_RECEIPT
private const val DEFAULT_RECEIVE_TITLE = RECEIVE_TITLE_CASH

data class PaymentMethodUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedAddress: OrderAddressEntity? = null,
    val paymentMethod: PaymentMethodEntity? = null,
    val orders: List<OrderEntity> = emptyList(),
    val selectedPaymentTitle: String = DEFAULT_PAYMENT_TITLE,
    val selectedReceiveTitle: String = DEFAULT_RECEIVE_TITLE
) {
    val canSubmit: Boolean
        get() = selectedAddress != null && orders.isNotEmpty() && !isLoading

    val isEmpty: Boolean
        get() = !isLoading && selectedAddress == null && errorMessage == null

    val totalItems: Int
        get() = orders.sumOf { it.numberOrder }

    val paymentOptions: List<String>
        get() {
            val method = paymentMethod ?: return emptyList()

            return listOf(
                "$PAYMENT_TITLE_CASH: ${method.cashprice.formatMoney()} ریال",
                "$PAYMENT_TITLE_CHEQUE: ${method.checkprice.formatMoney()} ریال",
                "$PAYMENT_TITLE_RECEIPT: ${method.receiptprice.formatMoney()} ریال"
            )
        }

    val receiveOptions: List<String>
        get() = listOf(
            RECEIVE_TITLE_CASH,
            RECEIVE_TITLE_PLACE
        )
}

fun String?.formatMoney(): String {
    val value = this?.takeIf { it.isNotBlank() } ?: "0"

    return runCatching {
        Currency(value).toFormattedString()
    }.getOrElse {
        value
    }
}

fun String.extractPaymentTitle(): String {
    return substringBefore(":").trim()
}