package com.cs407.pinpoint.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.cs407.pinpoint.ui.theme.PinPointPrimary
import com.google.firebase.auth.FirebaseAuth

/**
 * Login page composable that handles user authentication.
 *
 * Displays a login form with email and password fields, validates user input,
 * and authenticates users through Firebase Authentication. Includes error handling,
 * loading states, and navigation to other screens.
 *
 * @param onSuccess Callback function invoked when user successfully logs in
 * @param onNavigateToSignUp Callback function to navigate to the sign-up page
 * @param onBack Callback function to navigate back to the landing page
 */
@Composable
fun LoginPage(
    onSuccess: () -> Unit = {},
    onNavigateToSignUp: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    /**
     * Validates email address format using Android's built-in email pattern matcher.
     *
     * @param email The email string to validate
     * @return true if the email format is valid, false otherwise
     */
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
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
            Text("Login to PinPoint", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            // Email input field with validation
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    error = null
                },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                isError = email.isNotEmpty() && !isValidEmail(email),
                supportingText = {
                    if (email.isNotEmpty() && !isValidEmail(email)) {
                        Text("Please enter a valid email address", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
            Spacer(Modifier.height(8.dp))

            // Password input field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    error = null
                },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )
            Spacer(Modifier.height(16.dp))

            // Login button with Firebase authentication
            Button(
                onClick = {
                    when {
                        email.isBlank() -> error = "Email cannot be empty"
                        !isValidEmail(email) -> error = "Please enter a valid email address"
                        password.isBlank() -> error = "Password cannot be empty"
                        else -> {
                            isLoading = true
                            error = null

                            // Authenticate user with Firebase
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener {
                                    isLoading = false
                                    onSuccess()
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    // Parse Firebase errors into user-friendly messages
                                    error = when {
                                        e.message?.contains("no user record") == true ||
                                                e.message?.contains("invalid-credential") == true ||
                                                e.message?.contains("wrong-password") == true ->
                                            "Invalid email or password"
                                        e.message?.contains("network error") == true ->
                                            "Network error. Please check your connection"
                                        e.message?.contains("too-many-requests") == true ->
                                            "Too many failed attempts. Please try again later"
                                        else -> e.message ?: "Login failed"
                                    }
                                }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PinPointPrimary
                ),
                enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(24.dp),
                        color = Color.Black
                    )
                } else {
                    Text("Login")
                }
            }

            Spacer(Modifier.height(8.dp))

            // Navigate to sign-up page
            TextButton(
                onClick = onNavigateToSignUp,
                enabled = !isLoading
            ) {
                Text(
                    text = "Don't have an account? Sign Up",
                    color = Color.Black
                )
            }

            // Display error messages
            error?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}