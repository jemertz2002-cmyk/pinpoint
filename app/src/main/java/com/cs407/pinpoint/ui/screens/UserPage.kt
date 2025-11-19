package com.cs407.pinpoint.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.cs407.pinpoint.ui.theme.BackgroundMint
import com.cs407.pinpoint.ui.theme.ButtonRed
import com.cs407.pinpoint.ui.theme.PinPointGreen
import com.cs407.pinpoint.ui.theme.PinPointGreenLight
import com.cs407.pinpoint.ui.viewModels.ItemViewModel
import com.cs407.pinpoint.ui.viewModels.PinPointItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Composable
fun UserPage(
    navController: NavHostController,
    onBack: () -> Unit,
    // using ViewModel here to separate UI code from data logic.
    viewModel: ItemViewModel = viewModel()
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    // used LaunchedEffect so data fetch only runs once when user enters the screen.
    LaunchedEffect(currentUser) {
        viewModel.loadItemsForUser(currentUser?.email)
    }

    // Observing StateFlow from ViewModel.
    // List updates automatically if items are deleted/added.
    val allItems by viewModel.uiState.collectAsState()

    // State for tracking active tab (Lost vs Found)
    var selectedTab by remember { mutableStateOf("Lost") }

    // Filtering list based on tab selection to show relevant items
    val displayedItems = allItems.filter { item ->
        item.type == selectedTab
    }

    Surface(
        color = BackgroundMint,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileTopBar(onBack = onBack)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Displaying user profile info (picture, email, name)
                item {
                    ProfileInfoCard(user = currentUser)
                }

                // Tab switcher for filtering the list
                item {
                    TabButtons(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }

                // Using filtered list from ViewModel state
                items(displayedItems) { item ->
                    ItemPostCard(
                        item = item,
                        // Passing events to ViewModel instead of handling logic in UI
                        onMarkFound = { viewModel.markAsFound(item.id) },
                        onDelete = { viewModel.deletePost(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PinPointGreenLight)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }
        Text(
            text = "My Profile",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .weight(1f)
                .padding(end = 48.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ProfileInfoCard(user: FirebaseUser?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Placeholder icon, planning to add actual profile image upload later
            Image(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = user?.displayName ?: "Bucky Badger",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = user?.email ?: "bbadger@wisc.edu",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun TabButtons(selectedTab: String, onTabSelected: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TabButton(
            modifier = Modifier.weight(1f),
            text = "My Lost Items",
            isSelected = selectedTab == "Lost",
            onClick = { onTabSelected("Lost") }
        )
        TabButton(
            modifier = Modifier.weight(1f),
            text = "My Found Items",
            isSelected = selectedTab == "Found",
            onClick = { onTabSelected("Found") }
        )
    }
}

@Composable
fun TabButton(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Visual feedback for selected tab
    val backgroundColor = if (isSelected) PinPointGreen else Color.White
    val contentColor = if (isSelected) Color.Black else Color.Gray

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(if (isSelected) 4.dp else 2.dp)
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ItemPostCard(
    item: PinPointItem,
    onMarkFound: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Item Name: ${item.itemName}", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Location: ${item.location}", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Date Posted: ${item.datePosted}", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "User: ${item.user}", fontSize = 14.sp)
                }

                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "Item Image",
                    modifier = Modifier.size(80.dp),
                    tint = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Calls lambda function passed from parent composable
                Button(
                    onClick = onMarkFound,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PinPointGreen,
                        contentColor = Color.Black
                    )
                ) {
                    Text(text = "Mark as Found")
                }

                Spacer(modifier = Modifier.width(16.dp))

                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ButtonRed
                    ),
                    border = BorderStroke(1.dp, ButtonRed)
                ) {
                    Text(text = "Delete Post")
                }
            }
        }
    }
}