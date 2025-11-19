package com.cs407.pinpoint.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.pinpoint.R

@Composable
fun LandingPage(
    onSignUp: () -> Unit,
    onLogin: () -> Unit,
) {
    val mint = Color(0xFF79E8B2)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.padding(top = 96.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "PinPoint",
                fontSize = 48.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(Modifier.height(28.dp))

            Image(
                painter = painterResource(R.drawable.landing_page_marker),
                contentDescription = "Marker image for landing page"
            )
        }

        Spacer(Modifier.weight(1f))

        Column(
            modifier = Modifier.padding(bottom = 48.dp)
        ) {
            Button(
                onClick = onSignUp,
                colors = ButtonDefaults.buttonColors(containerColor = mint),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Up", color = Color.Black, fontSize = 20.sp)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onLogin,
                colors = ButtonDefaults.buttonColors(containerColor = mint),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login", color = Color.Black, fontSize = 20.sp)
            }
        }
    }
}