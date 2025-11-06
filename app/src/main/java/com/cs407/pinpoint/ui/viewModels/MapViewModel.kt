package com.cs407.pinpoint.ui.viewModels

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs

enum class Mode {
    NORMAL,
    ADD_MARKER,
    DELETE_MARKER
}

data class MapState(
    val markers: List<LatLng> = emptyList(),
    val currentLocation: LatLng? = null,
    val locationPermissionGranted: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val mode: Mode = Mode.NORMAL
)

class MapViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MapState())
    val uiState = _uiState.asStateFlow()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val _selectedMarker = MutableStateFlow<LatLng?>(null)
    val selectedMarker = _selectedMarker.asStateFlow()

    private fun isGoogleDefault(location: LatLng): Boolean {
        val googleLat = 37.4219983
        val googleLon = -122.084
        val tolerance = 1e-4
        return abs(location.latitude - googleLat) < tolerance &&
                abs(location.longitude - googleLon) < tolerance
    }

    fun initializeLocationClient(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        if (!_uiState.value.locationPermissionGranted) {
            _uiState.value = _uiState.value.copy(error = "Location permission not granted.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { location ->
                        val receivedLocation = location?.let { LatLng(it.latitude, it.longitude) }

                        val finalLocation = when {
                            receivedLocation == null -> LatLng(43.0731, -89.4012)
                            isGoogleDefault(receivedLocation) -> LatLng(43.0731, -89.4012)
                            else -> receivedLocation
                        }

                        _uiState.value = _uiState.value.copy(
                            currentLocation = finalLocation,
                            isLoading = false
                        )
                    }
                    .addOnFailureListener {
                        _uiState.value = _uiState.value.copy(
                            error = it.message ?: "Failed to get location.",
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error getting location.",
                    isLoading = false
                )
            }
        }
    }

    fun updateLocationPermission(granted: Boolean) {
        _uiState.value = _uiState.value.copy(locationPermissionGranted = granted)
    }

    fun enterAddMarkerMode() {
        _uiState.value = _uiState.value.copy(mode = Mode.ADD_MARKER)
    }

    fun enterDeleteMarkerMode() {
        _uiState.value = _uiState.value.copy(mode = Mode.DELETE_MARKER)
    }

    fun exitMode() {
        _uiState.value = _uiState.value.copy(mode = Mode.NORMAL)
    }

    fun addMarker(position: LatLng) {
        val existing = _uiState.value.markers.any { it.latitude == position.latitude && it.longitude == position.longitude }
        if (existing) {
            _uiState.value = _uiState.value.copy(mode = Mode.NORMAL)
            return
        }
        val currentMarkers = _uiState.value.markers.toMutableList()
        currentMarkers.add(position)
        _uiState.value = _uiState.value.copy(markers = currentMarkers, mode = Mode.NORMAL)
    }

    fun removeMarker(marker: LatLng) {
        val currentMarkers = _uiState.value.markers.toMutableList()
        currentMarkers.remove(marker)
        _uiState.value = _uiState.value.copy(markers = currentMarkers, mode = Mode.NORMAL)
    }

    fun selectMarker(marker: LatLng) {
        _selectedMarker.value = marker
    }

    fun clearSelection() {
        _selectedMarker.value = null
    }
}