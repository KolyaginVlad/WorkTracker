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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.ui.theme.SurfaceDisabled
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme

@Composable
fun LabelAndTime(
    label: String,
    time: Time,
    onTimeClick: () -> Unit,
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
        Text(
            modifier = Modifier.clickable(onClick = onTimeClick).weight(0.3f),
            text = time.toString(),
            style = MaterialTheme.typography.body2,
            color = SurfaceDisabled,
            textAlign = TextAlign.End
        )
    }
}

@Preview
@Composable
private fun LabelAndTimePrev() {
    WorkTrackerTheme {
        LabelAndTime(
            label = "123",
            time = Time(1, 0),
            onTimeClick = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}