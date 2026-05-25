@file:OptIn(ExperimentalFoundationApi::class)

package com.msa.eshop.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.msa.eshop.ui.acticity.MainActivity
import com.msa.eshop.ui.acticity.MainViewModel
import com.msa.eshop.ui.navigation.bottomNav.BottomNavNoAnimation
import com.msa.eshop.ui.screen.address.OrderAddressScreen
import com.msa.eshop.ui.screen.addressRegistration.AddressRegistrationScreen
import com.msa.eshop.ui.screen.addressRegistration.LocationRegistrationScreen
import com.msa.eshop.ui.screen.basket.BasketScreen
import com.msa.eshop.ui.screen.home.HomeScreen
import com.msa.eshop.ui.screen.login.LoginScreen
import com.msa.eshop.ui.screen.orderDetailsReport.OrderDetailsReportScreen
import com.msa.eshop.ui.screen.orderStatusReport.OrderStatusReportScreen
import com.msa.eshop.ui.screen.paymentMethod.PaymentMethodScreen
import com.msa.eshop.ui.screen.profile.ProfileScreen
import com.msa.eshop.ui.screen.simulate.SimulateScreen
import com.msa.eshop.ui.screen.splash.SplashScreen
import com.msa.eshop.ui.theme.PlatinumSilver

@Composable
fun MainActivity.SetupNavigator() {
    val navController = rememberNavController()

    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route

    val showBottomBar = remember(currentRoute) {
        shouldShowBottomBar(currentRoute)
    }

    LaunchedEffect(navController) {
        navManager.commands.collect { navInfo ->
            val route = navInfo.id ?: return@collect

            when (route) {
                Route.BACK.route -> {
                    navController.popBackStack()
                }

                else -> {
                    val currentDestinationRoute = navController.currentDestination?.route

                    if (currentDestinationRoute != route) {
                        navController.navigate(
                            route = route,
                            navOptions = navInfo.navOption
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = PlatinumSilver,
        bottomBar = {
            if (showBottomBar) {
                val mainViewModel: MainViewModel = hiltViewModel()
                val allOrder by mainViewModel.allOrder.collectAsState()

                BottomNavNoAnimation(
                    currentRoute = currentRoute,
                    basketCount = allOrder.size,
                    onClick = { route ->
                        navigateToTab(
                            navController = navController,
                            route = route
                        )
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {
            AppNavHost(
                navController = navController
            )
        }
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Route.SplashScreen.route
    ) {
        composable(Route.SplashScreen.route) {
            SplashScreen()
        }

        composable(Route.LoginScreen.route) {
            LoginScreen()
        }

        composable(Route.HomeScreen.route) {
            HomeScreen()
        }

        composable(Route.BasketScreen.route) {
            BasketScreen()
        }

        composable(Route.SimulateScreen.route) {
            SimulateScreen()
        }

        composable(Route.OrderAddressScreen.route) {
            OrderAddressScreen()
        }

        composable(Route.PaymentMethodScreen.route) {
            PaymentMethodScreen()
        }

        composable(Route.OrderStatusReportScreen.route) {
            OrderStatusReportScreen()
        }

        composable(
            route = Route.OrderDetailsReportScreen.routeWithArgs,
            arguments = listOf(
                navArgument(Route.OrderDetailsReportScreen.ARG_CARD) {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val cartCode = backStackEntry.arguments
                ?.getInt(Route.OrderDetailsReportScreen.ARG_CARD)
                ?: 0

            OrderDetailsReportScreen(
                card = cartCode
            )
        }

        composable(Route.ProfileScreen.route) {
            ProfileScreen()
        }

        composable(Route.AddressRegistrationScreen.route) {
            AddressRegistrationScreen()
        }

        composable(Route.LocationRegistrationScreen.route) {
            LocationRegistrationScreen()
        }
    }
}

fun shouldShowBottomBar(currentRoute: String?): Boolean {
    return currentRoute in bottomBarRoutes
}

private val bottomBarRoutes = setOf(
    Route.HomeScreen.route,
    Route.OrderStatusReportScreen.route,
    Route.BasketScreen.route,
    Route.ProfileScreen.route
)

private fun navigateToTab(
    navController: NavController,
    route: String
) {
    if (navController.currentDestination?.route == route) return

    navController.navigate(route) {
        popUpTo(Route.HomeScreen.route) {
            saveState = true
            inclusive = false
        }

        launchSingleTop = true
        restoreState = true
    }
}

fun orderDetailsRoute(card: Int): String {
    return Route.OrderDetailsReportScreen.createRoute(card)
}