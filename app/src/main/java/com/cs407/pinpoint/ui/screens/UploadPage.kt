package com.cs407.pinpoint.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.cs407.pinpoint.ui.viewModels.LostItemsViewModel
import com.google.firebase.auth.auth
import com.cs407.pinpoint.domain.models.LostItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadPage(
    onBack : () -> Unit = {},
    viewModel: LostItemsViewModel = viewModel()
) {
    var itemName by remember { mutableStateOf<String?>(null) }
    var location by remember { mutableStateOf<String?>(null) }
    var description by remember { mutableStateOf<String?>(null) }
    var additionalInfo by remember { mutableStateOf<String?>(null) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var photoUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val user = Firebase.auth.currentUser

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

    val onSubmit: () -> Unit = {
        val lostItem = LostItem(
            user?.uid,
            itemName,
            location,
            description,
            additionalInfo
        )
        viewModel.submitLostItem(lostItem)
    }

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
            itemName = null
            location = null
            description = null
            additionalInfo = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Photo") },
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
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (photoUri == null) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    contentDescription = "Take photo",
                    modifier = Modifier
                        .size(120.dp)
                        .clickable { launcher.launch(imageUri) }
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(photoUri),
                    contentDescription = "Captured Photo",
                    modifier = Modifier
                        .size(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { launcher.launch(imageUri) },
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(36.dp))

            OutlinedTextField(
                value = itemName ?: "",
                onValueChange = { itemName = it },
                label = { Text("Item Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = location ?: "",
                onValueChange = { location = it },
                label = { Text("Location") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = description ?: "",
                onValueChange = { description = it },
                label = { Text("description") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = additionalInfo ?: "",
                onValueChange = { additionalInfo = it },
                label = { Text("Additional Info") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onSubmit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green,
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Cancel") }
        }
    }
}