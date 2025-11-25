package com.cs407.pinpoint.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.cs407.pinpoint.viewModels.LostItemsViewModel
import com.google.firebase.auth.auth
import com.cs407.pinpoint.domain.models.LostItem
import com.cs407.pinpoint.ui.theme.PinPointPrimary
import kotlinx.coroutines.launch

/**
 * Upload page for creating new lost item posts.
 *
 * Allows users to take a photo, enter item details (name, location, city, state, description),
 * and submit the lost item to Firebase. Includes form validation and success/error handling.
 *
 * @param onBack Callback to navigate back to previous screen
 * @param viewModel ViewModel for handling item submission
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadPage(
    onBack: () -> Unit = {},
    viewModel: LostItemsViewModel = viewModel()
) {
    var itemName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var selectedState by remember { mutableStateOf("Wisconsin") }
    var stateExpanded by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val user = Firebase.auth.currentUser

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

    // Create a temporary file to store the photo
    val imageFile = remember {
        File.createTempFile("captured_", ".jpg", context.cacheDir)
    }

    val imageUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            imageFile
        )
    }

    // Camera launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri = imageUri
        }
    }

    /**
     * Handles form submission.
     * Creates a LostItem with current timestamp and submits to Firebase.
     */
    val onSubmit: () -> Unit = {
        // Get current date in readable format
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val lostItem = LostItem(
            id = "", // Will be generated by Firebase
            ownerId = user?.uid ?: "",
            itemName = itemName,
            location = location,
            description = description,
            additionalInfo = "", // Empty since we removed the field
            city = city,
            state = selectedState,
            datePosted = currentDate,
            userName = user?.displayName ?: "Anonymous",
            imageUrl = "", // TODO: Upload photo to Firebase Storage
            latitude = 0.0, // TODO: Get from location picker
            longitude = 0.0
        )
        viewModel.submitLostItem(lostItem)
    }

    // Handle success/error messages
    LaunchedEffect(uiState.eventId) {
        if (uiState.error != null) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = uiState.error!!,
                    actionLabel = "Dismiss",
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
            }
        }
        if (uiState.successMsg != null) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = uiState.successMsg!!,
                    actionLabel = "Dismiss",
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
            }
            // Clear form after successful submission
            itemName = ""
            location = ""
            city = ""
            selectedState = "Wisconsin"
            description = ""
            photoUri = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Lost Item") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Photo capture section
            if (photoUri == null) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = "Take photo",
                    modifier = Modifier
                        .size(120.dp)
                        .clickable { launcher.launch(imageUri) },
                    tint = PinPointPrimary
                )
                Text(
                    "Tap to take photo",
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(photoUri),
                    contentDescription = "Captured Photo",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { launcher.launch(imageUri) },
                    contentScale = ContentScale.Crop
                )
                Text(
                    "Tap to retake photo",
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Item Name
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item Name *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // Specific Location
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Specific Location *") },
                placeholder = { Text("e.g., Memorial Union, Room 302") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // City
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("City *") },
                placeholder = { Text("e.g., Madison") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            // State Dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                ExposedDropdownMenuBox(
                    expanded = stateExpanded,
                    onExpandedChange = { stateExpanded = !stateExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedState,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("State *") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = stateExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
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
            Spacer(Modifier.height(12.dp))

            // Description (combined description and additional info)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description *") },
                placeholder = { Text("Describe the item and how to contact you...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 8
            )
            Spacer(Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = onSubmit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PinPointPrimary,
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Lost Item")
            }

            Spacer(Modifier.height(12.dp))

            // Cancel Button
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Cancel")
            }
        }
    }
}