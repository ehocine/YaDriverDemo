package com.hocel.demodriver.ui.missionUI

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material3.Icon
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