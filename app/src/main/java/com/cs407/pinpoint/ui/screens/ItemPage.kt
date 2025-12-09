package com.cs407.pinpoint.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cs407.pinpoint.R
import com.cs407.pinpoint.ui.theme.PinPointGreenAccent
import com.cs407.pinpoint.ui.theme.PinPointBackground
import com.cs407.pinpoint.ui.theme.PinPointPrimary
import com.cs407.pinpoint.ui.theme.PinPointSurface
import com.cs407.pinpoint.ui.theme.TextPrimary
import com.cs407.pinpoint.ui.viewModels.ItemPageViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage

/**
 * Item page composable UI function that creates a structure for displaying an item card
 *
 * args:
 * itemId: The ID of the item to display
 * onBack(): A navBack function that gets called on in the navigation back Icon
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemPage(
    itemId: String,
    onBack: () -> Unit = {},
    viewModel: ItemPageViewModel = viewModel()
) {
    val item by viewModel.item.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var showLocationDialog by remember { mutableStateOf(false) }

    // Load item when composable is first created
    LaunchedEffect(itemId) {
        viewModel.loadItem(itemId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(item?.itemName ?: "Loading...", color = TextPrimary) },
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
        modifier = Modifier.fillMaxSize(),
        content = { padding ->
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PinPointPrimary)
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = error ?: "Error loading item",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                item != null -> {
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .background(PinPointBackground)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        ItemCard(item = item!!)
                    }
                }
            }
        }
    )
}

/**
 * ItemCard composable UI function creates an Elevated Card that displays all lost card information
 * pulled from the database. Displays an image from imageUrl, and a google maps instance with a marker
 *
 * args:
 * item: LostItem = the lost item to display
 */
@Composable
fun ItemCard(
    modifier: Modifier = Modifier,
    item: com.cs407.pinpoint.domain.models.LostItem
) {
    var showLocationDialog by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .wrapContentHeight()
            .padding(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.padding(24.dp),
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
                    item.itemName,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(0.dp, 8.dp),
                    color = TextPrimary
                )
                // Display image from imageUrl
                if (item.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.itemName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(16.dp, 16.dp)
                            .height(300.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = 1.dp,
                                color = TextPrimary,
                                shape = MaterialTheme.shapes.medium
                            ),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback if no image
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = item.itemName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(16.dp, 16.dp)
                            .size(500.dp, 300.dp)
                            .border(
                                width = 1.dp,
                                color = TextPrimary,
                                shape = MaterialTheme.shapes.medium
                            )
                    )
                }
                Spacer(Modifier.width(16.dp))
            }
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Only show contact info if it's been given
                if (!item.contactInfo.isBlank()) {
                    Row {
                        Icon(
                            Icons.Default.Phone,
                            contentDescription = "Phone Icon",
                            tint = TextPrimary
                        )
                        Text(
                            "Contact Information: ${item.contactInfo}",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp),
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row {
                    Icon(
                        Icons.Default.AccountBox,
                        contentDescription = "User Name",
                        tint = TextPrimary
                    )
                    Text(
                        "UserName: ${item.userName}",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp),
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = "Description of Item",
                        tint = TextPrimary
                    )
                    Text(
                        "Description: ${item.description}",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp),
                        color = TextPrimary
                    )
                }

                Button(
                    onClick = { showLocationDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text("Show Location")
                }

                if (showLocationDialog) {
                    ItemLocationDialog(item, { showLocationDialog = false })
                }
            }
        }
    }
}

@Composable
fun ItemLocationDialog(
    item: com.cs407.pinpoint.domain.models.LostItem,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Item Location") },
        text = {
            Box(
                modifier = Modifier
                    .height(300.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    val itemLocation = remember {
                        LatLng(
                            if (item.latitude != 0.0) item.latitude else 43.0731,
                            if (item.longitude != 0.0) item.longitude else -89.4012
                        )
                    }

                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(itemLocation, 15f)
                    }

                    Text(
                        "Location: ${item.location}",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 8.dp),
                        color = TextPrimary
                    )

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
        },
        confirmButton = {},
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Back")
            }
        }
    )
}