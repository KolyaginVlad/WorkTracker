package ru.kolyagin.worktracker.ui.notificationSettings.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.ui.theme.SurfaceDisabled
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme

@Composable
fun LabelAndRange(
    label: String,
    startTime: Time,
    endTime: Time,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(0.7f),
            text = label,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.primary
        )
        Row(modifier = Modifier.weight(0.3f), horizontalArrangement = Arrangement.End) {
            Text(
                modifier = Modifier.clickable(onClick = onStartTimeClick),
                text = startTime.toString(),
                style = MaterialTheme.typography.body2,
                color = SurfaceDisabled
            )
            Text(
                text = "-",
                style = MaterialTheme.typography.body2,
                color = SurfaceDisabled
            )
            Text(
                modifier = Modifier.clickable(onClick = onEndTimeClick),
                text = endTime.toString(),
                style = MaterialTheme.typography.body2,
                color = SurfaceDisabled
            )
        }
    }
}

@Preview
@Composable
private fun LabelAndRangePrev() {
    WorkTrackerTheme {
        LabelAndRange(
            label = "123",
            startTime = Time(1, 0),
            endTime = Time(2, 0),
            onStartTimeClick = {},
            onEndTimeClick = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}