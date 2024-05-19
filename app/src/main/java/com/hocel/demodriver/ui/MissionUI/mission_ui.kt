package com.hocel.demodriver.ui.MissionUI

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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Textsms
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hocel.demodriver.R
import com.hocel.demodriver.model.Task

@Composable
fun TaskItem(task : Task? = null,   taskClicked: (task: Task) -> Unit ) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Outlined.Task,
            contentDescription = "Localized description",
            tint = colorResource(id = R.color.taskcolor),
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.weight(6f).clickable {
            if (task != null) {
                taskClicked(task)
            }
        }) {
            Text(
                text = "Pick-up from Hydra",
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.W700,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ETA : 20 min",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.W700,

                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp , horizontal = 4.dp)
                )
                Icon(
                    Icons.Filled.Timer,
                    contentDescription = "Localized description",
                    tint =  colorResource(id = R.color.taskcolor),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Icon(
            Icons.Filled.Call,
            contentDescription = "Localized description",
            tint = colorResource(id = R.color.taskcolor),
            modifier = Modifier.size(30.dp)
        )

        Spacer(modifier = Modifier.width(20.dp))

        Icon(
            Icons.Filled.Textsms,
            contentDescription = "Localized description",
            tint = colorResource(id = R.color.taskcolor),
            modifier = Modifier.size(30.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TaskItemExpanded(task : Task? = null ) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Outlined.Task,
            contentDescription = "Localized description",
            tint = colorResource(id = R.color.taskcolor),
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier.weight(6f).clickable {

        }) {
            Text(
                text = "Pick-up from Hydra",
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.W700,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ETA : 20 min",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.W700,

                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp , horizontal = 4.dp)
                )
                Icon(
                    Icons.Filled.Timer,
                    contentDescription = "Localized description",
                    tint =  colorResource(id = R.color.taskcolor),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Icon(
            Icons.Filled.Call,
            contentDescription = "Localized description",
            tint = colorResource(id = R.color.taskcolor),
            modifier = Modifier.size(30.dp)
        )

        Spacer(modifier = Modifier.width(20.dp))

        Icon(
            Icons.Filled.Textsms,
            contentDescription = "Localized description",
            tint = colorResource(id = R.color.taskcolor),
            modifier = Modifier.size(30.dp)
        )
    }
}