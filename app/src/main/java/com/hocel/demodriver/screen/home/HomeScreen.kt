package com.hocel.demodriver.screen.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.hocel.demodriver.R
import com.hocel.demodriver.model.Driver
import com.hocel.demodriver.model.DriverStatus
import com.hocel.demodriver.model.Mission
import com.hocel.demodriver.model.Task
import com.hocel.demodriver.model.TaskStatus
import com.hocel.demodriver.ui.missionUI.TaskItem
import com.hocel.demodriver.ui.theme.yassirPurple
import com.hocel.demodriver.util.copyToClipboard
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSwitchClicked: (DriverStatus) -> Unit,
    //taskFlowAction: (task: Task, driver: Driver, action: MissionStatus) -> Unit
) {
    val user by viewModel.userData.collectAsState()
    var showProfileSheet by remember { mutableStateOf(false) }

    val mission by viewModel.currentMission.collectAsState()
    val currentTask by viewModel.currentTask
    val sheetContentState by remember {
        mutableStateOf(viewModel.sheetContentState)
    }

    val bottomSheetState = rememberModalBottomSheetState(
        confirmValueChange = { false }
    )
    val currentLocation = viewModel.currentLocation.collectAsState()

    Scaffold(
        content = {
            if (sheetContentState.value != SheetContentState.NONE) {
                ModalBottomSheet(
                    sheetState = bottomSheetState,
                    onDismissRequest = { },
                    dragHandle = null
                ) {
                    when (sheetContentState.value) {
                        SheetContentState.MISSION -> {
                            mission?.let {
                                MissionFlowSheet(
                                    mission = it,
                                    sheetTitle = "Mission Details",
                                    taskClicked = { task ->
                                        viewModel.selectTask(task)
                                    }
                                )
                            }
                        }

                        SheetContentState.TASK -> {
                            currentTask?.let { task ->
                                TaskFlowSheet(
                                    task = task,
                                    sheetTitle = "Task Details",
                                    actionButtonText = when (task.status) {
                                        TaskStatus.Pending -> "Start Task"
                                        TaskStatus.StartTask -> "Finish Task"
                                        TaskStatus.Finished -> "Finished"
                                    },
                                    buttonEnabled = task.status != TaskStatus.Finished,
                                    taskAction = {
                                        val action = when (task.status) {
                                            TaskStatus.Pending -> TaskStatus.StartTask
                                            TaskStatus.StartTask -> TaskStatus.Finished
                                            TaskStatus.Finished -> null
                                        }
                                        viewModel.taskAction(task, user, action)
                                    },
                                    onBackClicked = {
                                        viewModel.setSheetContentState(SheetContentState.MISSION)
                                    }
                                )
                            }
                        }

                        else -> Unit
                    }
                }
            }
            if (showProfileSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showProfileSheet = false },
                    dragHandle = null
                ) {
                    ProfileSheet(
                        driver = user,
                        updateProfileInfo = { name, email ->
                            viewModel.updateProfileInfo(user, name, email)
                            showProfileSheet = false
                        }
                    )
                }
            }
            HomeContent(
                driverName = user.name,
                driverStatus = user.status,
                location = currentLocation.value,
                handlePosition = viewModel::getCurrentPosition,
                onProfileClicked = {
                    showProfileSheet = true
                },
                onSwitchClicked = onSwitchClicked
            )
        }
    )
}

@Composable
private fun HomeContent(
    driverName: String,
    driverStatus: DriverStatus,
    location: LatLng,
    handlePosition: () -> Unit,
    onProfileClicked: () -> Unit,
    onSwitchClicked: (DriverStatus) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isMapLoaded = remember { mutableStateOf(false) }
    val cameraPositionState: CameraPositionState = rememberCameraPositionState()
    val zoom = 11f

    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                mapToolbarEnabled = false,
                myLocationButtonEnabled = true,
                zoomControlsEnabled = false,
                scrollGesturesEnabled = true,
            )
        )
    }

    if (location.latitude > 0.0 && isMapLoaded.value) {
        LaunchedEffect(location.latitude) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    location,
                    zoom
                )
            )
        }
    }

    var dataline: String
    context.resources.openRawResource(R.raw.harbin_map_style).bufferedReader().use {
        dataline = it.readText()
    }

    val properties by remember {
        mutableStateOf(MapProperties(mapStyleOptions = MapStyleOptions(dataline)))
    }

    var checked by remember { mutableStateOf(driverStatus != DriverStatus.Offline) }
    LaunchedEffect(key1 = driverStatus) {
        checked = driverStatus != DriverStatus.Offline
    }
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
            Box(
                modifier = Modifier
                    .weight(8f)
            ) {
                GoogleMap(
                    cameraPositionState = cameraPositionState,
                    uiSettings = mapUiSettings,
                    properties = properties,
                    onMapLoaded = { isMapLoaded.value = true }
                ) {
                    Marker(
                        state = MarkerState(position = location),
                        title = "My position"
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomEnd)
                        .background(color = Color.Transparent, shape = CircleShape)
                        .shadow(elevation = 15.dp, shape = CircleShape)
                        .clickable {
                            handlePosition()
                            scope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(
                                        location,
                                        zoom
                                    )
                                )
                            }
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(color = Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            modifier = Modifier.align(Alignment.Center),
                            contentDescription = null
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomStart)
                        .background(color = Color.Transparent, shape = CircleShape)
                        .shadow(elevation = 15.dp, shape = CircleShape)
                        .clickable {
                            onProfileClicked()
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(color = Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            modifier = Modifier.align(Alignment.Center),
                            contentDescription = null
                        )
                    }
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
                            text = if (driverStatus == DriverStatus.Online) "Online and ready for missions" else "Offline, no mission will be assigned to you",
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
    canCloseSheet: Boolean,
    onCloseClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        if (canCloseSheet) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                IconButton(onClick = {
                    onCloseClicked()
                }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close button")
                }
            }
        } else {
            Spacer(modifier = Modifier.padding(10.dp))
        }
        content()
    }
}

@Composable
private fun ProfileSheet(
    driver: Driver,
    updateProfileInfo: (name: String, email: String) -> Unit
) {
    val context = LocalContext.current
    var driverName by remember { mutableStateOf("") }
    var driverEmail by remember { mutableStateOf("") }
    var driverId by remember { mutableStateOf("") }
    var nameChanged by remember { mutableStateOf(false) }
    var emailChanged by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = driver) {
        driverName = driver.name
        driverEmail = driver.email
        driverId = driver._id.toHexString()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(horizontal = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Driver info",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = driverName,
                onValueChange = {
                    driverName = it
                    nameChanged = it != driver.name
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = yassirPurple,
                    unfocusedBorderColor = yassirPurple
                ),
                label = {
                    Text(
                        text = "Name",
                        color = yassirPurple
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = driverEmail,
                onValueChange = {
                    driverEmail = it
                    emailChanged = it != driver.email
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = yassirPurple,
                    unfocusedBorderColor = yassirPurple,

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
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = driverId,
                onValueChange = { },
                label = {
                    Text(
                        text = "Driver ID",
                        color = yassirPurple
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = yassirPurple,
                    unfocusedBorderColor = yassirPurple,

                    ),
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    IconButton(onClick = {
                        copyToClipboard(context, driverId)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    updateProfileInfo(driverName, driverEmail)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors().copy(containerColor = yassirPurple),
                enabled = nameChanged || emailChanged,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Update info",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun MissionFlowSheet(
    mission: Mission,
    sheetTitle: String = "sheetTitle",
    taskClicked: (task: Task) -> Unit
) {
    Column(
        Modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(8f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = sheetTitle,
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = mission.m_title,
                fontSize = 19.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = mission.m_desc,
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                style = typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn {
                items(mission.tasks) {
                    TaskItem(modifier = Modifier.padding(vertical = 10.dp), task = it) { task ->
                        taskClicked(task)
                    }
                }
            }
        }
    }
}

@Composable
fun TaskFlowSheet(
    task: Task,
    sheetTitle: String,
    actionButtonText: String,
    buttonEnabled: Boolean,
    taskAction: () -> Unit,
    onBackClicked: () -> Unit
) {
    Column(
        Modifier
            .fillMaxHeight(.5f)
            .padding(16.dp)
    ) {
        IconButton(onClick = onBackClicked) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)

        }
        Column(
            modifier = Modifier
                .weight(8f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = sheetTitle,
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = task.t_name,
                fontSize = 19.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = task.t_desc,
                fontSize = 19.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = task.client,
                fontSize = 19.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
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
        }
        Box(modifier = Modifier.weight(2f)) {
            Button(
                onClick = {
                    taskAction()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors().copy(containerColor = yassirPurple),
                shape = RoundedCornerShape(16.dp),
                enabled = buttonEnabled
            ) {
                Text(
                    text = actionButtonText,
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




