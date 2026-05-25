package com.msa.eshop.utils

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.msa.eshop.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import timber.log.Timber

class BiometricTools @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun showBiometricDialog(
        fragmentActivity: FragmentActivity,
        onAuthenticationError: (String) -> Unit,
        onAuthenticationFailed: () -> Unit = {},
        onAuthenticationSucceeded: () -> Unit
    ) {
        val availabilityError = getBiometricAvailabilityError()

        if (availabilityError != null) {
            Timber.tag(TAG).e("Biometric unavailable | reason=$availabilityError")
            onAuthenticationError(availabilityError)
            return
        }

        val executor = ContextCompat.getMainExecutor(context)

        val biometricPrompt = BiometricPrompt(
            fragmentActivity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)

                    val message = errorCode.toBiometricErrorMessage(
                        fallback = errString.toString()
                    )

                    Timber.tag(TAG).e(
                        "Biometric authentication error | code=$errorCode | message=$message"
                    )

                    onAuthenticationError(message)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    Timber.tag(TAG).e("Biometric authentication failed")
                    onAuthenticationFailed()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)

                    Timber.tag(TAG).d("Biometric authentication succeeded")
                    onAuthenticationSucceeded()
                }
            }
        )

        biometricPrompt.authenticate(createPromptInfo())
    }

    fun isBiometricAvailable(): Boolean {
        return getBiometricAvailabilityError() == null
    }

    private fun getBiometricAvailabilityError(): String? {
        val biometricManager = BiometricManager.from(context)

        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> null

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                context.getString(R.string.ERROR_NO_HARDWARE)
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                "سنسور اثر انگشت در حال حاضر در دسترس نیست"
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                context.getString(R.string.ERROR_NONE_ENROLLED)
            }

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                "برای استفاده از اثر انگشت، به‌روزرسانی امنیتی دستگاه لازم است"
            }

            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                "احراز هویت بایومتریک روی این دستگاه پشتیبانی نمی‌شود"
            }

            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                "وضعیت اثر انگشت روی دستگاه مشخص نیست"
            }

            else -> {
                "امکان استفاده از اثر انگشت وجود ندارد"
            }
        }
    }

    private fun createPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("ورود با اثر انگشت")
            .setSubtitle("برای ورود سریع، اثر انگشت خود را تأیید کنید")
            .setNegativeButtonText("انصراف")
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()
    }

    private fun Int.toBiometricErrorMessage(
        fallback: String
    ): String {
        return when (this) {
            BiometricPrompt.ERROR_NEGATIVE_BUTTON,
            BiometricPrompt.ERROR_USER_CANCELED,
            BiometricPrompt.ERROR_CANCELED -> {
                "احراز هویت لغو شد"
            }

            BiometricPrompt.ERROR_LOCKOUT -> {
                "به دلیل تلاش‌های ناموفق، اثر انگشت موقتاً غیرفعال شد"
            }

            BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                "اثر انگشت به دلیل تلاش‌های ناموفق زیاد غیرفعال شده است"
            }

            BiometricPrompt.ERROR_TIMEOUT -> {
                "زمان احراز هویت اثر انگشت تمام شد"
            }

            BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                context.getString(R.string.ERROR_NONE_ENROLLED)
            }

            BiometricPrompt.ERROR_HW_UNAVAILABLE -> {
                "سنسور اثر انگشت در حال حاضر در دسترس نیست"
            }

            BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> {
                "قفل صفحه یا اثر انگشت روی دستگاه فعال نیست"
            }

            else -> {
                fallback.ifBlank { "احراز هویت بایومتریک انجام نشد" }
            }
        }
    }

    companion object {
        private const val TAG = "BiometricTools"
    }
}