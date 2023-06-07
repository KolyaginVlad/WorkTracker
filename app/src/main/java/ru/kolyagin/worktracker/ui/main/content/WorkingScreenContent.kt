package ru.kolyagin.worktracker.ui.main.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.domain.models.WorkEvent
import ru.kolyagin.worktracker.ui.main.CardState
import ru.kolyagin.worktracker.ui.main.views.Button
import ru.kolyagin.worktracker.ui.main.views.EventList
import ru.kolyagin.worktracker.ui.main.views.HeaderDay
import ru.kolyagin.worktracker.ui.main.views.WorkTimer
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import ru.kolyagin.worktracker.ui.views.Spacer
import java.time.DayOfWeek

@Composable
fun WorkingScreenContent(
    state: CardState.Working,
    onClickStartPause: () -> Unit,
    onClickGoToDinner: () -> Unit,
    onClickDeleteDay: () -> Unit = {},
    onClickDeleteEvent: (WorkEvent,Int) -> Unit = {_,_->},
    onAddPeriod: (Int) -> Unit = {},
    onClickEvent: (Int, WorkEvent) -> Unit = {_,_->},
    onClickEndWork: () -> Unit = {}
) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        HeaderDay(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 17.dp, top = 26.dp, end = 28.dp, bottom = 15.dp),
            day = state.day,
            onClickDeleteDay = onClickDeleteDay
        )
        WorkTimer(
            time = state.time,
            title = if (!state.overwork) stringResource(id = R.string.time_work) else stringResource(
                id = R.string.overwork_time
            )
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            onClick = onClickStartPause,
            text = stringResource(id = R.string.pause_work),
            backgroundColor = MaterialTheme.colors.secondary,
            contentColor = MaterialTheme.colors.onSecondary,
            borderColor = MaterialTheme.colors.secondary
        )
        if (state.isDinnerEnable) {
            Spacer(12.dp)
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                onClick = onClickGoToDinner,
                text = stringResource(id = R.string.start_lunch),
                backgroundColor = MaterialTheme.colors.secondary,
                contentColor = MaterialTheme.colors.onSecondary,
                borderColor = MaterialTheme.colors.secondary
            )
        }
        EventList(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            events = state.events,
            onClickDeleteEvent = onClickDeleteEvent,
            onAddPeriod = onAddPeriod,
            day = state.day.ordinal,
            onClickEvent = onClickEvent
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            onClick = onClickEndWork,
            text = stringResource(id = R.string.finish_work),
            backgroundColor = MaterialTheme.colors.primaryVariant,
            contentColor = MaterialTheme.colors.onSecondary,
            borderColor =  MaterialTheme.colors.primaryVariant
        )
    }
}

@Preview(showBackground = true, locale = "ru", backgroundColor = 0xFFFFFFFF)
@Composable
private fun WorkingScreenPrev() {
    WorkTrackerTheme {
        WorkingScreenContent(
            state = CardState.Working(
                day = DayOfWeek.SATURDAY,
                events = persistentListOf(
                    WorkEvent(
                        id = 0,
                        timeStart = Time(19, 0),
                        timeEnd = Time(19, 10),
                        name = "УЖИН"
                    ), WorkEvent(
                        id = 0,
                        timeStart = Time(19, 0),
                        timeEnd = Time(19, 10),
                        name = "ОБЕД"
                    )
                ),
                time = TimeWithSeconds(1, 0, 30)
            ),
            onClickStartPause = { },
            onClickGoToDinner = { }
        )
    }
}