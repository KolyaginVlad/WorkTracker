package ru.kolyagin.worktracker.ui.main.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.ui.main.CardState
import ru.kolyagin.worktracker.ui.models.DayStartEvent
import ru.kolyagin.worktracker.ui.theme.Primary
import ru.kolyagin.worktracker.ui.theme.PrimaryVariantDisabled
import ru.kolyagin.worktracker.ui.theme.RoundedButtonShapes
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import ru.kolyagin.worktracker.ui.utils.toShortStringId
import ru.kolyagin.worktracker.ui.views.Timer
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
        WorkStartTimer(time = state.time)
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
fun WorkStartTimer(time: TimeWithSeconds?) {
    time?.let {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.time_before_work),
                modifier = Modifier.align(CenterHorizontally),
                style = MaterialTheme.typography.h5,
                color = MaterialTheme.colors.primaryVariant
            )
            Timer(
                time = it,
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(start = 40.dp, top = 8.dp, end = 40.dp, bottom = 24.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun HeaderDay(
    modifier: Modifier,
    day: DayOfWeek,
    onClickDeleteDay: () -> Unit
) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = day.toShortStringId()),
            style = MaterialTheme.typography.h4,
            modifier = Modifier.weight(1F),
            color = MaterialTheme.colors.primaryVariant
        )
        Icon(
            modifier = Modifier
                .clickable(onClick = onClickDeleteDay)
                .align(CenterVertically)
                .padding(start = 18.dp, top = 0.dp, end = 0.dp, bottom = 0.dp),
            painter = painterResource(id = R.drawable.delete),
            contentDescription = null,
            tint = MaterialTheme.colors.primaryVariant
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

@Composable
fun EventList(
    modifier: Modifier,
    events: ImmutableList<DayStartEvent>,
    onClickDeleteMeal: () -> Unit,
    onAddPeriod: () -> Unit,
    onClickEvent: () -> Unit,
) {
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        events.forEach {
            OutlinedButton(
                border = BorderStroke(2.dp, Primary),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedButtonShapes.medium,
                onClick = onClickEvent
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp)
                ) {
                    Text(
                        text = it.timeStart.toString() + "-" + it.timeEnd.toString(),
                        modifier = Modifier.weight(1F),
                    )
                    Text(text = it.name)
                    Icon(
                        modifier = Modifier
                            .clickable(onClick = onClickDeleteMeal)
                            .align(CenterVertically)
                            .padding(start = 18.dp, top = 0.dp, end = 0.dp, bottom = 0.dp),
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = null,
                        tint = Primary
                    )
                }
            }
        }
        OutlinedButton(
            border = BorderStroke(2.dp, Primary),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedButtonShapes.medium,
            onClick = onAddPeriod
        ) {
            Icon(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                painter = painterResource(id = R.drawable.plus),
                contentDescription = null,
                tint = Primary
            )
        }
    }
}

@Preview
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