package com.cs407.pinpoint.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.pinpoint.R
import com.cs407.pinpoint.ui.theme.PinPointPrimary
import com.cs407.pinpoint.ui.theme.PinPointSecondary
import com.cs407.pinpoint.ui.theme.PinPointGreenAccent
import com.cs407.pinpoint.ui.theme.PinPointBackground
import com.cs407.pinpoint.ui.theme.PinPointSurface
import com.cs407.pinpoint.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ItemPage(
    itemName: String = "ITEM NAME HERE",
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(itemName, color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    // keep a visual balance so the title appears centered
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PinPointGreenAccent
                )
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(PinPointBackground),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ImageCard()
            }
        }
    )
}

@Composable
fun ImageCard(
    /* Dynamic name and img URL to make it reusable*/
    modifier: Modifier = Modifier,
    imageRes: Int = R.drawable.ic_launcher_foreground,
    contentDescription: String = "Dynamic Item Name"
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier
                .wrapContentHeight()
                .padding(24.dp),
            shape = MaterialTheme.shapes.extraLarge,
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = PinPointSurface
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    contentDescription,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(0.dp, 8.dp),
                    color = TextPrimary
                )
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = contentDescription,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp, 16.dp)
                        .size(500.dp, 300.dp)
                        .border(
                            width = 1.dp,
                            color = TextPrimary,
                            shape = MaterialTheme.shapes.medium // border radius
                        )
                )
                Spacer(Modifier.width(16.dp))
            }
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ){
                Row(){
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = "Phone Icon",
                        tint = TextPrimary
                    )
                    Text(
                        "Contact Information: (Phone or Email)",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp),
                        color = TextPrimary
                    )
                }
                Row(){
                    Icon(
                        Icons.Default.AccountBox,
                        contentDescription ="User Name",
                        tint = TextPrimary
                    )
                    Text(
                        "UserName : default_user",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp),
                        color = TextPrimary
                    )
                }
                /* TODO : Add a map API here...
                    where the img was taken and put a pin there */ // Idk how to do that rn
                Spacer(Modifier.padding(64.dp))//Holder for the map
                Text("MAP GOES HERE", color = TextPrimary)
            }
        }
    }
}