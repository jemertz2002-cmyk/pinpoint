package com.cs407.pinpoint.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

import com.cs407.pinpoint.ui.viewModels.MapViewModel
import com.cs407.pinpoint.ui.viewModels.Mode

@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val selectedMarker by viewModel.selectedMarker.collectAsStateWithLifecycle()

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            if (fineLocationGranted || coarseLocationGranted) {
                viewModel.updateLocationPermission(granted = true)
                viewModel.getCurrentLocation()
            } else {
                viewModel.updateLocationPermission(granted = false)
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.initializeLocationClient(context)
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val defaultLocation = LatLng(43.0731, -89.4012)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    LaunchedEffect(uiState.currentLocation) {
        uiState.currentLocation?.let { location ->
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(location, 15f),
                durationMs = 1000
            )
        }
    }

    LaunchedEffect(uiState.mode) {
        when (uiState.mode) {
            Mode.ADD_MARKER -> snackbarHostState.showSnackbar("Tap anywhere on the map to add a marker.")
            Mode.DELETE_MARKER -> snackbarHostState.showSnackbar("Tap a marker to delete it.")
            Mode.NORMAL -> snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { clickedPosition ->
                    when (uiState.mode) {
                        Mode.ADD_MARKER -> viewModel.addMarker(clickedPosition)
                        Mode.DELETE_MARKER -> viewModel.exitMode()
                        Mode.NORMAL -> {
                            viewModel.clearSelection()
                        }
                    }
                }
            ) {
                uiState.currentLocation?.let { location ->
                    MarkerComposable(
                        state = MarkerState(position = location),
                        content = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(color = Color.Blue, shape = CircleShape)
                                    .border(width = 3.dp, color = Color.White, shape = CircleShape)
                            )
                        }
                    )
                }

                uiState.markers.forEach { markerPosition ->
                    Marker(
                        state = MarkerState(position = markerPosition),
                        title = "Lat: %.5f, Lon: %.5f".format(
                            markerPosition.latitude,
                            markerPosition.longitude
                        ),
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                        onClick = { marker ->
                            when (uiState.mode) {
                                Mode.DELETE_MARKER -> {
                                    viewModel.removeMarker(markerPosition)
                                }
                                else -> {
                                    marker.showInfoWindow()
                                    coroutineScope.launch {
                                        cameraPositionState.animate(
                                            update = CameraUpdateFactory.newLatLngZoom(markerPosition, 14f),
                                            durationMs = 800
                                        )
                                    }
                                    viewModel.selectMarker(markerPosition)
                                }
                            }
                            true
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                SmallFloatingActionButton(onClick = { viewModel.enterAddMarkerMode() }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Marker")
                }
                SmallFloatingActionButton(onClick = { viewModel.enterDeleteMarkerMode() }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Marker")
                }
                SmallFloatingActionButton(
                    onClick = {
                        uiState.currentLocation?.let { location ->
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(location, 15f)
                                )
                            }
                        }
                        viewModel.clearSelection()
                        viewModel.exitMode()
                    }
                ) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Recenter")
                }
            }
        }
    }
}