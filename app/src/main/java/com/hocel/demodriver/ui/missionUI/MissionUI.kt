package com.hocel.demodriver.ui.missionUI

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hocel.demodriver.model.Mission
import com.hocel.demodriver.model.MissionStatus
import com.hocel.demodriver.model.Task
import com.hocel.demodriver.model.TaskStatus
import com.hocel.demodriver.ui.theme.yassirPurple

@Composable
fun TaskItem(modifier: Modifier = Modifier, task: Task? = null, taskClicked: (task: Task) -> Unit) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        task?.let {
            Icon(
                Icons.Outlined.Task,
                contentDescription = "Localized description",
                tint = yassirPurple,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column(verticalArrangement = Arrangement.Center, modifier = Modifier
                .weight(6f)
                .clickable {
                    taskClicked(task)
                }) {
                Text(
                    text = task.t_name,
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.W700,
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Description,
                        contentDescription = "Localized description",
                        tint = yassirPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = task.t_desc,
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.W700,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                    )
                }
            }
            Icon(
                imageVector =
                when (task.status) {
                    TaskStatus.Pending -> Icons.Filled.AccessTime
                    TaskStatus.StartTask -> Icons.Filled.Start
                    TaskStatus.Finished -> Icons.Filled.Done
                },
                contentDescription = null
            )
        }
    }
}


@Composable
fun MissionFlowSheet(
    mission: Mission,
    sheetTitle: String = "sheetTitle",
    taskClicked: (task: Task) -> Unit,
    closeMissionSheet: () -> Unit
) {
    Column(
        Modifier
            .fillMaxHeight(.5f)
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
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(10.dp))
            when (mission.status) {
                MissionStatus.Pending -> {
                    LazyColumn {
                        items(mission.tasks) {
                            TaskItem(
                                modifier = Modifier.padding(vertical = 10.dp),
                                task = it
                            ) { task ->
                                taskClicked(task)
                            }
                        }
                    }
                }

                MissionStatus.Finished -> {
                    Text(
                        text = "Congrats! Mission is completed",
                        fontSize = 18.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                else -> Unit
            }
        }
        if (mission.status == MissionStatus.Finished)
            Box(modifier = Modifier.weight(2f)) {
                Button(
                    onClick = { closeMissionSheet() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors()
                        .copy(containerColor = yassirPurple),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Close",
                        fontSize = 16.sp,
                        color = Color.White
                    )
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
                    style = MaterialTheme.typography.labelLarge,
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
                style = MaterialTheme.typography.labelLarge
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
                    style = MaterialTheme.typography.labelLarge
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