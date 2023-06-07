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
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.domain.models.WorkEvent
import ru.kolyagin.worktracker.domain.models.WorkStatistic
import ru.kolyagin.worktracker.ui.main.CardState
import ru.kolyagin.worktracker.ui.main.views.EventList
import ru.kolyagin.worktracker.ui.main.views.HeaderDay
import ru.kolyagin.worktracker.ui.main.views.WorkTimer
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import java.time.DayOfWeek

@Composable
fun ResultsScreenContent(
    state: CardState.Results,
    onClickStartWork: () -> Unit = {},
    onClickDeleteDay: () -> Unit = {},
    onClickDeleteEvent: (WorkEvent, Int) -> Unit = { _, _->},
    onAddPeriod: (Int) -> Unit = {},
    onClickEvent: (Int, WorkEvent) -> Unit = { _, _->},
) {
    Column(modifier = Modifier) {
        HeaderDay(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 17.dp, top = 26.dp, end = 28.dp, bottom = 15.dp),
            day = state.day,
            onClickDeleteDay = onClickDeleteDay
        )
        WorkTimer(
            time = state.statistic.workTime,
            title = stringResource(id = R.string.time_worked),
        )
        StartButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 0.dp),
            onClickStartWork = onClickStartWork,
            buttonActive = true,
            startEarly = false,
            contentColor = MaterialTheme.colors.primaryVariant
        )
        EventList(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            events = state.events,
            day = state.day.ordinal,
            onClickDeleteEvent = onClickDeleteEvent,
            onAddPeriod = onAddPeriod,
            onClickEvent = onClickEvent
        )
    }
}

@Preview
@Composable
private fun ResultsScreenContentPrev() {
    WorkTrackerTheme {
        ResultsScreenContent(
            state = CardState.Results(
                DayOfWeek.SATURDAY,
                WorkStatistic(
                    workTime = TimeWithSeconds(1,0,0),
                    plannedPauseTime = TimeWithSeconds.fromSeconds(0),
                    unplannedPauseTime = TimeWithSeconds.fromSeconds(0),
                ),
                persistentListOf()
            )
        )
    }
}