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
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "landing",
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
            HomePage(navController = navController)
        }
        composable("item_page") {
            ItemPage()
        }
        composable("user_page") {
            UserPage(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }
        composable("upload_page") {
            UploadPage()
        }
        composable("settings") {
            SettingsPage()
        }
    }
}