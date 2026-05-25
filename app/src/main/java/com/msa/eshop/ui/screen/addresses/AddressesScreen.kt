package com.msa.eshop.ui.screen.addresses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.msa.eshop.R
import com.msa.eshop.data.Model.response.OrderAddressModel
import com.msa.eshop.ui.common.card.AddressCard
import com.msa.eshop.ui.common.topBar.TopBarDetails
import com.msa.eshop.ui.theme.PlatinumSilver

@Composable
fun AddressesScreen(
    modifier: Modifier = Modifier,
    addresses: List<OrderAddressModel> = emptyList(),
    onNewAddressClick: () -> Unit = {},
    onAddressClick: (OrderAddressModel) -> Unit = {}
) {
    val selectedAddressId = remember {
        mutableStateOf<String?>(null)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(PlatinumSilver),
        containerColor = PlatinumSilver,
        topBar = {
            TopBarDetails("آدرس‌ها")
        },
        bottomBar = {
            Button(
                onClick = onNewAddressClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.new_address),
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    ) { innerPadding ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            if (addresses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "هنوز آدرسی ثبت نشده است.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = addresses,
                        key = { it.id }
                    ) { address ->
                        AddressCard(
                            orderAddressModel = address,
                            onClick = {
                                selectedAddressId.value = it.id
                                onAddressClick(it)
                            },
                            isSelected = selectedAddressId.value == address.id
                        )
                    }
                }
            }
        }
    }
}