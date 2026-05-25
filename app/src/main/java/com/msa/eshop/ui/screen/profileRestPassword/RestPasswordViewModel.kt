package com.msa.eshop.ui.screen.profileRestPassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.eshop.data.Model.request.ChangePasswordRequest
import com.msa.eshop.data.repository.RestPasswordRepository
import com.msa.eshop.utils.result.GeneralStateModel
import com.msa.eshop.utils.result.makeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

data class RestPasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val repeatNewPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val message: String? = null
) {
    val canSubmit: Boolean
        get() = currentPassword.isNotBlank() &&
                newPassword.isNotBlank() &&
                repeatNewPassword.isNotBlank() &&
                !isLoading
}

@HiltViewModel
class RestPasswordViewModel @Inject constructor(
    private val restPasswordRepository: RestPasswordRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state.asStateFlow()

    private val _uiState = MutableStateFlow(RestPasswordUiState())
    val uiState: StateFlow<RestPasswordUiState> = _uiState.asStateFlow()

    fun onCurrentPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                currentPassword = value,
                errorMessage = null,
                message = null
            )
        }
    }

    fun onNewPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                newPassword = value,
                errorMessage = null,
                message = null
            )
        }
    }

    fun onRepeatNewPasswordChange(value: String) {
        _uiState.update {
            it.copy(
                repeatNewPassword = value,
                errorMessage = null,
                message = null
            )
        }
    }

    fun submit() {
        val current = _uiState.value

        restPasswordRequest(
            password = current.currentPassword,
            newPassword = current.newPassword,
            repeatNewPassword = current.repeatNewPassword
        )
    }

    fun restPasswordRequest(
        password: String,
        newPassword: String,
        repeatNewPassword: String
    ) {
        val validationError = validatePasswordForm(
            password = password,
            newPassword = newPassword,
            repeatNewPassword = repeatNewPassword
        )

        if (validationError != null) {
            updateStateError(validationError)
            return
        }

        makeRequest(
            scope = viewModelScope,
            request = {
                restPasswordRepository.changepassword(
                    ChangePasswordRequest(
                        oldPassword = password.trim(),
                        newPassword = newPassword.trim()
                    )
                )
            },
            onSuccess = { response ->
                val message = response?.message
                    ?: "رمز عبور با موفقیت تغییر کرد"

                Timber.tag(TAG).d("Password changed successfully")

                _uiState.update {
                    it.copy(
                        currentPassword = "",
                        newPassword = "",
                        repeatNewPassword = "",
                        isLoading = false,
                        errorMessage = null,
                        message = message
                    )
                }

                updateStateMessage(message)
            },
            updateStateLoading = ::updateStateLoading,
            updateStateError = ::updateStateError
        )
    }

    fun clearState() {
        _state.value = GeneralStateModel()

        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = null,
                message = null
            )
        }
    }

    private fun validatePasswordForm(
        password: String,
        newPassword: String,
        repeatNewPassword: String
    ): String? {
        return when {
            password.isBlank() -> "رمز عبور فعلی را وارد کنید"
            newPassword.isBlank() -> "رمز عبور جدید را وارد کنید"
            repeatNewPassword.isBlank() -> "تکرار رمز عبور جدید را وارد کنید"
            newPassword.length < 6 -> "رمز عبور جدید باید حداقل ۶ کاراکتر باشد"
            newPassword == password -> "رمز عبور جدید نباید با رمز عبور فعلی یکسان باشد"
            newPassword != repeatNewPassword -> "رمز عبور جدید و تکرار آن یکسان نیستند"
            else -> null
        }
    }

    private fun updateStateLoading(isLoading: Boolean) {
        _state.update {
            it.copy(
                isLoading = isLoading,
                error = null,
                message = null
            )
        }

        _uiState.update {
            it.copy(
                isLoading = isLoading,
                errorMessage = null,
                message = null
            )
        }
    }

    private fun updateStateError(errorMessage: String?) {
        Timber.tag(TAG).e(errorMessage.orEmpty())

        _state.update {
            it.copy(
                isLoading = false,
                error = errorMessage,
                message = null
            )
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = errorMessage,
                message = null
            )
        }
    }

    private fun updateStateMessage(message: String?) {
        _state.update {
            it.copy(
                isLoading = false,
                error = null,
                message = message
            )
        }
    }

    companion object {
        private const val TAG = "RestPasswordVM"
    }
}