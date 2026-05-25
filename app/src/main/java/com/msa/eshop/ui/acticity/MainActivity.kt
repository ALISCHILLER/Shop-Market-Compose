package com.msa.eshop.ui.acticity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.FragmentActivity
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.SetupNavigator
import com.msa.eshop.ui.theme.EShopTheme
import com.msa.eshop.utils.map.location.PiLocationManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@OptIn(ExperimentalFoundationApi::class)
@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var navManager: NavManager

    @Inject
    lateinit var piLocationManager: PiLocationManager

    val speechInput = mutableStateOf("")

    private val speechLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val spokenText = result.data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.firstOrNull()
            .orEmpty()

        if (spokenText.isNotBlank()) {
            speechInput.value = spokenText
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        piLocationManager.setActivity(this)

        setContent {
            EShopTheme {
                SetupNavigator()
            }
        }
    }

    fun askSpeechInput(context: Context) {
        val language = Locale("fa", "IR").toLanguageTag()

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Toast.makeText(context, "تشخیص گفتار در این دستگاه فعال نیست", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "نام کالا را بگویید")
        }

        speechLauncher.launch(intent)
    }
}