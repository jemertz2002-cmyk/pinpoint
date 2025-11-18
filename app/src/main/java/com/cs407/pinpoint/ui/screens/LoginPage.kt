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

    // Email validation
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

            Button(
                onClick = {
                    when {
                        email.isBlank() -> error = "Email cannot be empty"
                        !isValidEmail(email) -> error = "Please enter a valid email address"
                        password.isBlank() -> error = "Password cannot be empty"
                        else -> {
                            isLoading = true
                            error = null

                            auth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener {
                                    isLoading = false
                                    onSuccess()
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
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

            TextButton(
                onClick = onNavigateToSignUp,
                enabled = !isLoading
            ) {
                Text(
                    text = "Don't have an account? Sign Up",
                    color = Color.Black
                )
            }

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