package com.msa.eshop.ui.screen.splash

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.msa.eshop.ui.navigation.NavInfo
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.Route
import com.msa.eshop.utils.CompanionValues
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val navManager: NavManager,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private var hasStarted = false

    fun startSplashFlow() {
        if (hasStarted) return
        hasStarted = true

        viewModelScope.launch {
            delay(SPLASH_DELAY_MS)

            val token = sharedPreferences
                .getString(CompanionValues.TOKEN, null)
                .orEmpty()
                .trim()

            if (token.isNotEmpty()) {
                navigateToHome()
            } else {
                navigateToLogin()
            }
        }
    }

    private fun navigateToHome() {
        navManager.navigate(
            NavInfo(
                id = Route.HomeScreen.route,
                navOption = splashPopUpOptions()
            )
        )
    }

    private fun navigateToLogin() {
        navManager.navigate(
            NavInfo(
                id = Route.LoginScreen.route,
                navOption = splashPopUpOptions()
            )
        )
    }

    private fun splashPopUpOptions(): NavOptions {
        return NavOptions.Builder()
            .setPopUpTo(
                Route.SplashScreen.route,
                true
            )
            .setLaunchSingleTop(true)
            .build()
    }

    companion object {
        private const val SPLASH_DELAY_MS = 1500L
    }
}