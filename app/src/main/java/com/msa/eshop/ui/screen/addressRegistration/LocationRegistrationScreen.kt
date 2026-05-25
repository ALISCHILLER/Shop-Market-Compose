package com.msa.eshop.ui.screen.addressRegistration

import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.eshop.R
import com.msa.eshop.ui.common.card.BottomBoxLocationReqistrarion
import com.msa.eshop.ui.common.topBar.TopBarDetails
import com.msa.eshop.ui.component.dialog.ErrorDialog
import com.msa.eshop.ui.component.lottiefile.LoadingAnimate
import com.msa.eshop.ui.theme.PlatinumSilver
import com.msa.eshop.ui.theme.RedMain
import com.msa.eshop.utils.map.location.RequestLocationPermission
import com.msa.eshop.utils.map.osm.Coordinates
import com.msa.eshop.utils.map.osm.Marker
import com.msa.eshop.utils.map.osm.OpenStreetMap
import com.msa.eshop.utils.map.osm.model.LabelProperties
import com.msa.eshop.utils.map.osm.rememberCameraState
import com.msa.eshop.utils.map.osm.rememberMarkerState
import org.osmdroid.util.GeoPoint

@Composable
fun LocationRegistrationScreen(
    modifier: Modifier = Modifier,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    val cameraState = rememberCameraState {
        geoPoint = Coordinates.defaultLocation
        zoom = 6.0
    }

    val markerState = rememberMarkerState(
        geoPoint = Coordinates.defaultLocation,
        rotation = 0f
    )

    val markerIcon: Drawable? by remember {
        mutableStateOf(context.getDrawable(R.drawable.round_eject_24))
    }

    val labelProperties = remember {
        mutableStateOf(
            LabelProperties(
                labelColor = android.graphics.Color.RED,
                labelTextSize = 40f,
                labelAlign = Paint.Align.CENTER,
                labelTextOffset = 30f
            )
        )
    }

    LaunchedEffect(uiState.location) {
        uiState.location?.let { location ->
            val geoPoint = GeoPoint(
                location.latitude,
                location.longitude
            )

            cameraState.geoPoint = geoPoint
            cameraState.zoom = 15.0
            markerState.geoPoint = geoPoint
        }
    }

    uiState.errorMessage?.let { error ->
        ErrorDialog(
            error,
            { viewModel.clearError() },
            false
        )
    }

    RequestLocationPermission { granted ->
        viewModel.onPermissionResult(granted)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PlatinumSilver)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = PlatinumSilver,
            topBar = {
                TopBarDetails("ثبت موقعیت مکانی")
            },
            bottomBar = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    BottomBoxLocationReqistrarion(
                        onClickNavigateToAddress = {
                            viewModel.navigateToAddressRegistration()
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                OpenStreetMap(
                    modifier = Modifier.fillMaxSize(),
                    onMapClick = { geoPoint ->
                        markerState.geoPoint = geoPoint
                        cameraState.geoPoint = geoPoint
                    },
                    cameraState = cameraState
                ) {
                    Marker(
                        state = markerState,
                        icon = markerIcon,
                        title = "موقعیت انتخاب‌شده",
                        snippet = "برای تغییر، روی نقشه لمس کنید"
                    ) { marker ->
                        Column(
                            modifier = Modifier
                                .size(120.dp)
                                .background(
                                    color = Color.White,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = marker.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = marker.snippet,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                FloatingActionButton(
                    onClick = {
                        viewModel.restartLocationUpdates()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    containerColor = RedMain
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "دریافت موقعیت فعلی",
                        tint = Color.White
                    )
                }
            }
        }

        if (uiState.isLoading) {
            LoadingAnimate()
        }
    }
}