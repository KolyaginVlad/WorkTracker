package ru.kolyagin.worktracker.ui.main.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.WorkEvent
import ru.kolyagin.worktracker.ui.theme.RoundedButtonShapes
import ru.kolyagin.worktracker.ui.utils.toShortStringId
import ru.kolyagin.worktracker.ui.views.AddButton
import ru.kolyagin.worktracker.utils.Constants.BREAK
import java.time.DayOfWeek

@Composable
fun HeaderDay(
    modifier: Modifier,
    day: DayOfWeek,
    contentColor: Color = MaterialTheme.colors.primaryVariant,
    backgroundColor: Color = MaterialTheme.colors.onPrimary,
) {
    Row(
        modifier = modifier.background(backgroundColor)
    ) {
        Text(
            text = stringResource(id = day.toShortStringId()),
            style = MaterialTheme.typography.h4,
            modifier = Modifier.weight(1F),
            color = contentColor
        )
    }
}

@Composable
fun EventList(
    modifier: Modifier,
    events: ImmutableList<WorkEvent>,
    day: Int,
    contentColor: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = MaterialTheme.colors.onPrimary,
    onClickDeleteEvent: (WorkEvent, Int) -> Unit,
    onAddPeriod: (Int) -> Unit,
    onClickEvent: (Int, WorkEvent) -> Unit,
) {
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        events.sortedBy { it.timeStart }.forEach {
            OutlinedButton(
                elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                border = BorderStroke(2.dp, contentColor),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedButtonShapes.medium,
                onClick = remember(it, day) { { onClickEvent(day, it) } },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = backgroundColor,
                    contentColor = contentColor
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 19.dp)
                ) {
                    Text(
                        text = it.timeStart.toString() + "-" + it.timeEnd.toString(),
                        modifier = Modifier.weight(1F),
                    )
                    Text(
                        text = when {
                            it.isDinner -> {
                                stringResource(id = R.string.dinner)
                            }

                            it.name == BREAK -> {
                                stringResource(id = R.string.break_work)
                            }

                            else -> {
                                it.name
                            }
                        }
                    )
                    Icon(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 18.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)
                            .clickable(onClick = remember(it, day) {
                                {
                                    onClickDeleteEvent(
                                        it,
                                        day
                                    )
                                }
                            }),
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = null,
                        tint = contentColor
                    )
                }
            }
        }
        AddButton(
            day = day,
            modifier = Modifier.fillMaxWidth(),
            onAddPeriod = onAddPeriod,
            backgroundColor = backgroundColor,
            contentColor = contentColor
        )
    }
}


@Composable
fun Button(
    text: String,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    borderColor: Color = contentColor,
    onClick: () -> Unit = {},
) {
    Button(
        modifier = modifier,
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
        shape = RoundedButtonShapes.medium,
        border = BorderStroke(2.dp, borderColor),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            text = text
        )
    }
}