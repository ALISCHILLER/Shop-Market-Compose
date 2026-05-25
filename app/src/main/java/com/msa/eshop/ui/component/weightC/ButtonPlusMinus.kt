package com.msa.eshop.ui.component.weightC

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Composable
fun CounterButtonNew(
    value: String,
    onValueDecreaseClick: () -> Unit = {},
    onValueIncreaseClick: () -> Unit = {},
    onValue: (String) -> Unit,
    onValueClearClick: () -> Unit,
    modifier: Modifier = Modifier,
    minValue: Int = 0,
    maxValue: Int = 999_999,
    enabled: Boolean = true
) {
    val safeMin = minValue.coerceAtLeast(0)
    val safeMax = maxValue.coerceAtLeast(safeMin)
    val intValue = value.toIntOrNull()
        ?.coerceIn(safeMin, safeMax)
        ?: safeMin

    fun emitValue(newValue: Int) {
        val clamped = newValue.coerceIn(safeMin, safeMax)
        onValue(clamped.toString())
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = modifier.widthIn(min = 136.dp, max = 180.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            IconButton(
                enabled = enabled && intValue > safeMin,
                onClick = {
                    emitValue(intValue - 1)
                    onValueDecreaseClick()
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                ),
                modifier = Modifier.border(
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(10.dp)
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Remove,
                    contentDescription = "کم کردن"
                )
            }

            OutlinedTextField(
                value = intValue.toString(),
                onValueChange = { newValue ->
                    val filtered = newValue.filter(Char::isDigit)

                    if (filtered.isBlank()) {
                        emitValue(safeMin)
                        onValueClearClick()
                    } else {
                        emitValue(filtered.toIntOrNull() ?: safeMin)
                    }
                },
                enabled = enabled,
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center
                ),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            IconButton(
                enabled = enabled && intValue < safeMax,
                onClick = {
                    emitValue(intValue + 1)
                    onValueIncreaseClick()
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "زیاد کردن"
                )
            }
        }
    }
}

@Composable
fun CounterButton(
    value: String,
    onValueDecreaseClick: () -> Unit,
    onValueIncreaseClick: () -> Unit,
    onValue: (String) -> Unit,
    onValueClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CounterButtonNew(
        value = value,
        onValueDecreaseClick = onValueDecreaseClick,
        onValueIncreaseClick = onValueIncreaseClick,
        onValue = onValue,
        onValueClearClick = onValueClearClick,
        modifier = modifier
    )
}