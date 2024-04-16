package com.hocel.demodriver.screen.login

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hocel.demodriver.common.PermissionsPopupWindow
import com.hocel.demodriver.ui.theme.yassirPurple

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun LoginContent(
    loadingState: Boolean,
    hasLocationPermission: Boolean,
    hasNotificationPermission: Boolean,
    grantLocationPermission: () -> Unit,
    grantNotificationPermission: () -> Unit,
    onButtonClicked: (email: String, password: String) -> Unit,
    goToSignup: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    var emailValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    PermissionsPopupWindow(
        showDialog = showDialog,
        hasLocationPermission = hasLocationPermission,
        hasNotificationPermission = hasNotificationPermission,
        grantLocationPermission = grantLocationPermission,
        grantNotificationPermission = grantNotificationPermission,
        onDismiss = {
            showDialog = false
        }
    )

    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) { focusManager.clearFocus() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .padding(10.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.verticalScroll(state = scrollState)
                ) {
                    Text(
                        text = "Login",
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.displayMedium.fontSize
                    )
                    Spacer(modifier = Modifier.padding(10.dp))

                    Spacer(modifier = Modifier.padding(10.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        OutlinedTextField(
                            value = emailValue,
                            onValueChange = {
                                emailValue = it
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = yassirPurple,
                                unfocusedBorderColor = yassirPurple
                            ),
                            label = {
                                Text(
                                    text = "Email",
                                    color = yassirPurple
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.padding(5.dp))
                        OutlinedTextField(
                            value = passwordValue,
                            onValueChange = {
                                passwordValue = it
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = yassirPurple,
                                unfocusedBorderColor = yassirPurple,

                                ),
                            trailingIcon = {
                                IconButton(onClick = {
                                    passwordVisibility = !passwordVisibility
                                }) {
                                    Icon(
                                        if (passwordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Password eye",
                                        tint = yassirPurple
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisibility) VisualTransformation.None
                            else PasswordVisualTransformation(),
                            label = {
                                Text(
                                    text = "Password",
                                    color = yassirPurple
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.padding(10.dp))
                        Button(
                            onClick = {
                                if (!hasLocationPermission && !hasNotificationPermission) {
                                    showDialog = true
                                } else {
                                    onButtonClicked(emailValue, passwordValue)
                                }
                            },
                            enabled = !loadingState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors()
                                .copy(containerColor = yassirPurple, contentColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (loadingState) {
                                CircularProgressIndicator(color = Color.White)
                            } else {
                                Text(
                                    text = "Login",
                                    fontSize = 20.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.padding(15.dp))
                        Row {
                            Text(
                                text = "Don't have an account?",
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize
                            )
                            Spacer(modifier = Modifier.padding(end = 2.dp))
                            Text(
                                text = "Register",
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    goToSignup()
                                })
                        }
                    }
                }
            }
        }
    }
}