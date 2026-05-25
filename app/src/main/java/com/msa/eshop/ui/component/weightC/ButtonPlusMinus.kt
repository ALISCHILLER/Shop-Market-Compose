package com.msa.eshop.ui.component.weightC

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CounterButtonNew(
    value: String,
    onValueDecreaseClick: () -> Unit = {},
    onValueIncreaseClick: () -> Unit = {},
    onValue: (String) -> Unit,
    onValueClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val intValue = value.toIntOrNull()?.coerceAtLeast(0) ?: 0

    Row(
        modifier = modifier.widthIn(min = 136.dp, max = 168.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        IconButton(
            onClick = {
                val newValue = (intValue - 1).coerceAtLeast(0).toString()
                onValue(newValue)
                onValueDecreaseClick()
            },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                val filtered = newValue.filter { it.isDigit() }
                if (filtered.isBlank()) {
                    onValue("0")
                } else {
                    onValue(filtered.toIntOrNull()?.coerceAtLeast(0)?.toString() ?: "0")
                }
            },
            singleLine = true,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center
            ),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        IconButton(
            onClick = {
                val newValue = (intValue + 1).toString()
                onValue(newValue)
                onValueIncreaseClick()
            },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "زیاد کردن"
            )
        }
    }
}

/*
 * برای سازگاری با کدهای قدیمی.
 */
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