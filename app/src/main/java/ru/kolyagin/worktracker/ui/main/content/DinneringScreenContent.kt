package ru.kolyagin.worktracker.ui.main.content

import androidx.compose.foundation.background
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
import ru.kolyagin.worktracker.ui.theme.SurfaceDisabled
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import ru.kolyagin.worktracker.ui.views.WorkTimer
import java.time.DayOfWeek

@Composable
fun DinneringScreenContent(
    state: CardState.Dinnering,
    onClickReturnFromDinner: () -> Unit,
    onClickEndWork: () -> Unit = {},
    onClickDeleteEvent: (WorkEvent,Int) -> Unit = {_,_->},
    onAddPeriod: (Int) -> Unit = {},
    onClickEvent: (Int, WorkEvent) -> Unit = {_,_->},
    onClickShowMore: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.secondary)
            .padding(bottom = 12.dp)
    ) {
        HeaderDay(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 17.dp, top = 26.dp, end = 28.dp, bottom = 15.dp),
            day = state.day,
            contentColor = MaterialTheme.colors.onSecondary,
            backgroundColor = MaterialTheme.colors.secondary
        )
        WorkTimer(
            time = state.time,
            title = stringResource(id = R.string.time_dinnering),
            primaryColor = MaterialTheme.colors.onSecondary,
            disableColor = SurfaceDisabled,
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            onClick = onClickReturnFromDinner,
            text = stringResource(id = R.string.continue_work),
            backgroundColor = MaterialTheme.colors.onPrimary,
            contentColor = MaterialTheme.colors.primary,
            borderColor = MaterialTheme.colors.onPrimary
        )
        EventList(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            events = state.events,
            onClickDeleteEvent = onClickDeleteEvent,
            onAddPeriod = onAddPeriod,
            day = state.day.ordinal,
            onClickEvent = onClickEvent,
            contentColor = MaterialTheme.colors.onSecondary,
            backgroundColor = MaterialTheme.colors.secondary
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 0.dp),
            onClick = onClickEndWork,
            text = stringResource(id = R.string.finish_work),
            backgroundColor = MaterialTheme.colors.secondary,
            contentColor = MaterialTheme.colors.onSecondary
        )
    }
}

@Preview(locale = "ru")
@Composable
private fun PausePrev() {
    WorkTrackerTheme {
        PauseScreenContent(
            state = CardState.Pause(
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
            onClickEndPause = { },
            onClickEndWork = { }
        )
    }
}