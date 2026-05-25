package com.msa.eshop.ui.navigation

sealed class Route(
    val route: String
) {

    data object BACK : Route(route = "back")

    data object SplashScreen : Route(route = "splashScreen")

    // Login
    data object LoginScreen : Route(route = "loginScreen")
    data object NewPasswordScreen : Route(route = "newPasswordScreen")
    data object NationalCodeResetPassScreen : Route(route = "nationalCodeResetPassScreen")
    data object OtpScreen : Route(route = "otpScreen")

    // Home Product
    data object HomeScreen : Route(route = "homeScreen")
    data object DetailsProductScreen : Route(route = "detailsProductScreen")

    // Basket
    data object BasketScreen : Route(route = "BasketScreen")
    data object SimulateScreen : Route(route = "simulateScreen")
    data object OrderAddressScreen : Route(route = "OrderAddressScreen")
    data object PaymentMethodScreen : Route(route = "paymentMethodScreen")

    // Profile Customer
    data object ProfileScreen : Route(route = "profileScreen")
    data object AddressRegistrationScreen : Route(route = "addressRegistrationScreen")
    data object LocationRegistrationScreen : Route(route = "locationRegistrationScreen")

    // Report
    data object OrderStatusReportScreen : Route(route = "orderStatusReportScreen")

    data object OrderDetailsReportScreen : Route(route = "orderDetailsReportScreen") {
        const val ARG_CARD = "card"

        val routeWithArgs: String
            get() = "$route/{$ARG_CARD}"

        fun createRoute(card: Int): String {
            return "$route/$card"
        }
    }
}