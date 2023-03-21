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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.immutableListOf
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.ui.main.CardState
import ru.kolyagin.worktracker.ui.theme.OnPrimaryDisabled
import ru.kolyagin.worktracker.ui.theme.Primary
import ru.kolyagin.worktracker.ui.theme.RoundedButtonShapes
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import ru.kolyagin.worktracker.utils.models.DayOfWeek

@Composable
fun WorkStartScreenContent(
    state: CardState.WorkStart,
    onClickStartWork: () -> Unit,
    buttonActive: Boolean,
    events: ImmutableList<Event> ,
    onClickDeleteMeal: () -> Unit,
    onAddPeriod: () -> Unit ,
    onClickEvent: () -> Unit


) {
    Column {
        HeaderDay(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            state = state
        )
        StartButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 0.dp),
            onClickStartWork = onClickStartWork,
            buttonActive = buttonActive
        )
        EventList(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp, 12.dp),
            events = events,
            onClickDeleteMeal = onClickDeleteMeal,
            onAddPeriod = onAddPeriod,
            onClickEvent = onClickEvent
        )
    }


}

@Composable
fun HeaderDay(
    modifier: Modifier, state: CardState.WorkStart,
) {
    Row(
        modifier = modifier

    ) {
        Text(text = state.day.name, style = MaterialTheme.typography.h4)
    }
}

@Composable
fun StartButton(
    modifier: Modifier, onClickStartWork: () -> Unit,
    buttonActive: Boolean
) {
    var buttonColor: Color
    var buttonclick: () -> Unit
    if (buttonActive) {
        buttonColor = MaterialTheme.colors.primary
        buttonclick = onClickStartWork
    } else {
        buttonColor = OnPrimaryDisabled
        buttonclick = {}
    }
    Row(
        modifier = modifier

    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedButtonShapes.medium,
            onClick = buttonclick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = buttonColor,
                contentColor = MaterialTheme.colors.background
            )
        ) {
            Text(
                modifier = Modifier.padding(16.dp, 20.dp),
                text = stringResource(id = R.string.work_start)
            )
        }
    }
}

@Composable
fun EventList(
    modifier: Modifier,
    events: ImmutableList<Event>,
    onClickDeleteMeal: () -> Unit,
    onAddPeriod: () -> Unit,
    onClickEvent:()-> Unit,
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
                onClick = {onClickEvent }) {
                Row(modifier = Modifier.padding(16.dp, 20.dp)) {
                    Text(
                        text = it.timeStart.toString() + "-" + it.timeEnd.toString(),
                        modifier = Modifier.weight(1F),

                        )
                    Text(text = it.name)
                    Icon(
                        modifier = Modifier
                            .clickable(onClick = onClickDeleteMeal)
                            .align(CenterVertically)
                            .padding(20.dp, 0.dp, 0.dp, 0.dp),
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = null,
                        tint = Primary
                    )
                }


            }
        }
        OutlinedButton(border =
                BorderStroke(2.dp, Primary),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedButtonShapes.medium,
            onClick = { onAddPeriod() }) {
            Icon(
                modifier = Modifier
                    .clickable(
                        onClick =
                        {
                            onAddPeriod()
                        }

                    )
                    .padding(16.dp, 20.dp),
                painter = painterResource(id = R.drawable.plus),
                contentDescription = null,
                tint = Primary

            )
        }
    }
}

data class Event(
    val id: Long,
    val timeStart: Time,
    val timeEnd: Time,
    val name: String
)

@Preview
@Composable
private fun WorkStartScreenPreview() {
    WorkTrackerTheme {
        WorkStartScreenContent(
            state = CardState.WorkStart(DayOfWeek.Monday),
            onClickStartWork = {},
            events = immutableListOf(
                Event(
                    id = 0,
                    timeStart = Time(19, 0),
                    timeEnd = Time(19, 10),
                    name = "УЖИН"
                )
            ),
            onClickDeleteMeal = {},
            buttonActive = false,
            onAddPeriod = {},
            onClickEvent = {}

        )
    }
}