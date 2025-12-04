package com.cs407.pinpoint

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs407.pinpoint.ui.screens.HomePage
import com.cs407.pinpoint.ui.screens.ItemPage
import com.cs407.pinpoint.ui.screens.LandingPage
import com.cs407.pinpoint.ui.screens.LoginPage
import com.cs407.pinpoint.ui.screens.SettingsPage
import com.cs407.pinpoint.ui.screens.SignUpPage
import com.cs407.pinpoint.ui.screens.UploadPage
import com.cs407.pinpoint.ui.screens.UserPage

@Composable
fun PinPointApp(
    darkTheme: Boolean,
    onDarkThemeToggle: (Boolean) -> Unit,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable("landing") {
            LandingPage(
                onLogin = { navController.navigate("login") },
                onSignUp = { navController.navigate("signup") }
            )
        }
        composable("signup") {
            SignUpPage(
                onSuccess = { navController.navigate("home") },
                onNavigateToLogin = { navController.navigate("login") },
                onBack = { navController.navigate("landing") }
            )
        }
        composable("login") {
            LoginPage(
                onSuccess = { navController.navigate("home") },
                onNavigateToSignUp = { navController.navigate("signup") },
                onBack = { navController.navigate("landing") }
            )
        }
        composable("home") {
            HomePage(
                onNavigateToItem = { itemId ->
                    navController.navigate("item_page/$itemId")
                },
                onNavigateToUser = { navController.navigate("user_page") },
                onNavigateToUpload = { navController.navigate("upload_page") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }

        composable("item_page/{itemId}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            ItemPage(
                itemId = itemId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("user_page") {
            UserPage(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }
        composable("upload_page") {
            UploadPage(
                onBack = { navController.popBackStack() },
                onUploadSuccess = {
                    // navigate back to home after a successful upload
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            )
        }
        composable("settings") {
            SettingsPage(
                onBack = { navController.popBackStack() },
                onSignOut = {
                    navController.navigate("landing") {
                        popUpTo("landing") { inclusive = true }
                    }
                },
                onDeleteAccount = {
                    navController.navigate("landing") {
                        popUpTo("landing") { inclusive = true }
                    }
                },
                onDarkThemeToggle = onDarkThemeToggle,
                darkTheme = darkTheme
            )
        }
    }
}
