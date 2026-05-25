package com.msa.eshop.ui.screen.login

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.msa.eshop.data.Model.request.TokenRequest
import com.msa.eshop.data.repository.LoginRepository
import com.msa.eshop.ui.navigation.NavInfo
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.Route
import com.msa.eshop.utils.BiometricTools
import com.msa.eshop.utils.CompanionValues
import com.msa.eshop.utils.Convert_Number
import com.msa.eshop.utils.result.GeneralStateModel
import com.msa.eshop.utils.result.makeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val canUseBiometric: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val canSubmit: Boolean
        get() = username.isNotBlank() && password.isNotBlank() && !isLoading
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val navManager: NavManager,
    private val loginRepository: LoginRepository,
    private val biometricTools: BiometricTools,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val converter = Convert_Number()

    private val _state = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _biometric = MutableStateFlow("")
    val biometric: StateFlow<String> = _biometric

    fun onUsernameChange(value: String) {
        _uiState.update {
            it.copy(
                username = value,
                errorMessage = null
            )
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                password = value,
                errorMessage = null
            )
        }
    }

    fun onRememberMeChange(value: Boolean) {
        _uiState.update {
            it.copy(rememberMe = value)
        }
    }

    fun togglePasswordVisibility() {
        _uiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    fun login() {
        val currentState = _uiState.value

        tokenRequest(
            username = currentState.username,
            password = currentState.password,
            rememberme = currentState.rememberMe
        )
    }

    fun tokenRequest(
        username: String,
        password: String,
        rememberme: Boolean
    ) {
        val normalizedUsername = converter.PersianToEnglish(username.trim())
        val normalizedPassword = converter.PersianToEnglish(password.trim())

        when {
            normalizedUsername.isBlank() && normalizedPassword.isBlank() -> {
                updateStateError("لطفاً کد مشتری و رمز عبور را وارد کنید")
                return
            }

            normalizedUsername.isBlank() -> {
                updateStateError("لطفاً کد مشتری را وارد کنید")
                return
            }

            normalizedPassword.isBlank() -> {
                updateStateError("لطفاً رمز عبور را وارد کنید")
                return
            }

            _uiState.value.isLoading -> {
                return
            }
        }

        makeRequest(
            scope = viewModelScope,
            request = {
                loginRepository.loginToken(
                    TokenRequest(
                        customerCode = normalizedUsername,
                        password = normalizedPassword
                    )
                )
            },
            onSuccess = { response ->
                val token = response?.data

                if (token.isNullOrBlank()) {
                    updateStateError("توکن ورود دریافت نشد")
                    return@makeRequest
                }

                saveLoginData(
                    token = token,
                    username = normalizedUsername,
                    password = normalizedPassword,
                    rememberMe = rememberme
                )

                UserRequest()
            },
            updateStateLoading = ::updateStateLoading,
            updateStateError = ::updateStateError
        )
    }

    fun UserRequest() {
        makeRequest(
            scope = viewModelScope,
            request = {
                loginRepository.getUserData()
            },
            onSuccess = { response ->
                viewModelScope.launch {
                    val user = response?.data?.firstOrNull()

                    if (user == null) {
                        updateStateError("اطلاعات کاربر دریافت نشد")
                        return@launch
                    }

                    runCatching {
                        loginRepository.insertUser(user)
                    }.onFailure { throwable ->
                        Timber.tag("LoginViewModel").e(throwable, "insertUser failed")
                    }

                    navigateToHome()
                }
            },
            updateStateLoading = ::updateStateLoading,
            updateStateError = ::updateStateError
        )
    }

    fun biometricDialog(fragmentActivity: FragmentActivity) {
        val savedUsername = getSavedUsername()
        val savedPassword = getSavedPassword()

        if (savedUsername.isBlank() || savedPassword.isBlank()) {
            updateStateError("برای ورود با اثر انگشت، ابتدا یک‌بار گزینه «مرا به خاطر بسپار» را فعال کنید")
            return
        }

        biometricTools.showBiometricDialog(
            fragmentActivity = fragmentActivity,
            onAuthenticationFailed = {
                updateStateError("اثر انگشت تأیید نشد")
            },
            onAuthenticationError = { message ->
                updateStateError(message)
            },
            onAuthenticationSucceeded = {
                tokenRequest(
                    username = savedUsername,
                    password = savedPassword,
                    rememberme = true
                )
            }
        )
    }

    fun clearError() {
        _uiState.update {
            it.copy(errorMessage = null)
        }

        _state.update {
            it.copy(error = null)
        }
    }

    fun clearState() {
        _state.value = GeneralStateModel()

        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = null
            )
        }
    }

    fun getSavedUsername(): String {
        return sharedPreferences
            .getString(CompanionValues.USERNAME, "")
            .orEmpty()
    }

    fun getSavedPassword(): String {
        return sharedPreferences
            .getString(CompanionValues.PASSWORD, "")
            .orEmpty()
    }

    private fun createInitialState(): LoginUiState {
        val savedUsername = getSavedUsername()
        val savedPassword = getSavedPassword()

        return LoginUiState(
            username = savedUsername,
            password = savedPassword,
            rememberMe = savedUsername.isNotBlank() && savedPassword.isNotBlank(),
            canUseBiometric = savedUsername.isNotBlank() && savedPassword.isNotBlank()
        )
    }

    private fun saveLoginData(
        token: String,
        username: String,
        password: String,
        rememberMe: Boolean
    ) {
        sharedPreferences.edit().apply {
            putString(CompanionValues.TOKEN, token)

            if (rememberMe) {
                putString(CompanionValues.USERNAME, username)
                putString(CompanionValues.PASSWORD, password)
            } else {
                remove(CompanionValues.USERNAME)
                remove(CompanionValues.PASSWORD)
            }
        }.apply()

        _uiState.update {
            it.copy(
                username = if (rememberMe) username else it.username,
                password = if (rememberMe) password else it.password,
                rememberMe = rememberMe,
                canUseBiometric = rememberMe && username.isNotBlank() && password.isNotBlank()
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

    private fun updateStateError(errorMessage: String?) {
        Timber.tag("LoginViewModel").e(errorMessage.orEmpty())

        _state.update {
            it.copy(
                isLoading = false,
                error = errorMessage
            )
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = errorMessage
            )
        }
    }

    private fun navigateToHome() {
        navManager.navigate(
            NavInfo(
                id = Route.HomeScreen.route,
                navOption = NavOptions.Builder()
                    .setPopUpTo(
                        Route.LoginScreen.route,
                        true
                    )
                    .setLaunchSingleTop(true)
                    .build()
            )
        )
    }
}