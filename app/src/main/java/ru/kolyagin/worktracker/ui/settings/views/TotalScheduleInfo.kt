package ru.kolyagin.worktracker.ui.settings.views

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.ui.theme.OnPrimaryHighEmphasis
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TotalScheduleInfo(
    totalTime: Time,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colors.primary,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(40.dp),
        backgroundColor = OnPrimaryHighEmphasis,
        elevation = 0.dp
    ) {
        Text(
            modifier = Modifier.padding(24.dp),
            text = "${stringResource(id = R.string.total_time)}\n${
                pluralStringResource(
                    id = R.plurals.hoursL,
                    count = totalTime.hours,
                    totalTime.hours
                )
            } ${
                pluralStringResource(
                    id = R.plurals.minutesL,
                    count = totalTime.minutes,
                    totalTime.minutes
                )
            }",
            style = MaterialTheme.typography.h5,
            color = contentColor,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFBE9E7)
@Composable
private fun TotalScheduleInfo() {
    WorkTrackerTheme {
        TotalScheduleInfo(
            Time(30, 1)
        )
    }
}