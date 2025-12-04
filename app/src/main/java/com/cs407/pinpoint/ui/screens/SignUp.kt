package com.cs407.pinpoint.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.pinpoint.ui.theme.PinPointPrimary
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.firestore

/**
 * Sign up page composable that handles new user registration.
 *
 * Displays a registration form with email, username, password, and password confirmation fields.
 * Also includes Google Sign-In option. Validates user input, creates a new Firebase Authentication
 * account, and stores the username in the user's Firebase profile.
 *
 * @param onSuccess Callback function invoked when user successfully signs up
 * @param onNavigateToLogin Callback function to navigate to the login page
 * @param onBack Callback function to navigate back to the landing page
 */
@Composable
fun SignUpPage(
    onSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = Firebase.firestore
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Google Sign-In configuration
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1076119988683-klepr015fnaleihffv98vhnfvj7lnpto.apps.googleusercontent.com")
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, gso)
    }

    // Google Sign-In launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                isLoading = true
                auth.signInWithCredential(credential)
                    .addOnSuccessListener { authResult ->
                        val user = authResult.user
                        if (user != null) {
                            // Create user document in Firestore
                            val userDoc = hashMapOf(
                                "uid" to user.uid,
                                "email" to user.email,
                                "displayName" to (user.displayName ?: account.displayName),
                                "createdAt" to Timestamp.now(),
                                "isActive" to true
                            )

                            firestore.collection("users")
                                .document(user.uid)
                                .set(userDoc)
                                .addOnSuccessListener {
                                    isLoading = false
                                    onSuccess()
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    error = "Failed to create user profile: ${e.message}"
                                }
                        } else {
                            isLoading = false
                            error = "Failed to get user information"
                        }
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                        error = e.message ?: "Google Sign-In failed"
                    }
            } catch (e: ApiException) {
                isLoading = false
                error = "Google Sign-In failed: ${e.message}"
            }
        } else {
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Back button in top-left corner
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back to Landing",
                tint = Color.Black
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Sign Up for PinPoint", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            // Google Sign-In Button
            OutlinedButton(
                onClick = {
                    // Sign out first to force account picker
                    googleSignInClient.signOut().addOnCompleteListener {
                        val signInIntent = googleSignInClient.signInIntent
                        googleSignInLauncher.launch(signInIntent)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("G", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))
                Spacer(Modifier.width(8.dp))
                Text("Sign up with Google", color = Color.Black)
            }

            Spacer(Modifier.height(16.dp))

            // Divider with "OR"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    "OR",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))

            // Email input field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            Spacer(Modifier.height(8.dp))

            // Username input field
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            Spacer(Modifier.height(8.dp))

            // Password input field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            Spacer(Modifier.height(8.dp))

            // Password confirmation field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            Spacer(Modifier.height(16.dp))

            // Sign up button with Firebase account creation
            Button(
                onClick = {
                    // Validate all input fields before creating account
                    when {
                        email.isBlank() -> error = "Email cannot be empty"
                        username.isBlank() -> error = "Username cannot be empty"
                        password.isBlank() -> error = "Password cannot be empty"
                        password.length < 6 -> error = "Password must be at least 6 characters"
                        password != confirmPassword -> error = "Passwords do not match"
                        else -> {
                            isLoading = true
                            error = null

                            // Create new Firebase Authentication user
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener { authResult ->
                                    val user = authResult.user

                                    // Store username in Firebase user profile
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(username)
                                        .build()

                                    user?.updateProfile(profileUpdates)
                                        ?.addOnSuccessListener {
                                            // Create user document in Firestore
                                            val userDoc = hashMapOf(
                                                "uid" to user.uid,
                                                "email" to user.email,
                                                "displayName" to username,
                                                "createdAt" to Timestamp.now(),
                                                "isActive" to true
                                            )

                                            firestore.collection("users")
                                                .document(user.uid)
                                                .set(userDoc)
                                                .addOnSuccessListener {
                                                    isLoading = false
                                                    onSuccess()
                                                }
                                                .addOnFailureListener { e ->
                                                    isLoading = false
                                                    error = "Failed to create user profile: ${e.message}"
                                                }
                                        }
                                        ?.addOnFailureListener { e ->
                                            isLoading = false
                                            error = "Failed to set username: ${e.message}"
                                        }
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    // Parse Firebase errors into user-friendly messages
                                    error = when {
                                        e.message?.contains("email address is already in use") == true ->
                                            "This email is already registered"
                                        e.message?.contains("email address is badly formatted") == true ->
                                            "Invalid email address"
                                        else -> e.message ?: "Sign up failed"
                                    }
                                }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PinPointPrimary
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(24.dp),
                        color = Color.Black
                    )
                } else {
                    Text("Sign Up")
                }
            }

            Spacer(Modifier.height(8.dp))

            // Navigate to login page
            TextButton(
                onClick = onNavigateToLogin,
                enabled = !isLoading
            ) {
                Text(
                    text = "Already have an account? Login here",
                    color = Color.Black
                )
            }

            // Display error messages
            error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}