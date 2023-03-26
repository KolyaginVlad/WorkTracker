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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.ui.main.CardState
import ru.kolyagin.worktracker.ui.main.views.EventList
import ru.kolyagin.worktracker.ui.main.views.HeaderDay
import ru.kolyagin.worktracker.ui.main.views.WorkTimer
import ru.kolyagin.worktracker.ui.models.DayStartEvent
import ru.kolyagin.worktracker.ui.theme.PrimaryVariantDisabled
import ru.kolyagin.worktracker.ui.theme.RoundedButtonShapes
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import ru.kolyagin.worktracker.utils.models.DayOfWeek

@Composable
fun WorkStartScreenContent(
    state: CardState.WorkStart,
    onClickStartWork: () -> Unit,
    onClickDeleteEvent: () -> Unit = {},
    onAddPeriod: () -> Unit = {},
    onClickEvent: () -> Unit = {},
    onClickDeleteDay: () -> Unit = {}
) {
    Column {
        HeaderDay(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 17.dp, top = 26.dp, end = 28.dp, bottom = 15.dp),
            day = state.day,
            onClickDeleteDay = onClickDeleteDay
        )
        WorkTimer(
            time = state.time,
            title = stringResource(id = R.string.time_before_work)
        )
        StartButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 0.dp),
            onClickStartWork = onClickStartWork,
            buttonActive = state.buttonActive,
            startEarly = state.buttonStartEarly
        )
        EventList(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            events = state.events,
            onClickDeleteMeal = onClickDeleteEvent,
            onAddPeriod = onAddPeriod,
            onClickEvent = onClickEvent
        )
    }
}

@Composable
fun StartButton(
    modifier: Modifier,
    onClickStartWork: () -> Unit,
    buttonActive: Boolean,
    startEarly: Boolean
) {
    Row(
        modifier = modifier
    ) {
        Button(
            enabled = buttonActive,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedButtonShapes.medium,
            onClick = onClickStartWork,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primaryVariant,
                disabledBackgroundColor = PrimaryVariantDisabled,
                disabledContentColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.background
            )
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
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
            DayOfWeek.Monday,
            events = persistentListOf(
                DayStartEvent(
                    id = 0, timeStart = Time(19, 0), timeEnd = Time(19, 10), name = "УЖИН"
                ), DayStartEvent(
                    id = 0, timeStart = Time(19, 0), timeEnd = Time(19, 10), name = "ОБЕД"
                )
            ),
            buttonActive = true,
            time = TimeWithSeconds(hours = 10, minutes = 0, seconds = 36),
            buttonStartEarly = false
        ),
            onClickStartWork = {},
            onClickDeleteEvent = {},
            onAddPeriod = {},
            onClickEvent = {},
            onClickDeleteDay = {})
    }
}