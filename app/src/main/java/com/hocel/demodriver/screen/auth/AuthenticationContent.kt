package com.hocel.demodriver.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hocel.demodriver.R
import com.hocel.demodriver.util.copyToClipboard
import com.stevdza.san.demodriver.ui.theme.yassirPurple

@Composable
fun AuthenticationContent(
    loadingState: Boolean,
    hasLocationPermission: Boolean,
    hasNotificationPermission: Boolean,
    grantLocationPermission: () -> Unit,
    grantNotificationPermission: () -> Unit,
    onButtonClicked: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(9f)
                .fillMaxWidth()
                .padding(all = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.weight(weight = 10f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(120.dp),
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = "Google Logo"
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Please sign in to continue.",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        grantLocationPermission()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors().copy(containerColor = yassirPurple),
                    enabled = !hasLocationPermission,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (!hasLocationPermission) "Grant location permission" else "Location permission granted",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        grantNotificationPermission()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors().copy(containerColor = yassirPurple),
                    enabled = !hasNotificationPermission,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (!hasNotificationPermission) "Grant notification permission" else "Notification permission granted",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
            Column(
                modifier = Modifier.weight(weight = 2f),
                verticalArrangement = Arrangement.Bottom
            ) {
                GoogleButton(
                    loadingState = loadingState,
                    onClick = onButtonClicked
                )
            }
        }
    }
}