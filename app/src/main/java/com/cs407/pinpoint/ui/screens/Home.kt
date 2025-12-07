package com.cs407.pinpoint.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.cs407.pinpoint.domain.models.LostItem
import com.cs407.pinpoint.ui.theme.PinPointGreenAccent
import com.cs407.pinpoint.ui.theme.PinPointPrimary
import com.cs407.pinpoint.ui.theme.PinPointSecondary
import com.cs407.pinpoint.ui.theme.PinPointSurface
import com.cs407.pinpoint.ui.theme.TextPrimary
import com.cs407.pinpoint.ui.viewModels.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    onNavigateToItem: (String) -> Unit = {},
    onNavigateToUser: () -> Unit = {},
    onNavigateToUpload: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Collect data from ViewModel
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Location states
    var cityInput by remember { mutableStateOf("") }
    var selectedState by remember { mutableStateOf("Wisconsin") }
    var stateExpanded by remember { mutableStateOf(false) }
    var isNearbyMode by remember { mutableStateOf(false) }

    // US States list
    val states = listOf(
        "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado",
        "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho",
        "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana",
        "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota",
        "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada",
        "New Hampshire", "New Jersey", "New Mexico", "New York",
        "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon",
        "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota",
        "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington",
        "West Virginia", "Wisconsin", "Wyoming"
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "PinPoint",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider(
                    Modifier.padding(16.dp),
                    DividerDefaults.Thickness,
                    DividerDefaults.color
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home Page Navigation Button") },
                    label = { Text("Home") },
                    selected = true,
                    onClick = { scope.launch { drawerState.close() } },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Add, contentDescription = "Upload Page Navigation Button") },
                    label = { Text("Upload Item") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            onNavigateToUpload()
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "User Profile Navigation Button") },
                    label = { Text("My Profile") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            onNavigateToUser()
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings Page Navigation Button") },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            onNavigateToSettings()
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                Column {
                    // App bar with title
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PinPointGreenAccent,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } }
                            ) {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = TextPrimary
                                )
                            }
                            Text(
                                "PinPoint",
                                modifier = Modifier.padding(start = 8.dp),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    // Location selection: City text field + State dropdown + buttons
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PinPointSecondary
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // City Text Field
                            TextField(
                                value = cityInput,
                                onValueChange = { cityInput = it },
                                modifier = Modifier
                                    .weight(1.2f)
                                    .height(48.dp),
                                placeholder = { Text("Enter city", fontSize = 14.sp) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Search City",
                                        tint = Color.Gray
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    disabledContainerColor = Color.White,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    cursorColor = PinPointPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // State Dropdown
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                ExposedDropdownMenuBox(
                                    expanded = stateExpanded,
                                    onExpandedChange = { stateExpanded = !stateExpanded }
                                ) {
                                    TextField(
                                        value = selectedState,
                                        onValueChange = {},
                                        readOnly = true,
                                        placeholder = { Text("State", fontSize = 12.sp) },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = stateExpanded
                                            )
                                        },
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White,
                                            disabledContainerColor = Color.White,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            disabledIndicatorColor = Color.Transparent,
                                            cursorColor = PinPointPrimary
                                        ),
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true
                                    )

                                    ExposedDropdownMenu(
                                        expanded = stateExpanded,
                                        onDismissRequest = { stateExpanded = false }
                                    ) {
                                        states.forEach { state ->
                                            DropdownMenuItem(
                                                text = { Text(state) },
                                                onClick = {
                                                    selectedState = state
                                                    stateExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Search button to apply filter
                            FilledIconButton(
                                onClick = {
                                    isNearbyMode = false
                                    if (cityInput.isNotBlank()) {
                                        viewModel.filterByLocation(cityInput, selectedState)
                                    } else {
                                        viewModel.filterByLocation("", selectedState)
                                    }
                                },
                                modifier = Modifier.size(48.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = PinPointPrimary,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search Location"
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Quick "Near Me" button
                            FilledIconButton(
                                onClick = {
                                    isNearbyMode = true
                                    viewModel.loadNearbyItems()
                                },
                                modifier = Modifier.size(48.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = PinPointPrimary,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    Icons.Default.MyLocation,
                                    contentDescription = "Show items near me"
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(PinPointSurface)
            ) {
                // Show current filter info
                if (isNearbyMode || cityInput.isNotBlank() || selectedState != "Wisconsin") {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        color = PinPointPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = when {
                                    isNearbyMode -> "Showing items near you"
                                    cityInput.isNotBlank() -> "Showing items in $cityInput, $selectedState"
                                    else -> "Showing items in $selectedState"
                                },
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            TextButton(
                                onClick = {
                                    isNearbyMode = false
                                    cityInput = ""
                                    selectedState = "Wisconsin"
                                    viewModel.refresh()
                                }
                            ) {
                                Text("Clear", fontSize = 12.sp)
                            }
                        }
                    }
                }

                Text(
                    "Recently Lost Items:",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                // Show loading indicator
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PinPointPrimary)
                    }
                }

                // Show error message
                error?.let { errorMessage ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                            Button(onClick = { viewModel.refresh() }) {
                                Text("Retry")
                            }
                        }
                    }
                }

                // Show items list
                if (!isLoading && error == null) {
                    if (items.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = when {
                                        isNearbyMode -> "No items found near you"
                                        cityInput.isNotBlank() -> "No items found in $cityInput, $selectedState"
                                        else -> "No items found in $selectedState"
                                    },
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(onClick = {
                                    isNearbyMode = false
                                    cityInput = ""
                                    selectedState = "Wisconsin"
                                    viewModel.refresh()
                                }) {
                                    Text("View all items")
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(items) { item ->
                                LostItemCard(item, onClick = { onNavigateToItem(item.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostItemCard(item: LostItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Item: ${item.itemName}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    "Location: ${item.location}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    "City: ${item.city}, ${item.state}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    "Posted: ${item.datePosted}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // Image display
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(88.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.itemName,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = "Lost Item",
                        modifier = Modifier.size(48.dp),
                        tint = TextPrimary
                    )
                }
            }
        }
    }
}
