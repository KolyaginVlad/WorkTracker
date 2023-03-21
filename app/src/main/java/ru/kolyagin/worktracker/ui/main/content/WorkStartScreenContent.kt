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
import ru.kolyagin.worktracker.ui.main.CardState
import ru.kolyagin.worktracker.ui.models.DayStartEvent
import ru.kolyagin.worktracker.ui.theme.OnPrimaryDisabled
import ru.kolyagin.worktracker.ui.theme.Primary
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
    onClickDeleteday: () -> Unit = {}
) {
    Column {
        HeaderDay(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            day = state.day,
            onClickDeleteday = onClickDeleteday
        )
        StartButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 0.dp),
            onClickStartWork = onClickStartWork,
            buttonActive = state.buttonActive
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
fun HeaderDay(
    modifier: Modifier, day: DayOfWeek, onClickDeleteday: () -> Unit
) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = day.name,
            style = MaterialTheme.typography.h4,
            modifier = Modifier.weight(1F)
        )
        Icon(
            modifier = Modifier
                .clickable(onClick = onClickDeleteday)
                .align(CenterVertically)
                .padding(18.dp, 0.dp, 0.dp, 0.dp),
            painter = painterResource(id = R.drawable.delete),
            contentDescription = null,
            tint = Primary
        )
    }
}
@Composable
fun StartButton(
    modifier: Modifier, onClickStartWork: () -> Unit,
    buttonActive: Boolean
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
                backgroundColor = MaterialTheme.colors.primary,
                disabledBackgroundColor=OnPrimaryDisabled,
                disabledContentColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.background
            )
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                text = stringResource(id = R.string.work_start)
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
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        events.forEach {
            OutlinedButton(
                border = BorderStroke(2.dp, Primary),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedButtonShapes.medium,
                onClick = onClickEvent
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
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
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 20.dp),
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
        WorkStartScreenContent(
            state = CardState.WorkStart(
                DayOfWeek.Monday,
                events = persistentListOf(
                    DayStartEvent(
                        id = 0,
                        timeStart = Time(19, 0),
                        timeEnd = Time(19, 10),
                        name = "УЖИН"
                    )
                ),
                buttonActive = false,
            ),
            onClickStartWork = {},
            onClickDeleteEvent = {},
            onAddPeriod = {},
            onClickEvent = {},
            onClickDeleteday = {}
        )
    }
}