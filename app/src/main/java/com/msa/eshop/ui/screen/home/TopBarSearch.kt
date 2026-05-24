package com.msa.eshop.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.msa.eshop.R
import com.msa.eshop.ui.component.DockedSearch

@Composable
fun TopBarSearch(
    modifier: Modifier = Modifier,
    onSearchChange: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 110.dp, max = 130.dp)
            .background(Color.White)
            .padding(
                top = 16.dp,
                bottom = 8.dp
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo",
            modifier = Modifier
                .size(width = 130.dp, height = 42.dp)
                .layoutId("logo")
        )

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            DockedSearch(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                onQueryChange = onSearchChange
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TopBarSearchPreview() {
    TopBarSearch(
        onSearchChange = {}
    )
}