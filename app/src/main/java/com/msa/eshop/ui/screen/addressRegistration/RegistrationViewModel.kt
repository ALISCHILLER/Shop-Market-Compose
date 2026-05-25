package com.msa.eshop.ui.screen.addressRegistration

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.msa.eshop.ui.navigation.NavInfo
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.Route
import com.msa.eshop.utils.map.location.PiLocationException
import com.msa.eshop.utils.map.location.PiLocationManager
import com.msa.eshop.utils.result.GeneralStateModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

data class LocationRegistrationUiState(
    val location: Location? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val hasPermission: Boolean = false
)

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val navManager: NavManager,
    private val locationManager: PiLocationManager
) : ViewModel() {

    private val _state = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state.asStateFlow()

    private val _uiState = MutableStateFlow(LocationRegistrationUiState())
    val uiState: StateFlow<LocationRegistrationUiState> = _uiState.asStateFlow()

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location.asStateFlow()

    private var locationJob: Job? = null

    fun onPermissionResult(granted: Boolean) {
        _uiState.update {
            it.copy(
                hasPermission = granted,
                errorMessage = if (granted) null else "برای ثبت موقعیت، دسترسی مکان لازم است"
            )
        }

        if (granted) {
            startLocationUpdates()
        }
    }

    fun startLocationUpdates() {
        if (locationJob?.isActive == true) return

        locationJob = viewModelScope.launch {
            updateStateLoading(true)

            runCatching {
                locationManager.locationUpdates(LOCATION_INTERVAL_MS).collect { location ->
                    Timber.tag(TAG).d(
                        "Location update | lat=${location.latitude}, lng=${location.longitude}"
                    )

                    _location.value = location

                    _uiState.update {
                        it.copy(
                            location = location,
                            isLoading = false,
                            errorMessage = null
                        )
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = null
                        )
                    }
                }
            }.onFailure { throwable ->
                handleLocationError(throwable)
            }
        }
    }

    fun restartLocationUpdates() {
        locationJob?.cancel()
        locationJob = null
        startLocationUpdates()
    }

    private fun handleLocationError(throwable: Throwable) {
        Timber.tag(TAG).e(throwable, "Location update failed")

        if (throwable is PiLocationException) {
            locationManager.turnOnGPS()
        }

        val message = throwable.message ?: "دریافت موقعیت مکانی با خطا مواجه شد"

        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = message
            )
        }

        _state.update {
            it.copy(
                isLoading = false,
                error = message
            )
        }
    }

    private fun updateStateLoading(isLoading: Boolean) {
        _state.update {
            it.copy(
                isLoading = isLoading,
                error = null
            )
        }

        _uiState.update {
            it.copy(
                isLoading = isLoading,
                errorMessage = null
            )
        }
    }

    fun clearError() {
        _state.update {
            it.copy(error = null)
        }

        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    fun navigateToLocationRegistration() {
        navManager.navigate(
            NavInfo(
                id = Route.LocationRegistrationScreen.route,
                navOption = NavOptions.Builder()
                    .setPopUpTo(
                        Route.AddressRegistrationScreen.route,
                        false
                    )
                    .setLaunchSingleTop(true)
                    .build()
            )
        )
    }

    fun navigateToAddressRegistration() {
        navManager.navigate(
            NavInfo(
                id = Route.AddressRegistrationScreen.route,
                navOption = NavOptions.Builder()
                    .setPopUpTo(
                        Route.LocationRegistrationScreen.route,
                        false
                    )
                    .setLaunchSingleTop(true)
                    .build()
            )
        )
    }

    override fun onCleared() {
        locationJob?.cancel()
        super.onCleared()
    }

    companion object {
        private const val TAG = "RegistrationVM"
        private const val LOCATION_INTERVAL_MS = 5_000L
    }
}