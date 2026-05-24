package com.msa.eshop.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.msa.eshop.R
import com.msa.eshop.ui.acticity.MainActivity
import com.msa.eshop.ui.theme.barcolor
import com.msa.eshop.ui.theme.barcolorlow

@Composable
fun DockedSearch(
    modifier: Modifier = Modifier,
    onQueryChange: (String) -> Unit
) {
    val context = LocalContext.current
    val mainActivity = context as? MainActivity

    var text by rememberSaveable {
        mutableStateOf("")
    }

    val speechText = mainActivity?.speechInput?.value.orEmpty()

    LaunchedEffect(speechText) {
        if (speechText.isNotBlank()) {
            text = speechText
            onQueryChange(speechText)
            mainActivity?.speechInput?.value = ""
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                onQueryChange(newText)
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .height(56.dp),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.title_search),
                    color = barcolorlow
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Search",
                    tint = barcolorlow
                )
            },
            trailingIcon = {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                text = ""
                                onQueryChange("")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = barcolorlow
                            )
                        }
                    }

                    VerticalDivider(
                        modifier = Modifier
                            .height(28.dp)
                            .padding(vertical = 4.dp),
                        color = barcolor,
                        thickness = 1.dp
                    )

                    IconButton(
                        onClick = {
                            mainActivity?.askSpeechInput(context)
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(id = R.drawable.ic_microphone),
                            contentDescription = "Mic",
                            tint = barcolorlow
                        )
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DockedSearchPreview() {
    DockedSearch(
        onQueryChange = {}
    )
}