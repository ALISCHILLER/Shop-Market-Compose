package com.msa.eshop.ui.screen.accountInformation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.eshop.data.local.entity.UserModelEntity
import com.msa.eshop.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

data class AccountInformationUiState(
    val user: UserModelEntity? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class AccountInformationViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountInformationUiState())
    val uiState: StateFlow<AccountInformationUiState> = _uiState.asStateFlow()

    private var userJob: Job? = null

    init {
        observeUser()
    }

    private fun observeUser() {
        if (userJob != null) return

        userJob = viewModelScope.launch {
            profileRepository.getUser.collect { user ->
                Timber.tag(TAG).d("User loaded: $user")

                _uiState.value = AccountInformationUiState(
                    user = user,
                    isLoading = false
                )
            }
        }
    }

    companion object {
        private const val TAG = "AccountInfoVM"
    }
}