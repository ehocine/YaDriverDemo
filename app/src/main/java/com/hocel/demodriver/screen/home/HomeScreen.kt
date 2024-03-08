package com.hocel.demodriver.screen.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.hocel.demodriver.model.DriverStatus
import com.hocel.demodriver.model.Trip
import com.hocel.demodriver.model.TripStatus
import com.stevdza.san.demodriver.ui.theme.yassirPurple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSwitchClicked: (DriverStatus) -> Unit,
    acceptTrip: (tripId: ObjectId) -> Unit,
    declineTrip: (tripId: String) -> Unit
) {

    val user by viewModel.userData.collectAsState()
    val tripData by viewModel.tripData.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = tripData, key2 = user.status) {
        if (user.status == DriverStatus.Online) showBottomSheet = tripData.isNotEmpty()
    }

    val trip by viewModel.currentTrip

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        confirmValueChange = { false }
    )
    Scaffold(
        content = {
            if (showBottomSheet) {
                ModalBottomSheet(
                    sheetState = sheetState,
                    onDismissRequest = { },
                    dragHandle = null
                ) {
                    SheetContent(
                        content = {
                            trip.let { tr ->
                                when (tr.status) {
                                    TripStatus.Pending -> {
                                        TripInfoSheet(
                                            trip = tr,
                                            acceptTrip = {
                                                acceptTrip(it)
                                            }
                                        )
                                    }

                                    TripStatus.Accepted -> {
                                        TripFlowSheet(
                                            trip = trip,
                                            acceptTrip = acceptTrip
                                        )
                                    }

                                    else -> {

                                    }
                                }
                            }
                        },
                        onCloseClicked = {
                            scope.launch {
                                sheetState.hide()
                                delay(100)
                                showBottomSheet = false
                            }
                        }
                    )

                }
            }
            HomeContent(
                driverName = user.name,
                driverStatus = user.status,
                onSwitchClicked = onSwitchClicked
            )
        }
    )
}

@Composable
private fun HomeContent(
    driverName: String,
    driverStatus: DriverStatus,
    onSwitchClicked: (DriverStatus) -> Unit
) {
    var checked by remember { mutableStateOf(driverStatus != DriverStatus.Offline) }
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val location = LatLng(36.75, 2.95)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(location, 15f)
            }
            Box(
                modifier = Modifier
                    .weight(8f)
            ) {
                GoogleMap(
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = MarkerState(position = location),
                        title = "Algiers",
                        snippet = "Marker in Algiers"
                    )
                }
                if (driverStatus == DriverStatus.Offline) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.Black.copy(alpha = 0.3f))
                    )
                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 10.dp)
                    .weight(2f)
            ) {
                Text(
                    text = "Good to see you $driverName",
                    fontSize = typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold
                )
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (driverStatus == DriverStatus.Online) "You are online and ready for trips" else "You are offline, no trip will be received",
                            color = Color.Black
                        )
                        Switch(
                            checked = checked,
                            onCheckedChange = {
                                checked = it
                                if (it) {
                                    onSwitchClicked(DriverStatus.Online)
                                } else {
                                    onSwitchClicked(DriverStatus.Offline)
                                }
                            },
                            colors = SwitchDefaults.colors().copy(checkedTrackColor = yassirPurple)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SheetContent(
    content: @Composable (() -> Unit),
    onCloseClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            IconButton(onClick = {
                onCloseClicked()
            }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close button")
            }
        }
        content()
    }
}

@Composable
fun TripInfoSheet(
    trip: Trip,
    acceptTrip: (tripId: ObjectId) -> Unit
) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = trip.client,
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Trajectory",
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            style = typography.bodyMedium
        )

        BulletedList(
            listOf(
                trip.pickUpAddress,
                trip.dropOffAddress
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Price",
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                style = typography.labelLarge,
                modifier = Modifier
                    .weight(9f)
            )
            Icon(
                Icons.Outlined.Info,
                contentDescription = "Localized description",
                tint = yassirPurple,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Price + Currency",
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.W400,
            style = typography.labelLarge
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.AttachMoney,
                contentDescription = "Localized description",
                tint = yassirPurple, modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Payment in cash",
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                style = typography.labelLarge
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box {
            Button(
                onClick = {
                    acceptTrip(trip._id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors().copy(containerColor = yassirPurple),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Accept trip",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun TripFlowSheet(
    trip: Trip,
    acceptTrip: (tripId: ObjectId) -> Unit
) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = trip.client,
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Acce Trajectory",
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            style = typography.bodyMedium
        )

        BulletedList(
            listOf(
                trip.pickUpAddress,
                trip.dropOffAddress
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Price",
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                style = typography.labelLarge,
                modifier = Modifier
                    .weight(9f)
            )
            Icon(
                Icons.Outlined.Info,
                contentDescription = "Localized description",
                tint = yassirPurple,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Price + Currency",
            fontSize = 18.sp,
            color = Color.Black,
            fontWeight = FontWeight.W400,
            style = typography.labelLarge
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.AttachMoney,
                contentDescription = "Localized description",
                tint = yassirPurple, modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Payment in cash",
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                style = typography.labelLarge
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box {
            Button(
                onClick = {
                    acceptTrip(trip._id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors().copy(containerColor = yassirPurple),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text =
                    "Accept trip",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun BulletedList(items: List<String>) {
    LazyColumn {
        items(items.size) { index ->
            val text = items[index]
            BulletPointRow(text = text, isLastItem = index == items.size - 1)
        }
    }
}

@Composable
fun BulletPointRow(text: String, isLastItem: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        BulletPoint()
        Spacer(modifier = Modifier.width(10.dp))
        TextWithPadding(text = text)
    }
    if (!isLastItem) {
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun BulletPoint(colorResId: Color = yassirPurple) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {

        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(

                color = colorResId,
                radius = size.width / 2,
                center = Offset(x = size.width / 2, y = size.height / 2),
                style = Stroke(
                    width = 3.dp.toPx()
                )
            )
        }
    }
}

@Composable
fun TextWithPadding(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        color = Color.Black,
        style = typography.bodyMedium,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}



