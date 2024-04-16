package com.hocel.demodriver.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hocel.demodriver.ui.theme.yassirPurple

@Composable
fun PermissionsPopupWindow(
    showDialog: Boolean,
    hasLocationPermission: Boolean,
    hasNotificationPermission: Boolean,
    grantLocationPermission: () -> Unit,
    grantNotificationPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = {
                onDismiss()
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.background
            ) {

                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(32.dp)
                ) {
                    Text(
                        text = "Required permission",
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            grantLocationPermission()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors()
                            .copy(containerColor = yassirPurple),
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
                        colors = ButtonDefaults.buttonColors()
                            .copy(containerColor = yassirPurple),
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
            }


        }
    }
}