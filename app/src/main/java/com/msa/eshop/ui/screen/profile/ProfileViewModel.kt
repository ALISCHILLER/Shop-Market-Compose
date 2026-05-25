package com.msa.eshop.ui.screen.profile

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.msa.eshop.data.repository.ProfileRepository
import com.msa.eshop.ui.navigation.NavInfo
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.Route
import com.msa.eshop.utils.CompanionValues
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

data class ProfileUiState(
    val customerName: String = "نام مشتری",
    val customerCode: String = "کد مشتری",
    val isLoading: Boolean = true,
    val showLogoutConfirm: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val navManager: NavManager,
    private val profileRepository: ProfileRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var userJob: Job? = null

    init {
        observeUser()
    }

    private fun observeUser() {
        if (userJob?.isActive == true) return

        userJob = viewModelScope.launch {
            profileRepository.getUser.collect { user ->
                Timber.tag(TAG).d("Profile user loaded: $user")

                _uiState.update {
                    it.copy(
                        customerName = user.customerName.orEmpty().ifBlank { "نام مشتری" },
                        customerCode = user.id.toString().ifBlank { "کد مشتری" },
                        isLoading = false
                    )
                }
            }
        }
    }

    fun showLogoutDialog() {
        _uiState.update {
            it.copy(showLogoutConfirm = true)
        }
    }

    fun dismissLogoutDialog() {
        _uiState.update {
            it.copy(showLogoutConfirm = false)
        }
    }

    fun navigateToAddressRegistration() {
        navManager.navigate(
            NavInfo(
                id = Route.AddressRegistrationScreen.route,
                navOption = NavOptions.Builder()
                    .setPopUpTo(
                        Route.ProfileScreen.route,
                        false
                    )
                    .setLaunchSingleTop(true)
                    .build()
            )
        )
    }

    fun logout() {
        sharedPreferences.edit()
            .remove(CompanionValues.TOKEN)
            .remove(CompanionValues.USERNAME)
            .remove(CompanionValues.PASSWORD)
            .apply()

        _uiState.update {
            it.copy(showLogoutConfirm = false)
        }

        navManager.navigate(
            NavInfo(
                id = Route.LoginScreen.route,
                navOption = NavOptions.Builder()
                    .setPopUpTo(
                        Route.HomeScreen.route,
                        true
                    )
                    .setLaunchSingleTop(true)
                    .build()
            )
        )
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}