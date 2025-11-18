package com.cs407.pinpoint.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.cs407.pinpoint.ui.theme.PinPointPrimary
import com.cs407.pinpoint.ui.theme.PinPointGreenAccent
import com.cs407.pinpoint.ui.theme.PinPointSecondary
import com.cs407.pinpoint.ui.theme.PinPointSurface
import com.cs407.pinpoint.ui.theme.TextPrimary
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

data class LostItem(
    val id: String,
    val name: String,
    val location: String,
    val datePosted: String,
    val user: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    onNavigateToItem: (String) -> Unit = {},
    onNavigateToUser: () -> Unit = {},
    onNavigateToUpload: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }


    val recentItems = remember {
        listOf(
            LostItem("1", "Wallet", "Memorial Union", "Nov 14, 2025", "John Doe"),
            LostItem("2", "Keys", "Engineering Hall", "Nov 13, 2025", "Jane Smith"),
            LostItem("3", "Phone", "Library", "Nov 12, 2025", "Mike Johnson")
        )
    }

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
                HorizontalDivider(Modifier.padding(16.dp), DividerDefaults.Thickness, DividerDefaults.color)

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home Page Navigation Button") },
                    label = { Text("Home") },
                    selected = true,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
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

                    // Search bar with location
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = PinPointSecondary
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Search TextField
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier
                                    .weight(1f),
                                placeholder = { Text("City, State", color = Color.Gray) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = Color.Gray
                                    )
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White.copy(alpha = 0.9f),
                                    unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )

                            // Location indicator
                            Row(
                                modifier = Modifier
                                    .background(
                                        Color.White.copy(alpha = 0.9f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    "Madison, WI",
                                    modifier = Modifier.padding(start = 4.dp),
                                    color = TextPrimary,
                                    fontSize = 12.sp
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
                Text(
                    "Recently Lost Items:",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recentItems) { item ->
                        LostItemCard(item, onClick = { onNavigateToItem(item.name) })
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
                    "Item Name",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    "Location:",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    "Date Posted:",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    "User:",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Column(
                modifier = Modifier.width(100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Lost Item",
                    modifier = Modifier.size(48.dp),
                    tint = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Lost Item\nImage",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    lineHeight = 12.sp
                )
            }
        }
    }
}