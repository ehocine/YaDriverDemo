package com.hocel.demodriver.screen.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.hocel.demodriver.model.DriverStatus

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    onSwitchClicked: (Boolean) -> Unit,
    acceptTrip: (tripId: String) -> Unit,
    declineTrip: (tripId: String) -> Unit
) {

    var showBottomSheet by remember { mutableStateOf(true) }
    Scaffold(
        content = {
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false }) {
                    TripRequestSheet(
                        tripId = "",
                        acceptTrip = acceptTrip,
                        declineTrip = declineTrip
                    )
                }
            }
            HomeContent(
                driverStatus = DriverStatus.Offline,
                onSwitchClicked = onSwitchClicked
            )
        }
    )
}

@Composable
fun HomeContent(
    driverStatus: DriverStatus,
    onSwitchClicked: (Boolean) -> Unit
) {
    var checked by remember { mutableStateOf(driverStatus != DriverStatus.Offline) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier
            .fillMaxSize()) {
            val location = LatLng(36.75, 2.95)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(location, 20f)
            }
            GoogleMap(
                modifier = Modifier.weight(9f),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = MarkerState(position = location),
                    title = "Algiers",
                    snippet = "Marker in Algiers"
                )
            }
            Row(
                Modifier.fillMaxWidth().padding(vertical = 20.dp, horizontal = 10.dp).weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Driver status", color = Color.Black)
                Switch(
                    checked = checked,
                    onCheckedChange = {

                        checked = it
                    }
                )
            }
        }
    }
}

@Composable
private fun TripRequestSheet(
    tripId: String,
    acceptTrip: (tripId: String) -> Unit,
    declineTrip: (tripId: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            IconButton(onClick = {
                declineTrip(tripId)
            }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close button")
            }
        }
        Text(
            text = "Client name",
            fontSize = typography.titleMedium.fontSize,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Trip details",
            fontSize = typography.titleSmall.fontSize,
            fontWeight = FontWeight.Normal
        )
    }
}

