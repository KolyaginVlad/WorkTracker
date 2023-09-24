package ru.kolyagin.worktracker.ui.main.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.domain.models.WorkEvent
import ru.kolyagin.worktracker.ui.main.CardState
import ru.kolyagin.worktracker.ui.main.views.EventList
import ru.kolyagin.worktracker.ui.main.views.HeaderDay
import ru.kolyagin.worktracker.ui.main.views.WorkTimer
import ru.kolyagin.worktracker.ui.theme.PrimaryVariantDisabled
import ru.kolyagin.worktracker.ui.theme.Red
import ru.kolyagin.worktracker.ui.theme.RedDisable
import ru.kolyagin.worktracker.ui.theme.RoundedButtonShapes
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import java.time.DayOfWeek

@Composable
fun WorkStartScreenContent(
    state: CardState.WorkStart,
    onClickStartWork: () -> Unit,
    onClickDeleteEvent: (WorkEvent, Int) -> Unit = { _, _ -> },
    onAddPeriod: (Int) -> Unit = {},
    onClickEvent: (Int, WorkEvent) -> Unit = { _, _ -> },
    onClickDeleteDay: () -> Unit = {}
) {
    Column {
        val title: String
        val textColor: Color
        val disablseColor: Color
        if (!state.late) {
            title = stringResource(id = R.string.time_before_work)
            textColor = MaterialTheme.colors.primaryVariant
            disablseColor = PrimaryVariantDisabled
        } else {
            title = stringResource(
                id = R.string.late_time
            )
            textColor = Red
            disablseColor = RedDisable
        }
        HeaderDay(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 17.dp, top = 26.dp, end = 28.dp, bottom = 15.dp),
            day = state.day,
            onClickDeleteDay = onClickDeleteDay,
            contentColor = textColor
        )
        WorkTimer(
            time = state.time,
            title = title,
            primaryColor = textColor,
            disableColor = disablseColor,
        )
        StartButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 0.dp),
            onClickStartWork = onClickStartWork,
            buttonActive = state.buttonActive,
            startEarly = state.buttonStartEarly,
            contentColor = textColor
        )
        EventList(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            events = state.events,
            onClickDeleteEvent = onClickDeleteEvent,
            onAddPeriod = onAddPeriod,
            onClickEvent = onClickEvent,
            day = state.day.ordinal,
            contentColor = textColor
        )
    }
}

@Composable
fun StartButton(
    modifier: Modifier,
    onClickStartWork: () -> Unit,
    buttonActive: Boolean,
    startEarly: Boolean,
    contentColor: Color = MaterialTheme.colors.primaryVariant
) {
    Row(
        modifier = modifier
    ) {
        Button(
            enabled = buttonActive,
            modifier = Modifier.fillMaxWidth(),
            elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
            shape = RoundedButtonShapes.medium,
            onClick = onClickStartWork,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = contentColor,
                disabledBackgroundColor = PrimaryVariantDisabled,
                disabledContentColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.background
            )
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
                style = MaterialTheme.typography.button,
                text = if (startEarly) stringResource(id = R.string.work_start_early)
                else stringResource(
                    id = R.string.work_start
                )
            )
        }
    }
}

@Preview(showBackground = true, locale = "ru", backgroundColor = 0xFFFFFFFF)
@Composable
private fun WorkStartScreenPreview() {
    WorkTrackerTheme {
        WorkStartScreenContent(state = CardState.WorkStart(
            DayOfWeek.MONDAY,
            events = persistentListOf(
                WorkEvent(
                    id = 0, timeStart = Time(19, 0), timeEnd = Time(19, 10), name = "УЖИН"
                ), WorkEvent(
                    id = 0, timeStart = Time(19, 0), timeEnd = Time(19, 10), name = "ОБЕД"
                )
            ),
            buttonActive = true,
            time = TimeWithSeconds(hours = 10, minutes = 0, seconds = 36),
            buttonStartEarly = false,
            late = true
        ),
            onClickStartWork = {},
            onClickDeleteEvent = { _, _ -> },
            onAddPeriod = {},
            onClickDeleteDay = {})
    }
}