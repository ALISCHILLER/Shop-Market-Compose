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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val navManager: NavManager,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private var splashJob: Job? = null

    fun startSplashFlow() {
        if (splashJob?.isActive == true) return

        splashJob = viewModelScope.launch {
            delay(SPLASH_DELAY_MS)

            val token = sharedPreferences
                .getString(CompanionValues.TOKEN, null)
                .orEmpty()
                .trim()

            val destination = if (token.isValidToken()) {
                Timber.tag(TAG).d("Splash destination: home")
                Route.HomeScreen.route
            } else {
                Timber.tag(TAG).d("Splash destination: login")
                Route.LoginScreen.route
            }

            navManager.navigate(
                NavInfo(
                    id = destination,
                    navOption = splashPopUpOptions()
                )
            )
        }
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

    private fun String.isValidToken(): Boolean {
        if (isBlank()) return false

        val normalized = lowercase()

        return normalized != "null" &&
                normalized != "undefined" &&
                normalized != "none" &&
                length >= MIN_TOKEN_LENGTH
    }

    companion object {
        private const val TAG = "SplashViewModel"
        private const val SPLASH_DELAY_MS = 1_350L
        private const val MIN_TOKEN_LENGTH = 8
    }
}