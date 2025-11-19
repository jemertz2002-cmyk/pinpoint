package com.cs407.pinpoint.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.pinpoint.R
import com.cs407.pinpoint.ui.theme.PinPointGreenAccent
import com.cs407.pinpoint.ui.theme.PinPointBackground
import com.cs407.pinpoint.ui.theme.PinPointSurface
import com.cs407.pinpoint.ui.theme.TextPrimary
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.remember

/**
 * Item page composable UI function that creates a structure for displaying an item card
 *
 * args:
 * itemName: A string to display on the topBar
 * onBack(): A navBack function that gets called on in the navigation back Icon
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemPage(
    itemName: String = "ITEM NAME HERE",
    onBack: () -> Unit = {},
    latitude: Double = 43.0731, // Default to Madison, WI
    longitude: Double = -89.4012
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(itemName, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    // keep a visual balance so the title appears centered
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PinPointGreenAccent
                )
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(PinPointBackground),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ItemCard(
                    itemName = itemName,
                    latitude = latitude,
                    longitude = longitude
                )
            }
        }
    )
}

/**
 * ItemCard composable UI function creates an Elevated Card that displays all lost card information
 * pulled from the database. Displays an image from imageRes, and a google maps instance with a marker
 *
 * args:
 * imageRes: Int = image resource to display or default
 * itemName: String = name to be displayed
 * latitude: Double = needed for LatLong()
 * longitude: Double = needed for LatLong()
 */
@Composable
fun ItemCard(
    /* Dynamic name and img URL to make it reusable*/
    modifier: Modifier = Modifier,
    imageRes: Int = R.drawable.ic_launcher_foreground,
    itemName: String = "Dynamic Item Name",
    latitude: Double = 43.0731, // Default to Madison, WI
    longitude: Double = -89.4012
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier
                .wrapContentHeight()
                .padding(24.dp),
            shape = MaterialTheme.shapes.extraLarge,
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = PinPointSurface
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    itemName,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(0.dp, 8.dp),
                    color = TextPrimary
                )
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = itemName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp, 16.dp)
                        .size(500.dp, 300.dp)
                        .border(
                            width = 1.dp,
                            color = TextPrimary,
                            shape = MaterialTheme.shapes.medium // border radius
                        )
                )
                Spacer(Modifier.width(16.dp))
            }
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ){
                Row(){
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = "Phone Icon",
                        tint = TextPrimary
                    )
                    Text(
                        "Contact Information: (Phone or Email)",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp),
                        color = TextPrimary
                    )
                }
                Row(){
                    Icon(
                        Icons.Default.AccountBox,
                        contentDescription ="User Name",
                        tint = TextPrimary
                    )
                    Text(
                        "UserName : default_user",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp),
                        color = TextPrimary
                    )
                }
                // Google Map showing where the item was found
                val itemLocation = remember { LatLng(latitude, longitude) }
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(itemLocation, 15f)
                }
                
                Text(
                    "Location:",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 8.dp),
                    color = TextPrimary
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = true,
                            mapToolbarEnabled = false,
                            scrollGesturesEnabled = true,
                            zoomGesturesEnabled = true,
                            rotationGesturesEnabled = false,
                            tiltGesturesEnabled = false
                        )
                    ) {
                        Marker(
                            state = MarkerState(position = itemLocation),
                            title = "Item Location"
                        )
                    }
                }
            }
        }
    }
}