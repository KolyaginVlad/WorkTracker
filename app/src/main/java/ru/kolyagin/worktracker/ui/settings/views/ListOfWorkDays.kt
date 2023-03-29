package ru.kolyagin.worktracker.ui.settings.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.DayWorkInfo
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.models.WorkPeriod
import ru.kolyagin.worktracker.ui.settings.models.PeriodPart
import ru.kolyagin.worktracker.ui.theme.Red
import ru.kolyagin.worktracker.ui.theme.Secondary
import ru.kolyagin.worktracker.ui.theme.SurfaceDisabled
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import ru.kolyagin.worktracker.ui.utils.toStringId
import java.time.DayOfWeek

@Composable
fun ListOfWorkDays(
    listOfWorkPeriods: ImmutableList<DayWorkInfo>,
    onClickPeriod: (DayOfWeek, WorkPeriod, PeriodPart) -> Unit,
    onDeletePeriod: (WorkPeriod) -> Unit,
    onAddPeriod: (DayOfWeek) -> Unit,
    onDinnerChange: (DayOfWeek, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOfWorkPeriods.forEach {
            WorkDay(
                modifier = Modifier.fillMaxWidth(),
                dayWorkInfo = it,
                onClickPeriod = onClickPeriod,
                onDeletePeriod = onDeletePeriod,
                onAddPeriod = onAddPeriod,
                onDinnerChange = onDinnerChange
            )
        }
    }
}

@Composable
private fun WorkDay(
    dayWorkInfo: DayWorkInfo,
    onClickPeriod: (DayOfWeek, WorkPeriod, PeriodPart) -> Unit,
    onDeletePeriod: (WorkPeriod) -> Unit,
    onAddPeriod: (DayOfWeek) -> Unit,
    onDinnerChange: (DayOfWeek, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadow(
            elevation = 4.dp,
            shape = RoundedCornerShape(8.dp)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = dayWorkInfo.day.toStringId()),
                    fontSize = 20.sp
                )
                Text(
                    text = dayWorkInfo.totalTime.toString(),
                    fontSize = 20.sp
                )
            }
            Dinner(
                isInclude = dayWorkInfo.isDinnerInclude,
                onDinnerChange = remember(dayWorkInfo) {
                    {
                        onDinnerChange(dayWorkInfo.day, it)
                    }
                }
            )
            ProvideTextStyle(
                value = TextStyle(
                    color = SurfaceDisabled
                )
            ) {
                Periods(
                    dayWorkInfo = dayWorkInfo,
                    onClickPeriod = onClickPeriod,
                    onDeletePeriod = onDeletePeriod
                )
            }
            Icon(
                modifier = Modifier.clickable(
                    onClick = remember(dayWorkInfo) {
                        {
                            onAddPeriod(dayWorkInfo.day)
                        }
                    }
                ),
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Secondary
            )
        }
    }
}

@Composable
fun Dinner(
    isInclude: Boolean,
    onDinnerChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.dinner),
            fontSize = 18.sp
        )
        Switch(checked = isInclude, onCheckedChange = onDinnerChange)
    }
}

@Composable
private fun ColumnScope.Periods(
    dayWorkInfo: DayWorkInfo,
    onClickPeriod: (DayOfWeek, WorkPeriod, PeriodPart) -> Unit,
    onDeletePeriod: (WorkPeriod) -> Unit
) {
    dayWorkInfo.periods.forEach {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        ) {
            Text(
                modifier = Modifier
                    .clickable(
                        onClick = remember(dayWorkInfo) {
                            {
                                onClickPeriod(dayWorkInfo.day, it, PeriodPart.START)
                            }
                        }
                    )
                    .align(Alignment.CenterStart),
                text = it.timeStart.toString()
            )
            Icon(
                modifier = Modifier
                    .clickable(
                        onClick = remember(dayWorkInfo) {
                            {
                                onDeletePeriod(it)
                            }
                        }
                    )
                    .align(Alignment.Center),
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = Red
            )
            Text(
                modifier = Modifier
                    .clickable(
                        onClick = remember(dayWorkInfo) {
                            {
                                onClickPeriod(dayWorkInfo.day, it, PeriodPart.END)
                            }
                        }
                    )
                    .align(Alignment.CenterEnd),
                text = it.timeEnd.toString()
            )
        }
    }
}

@Preview
@Composable
private fun ListOfDaysPrev() {
    WorkTrackerTheme {
        ListOfWorkDays(
            listOfWorkPeriods = persistentListOf(
                DayWorkInfo(
                    DayOfWeek.MONDAY,
                    persistentListOf(
                        WorkPeriod(
                            1,
                            Time(9, 0),
                            Time(19, 0)
                        )
                    ),
                    true
                ),
                DayWorkInfo(
                    DayOfWeek.TUESDAY,
                    persistentListOf(
                        WorkPeriod(2,
                            Time(8, 0),
                            Time(11, 20)
                        ),
                        WorkPeriod(
                            3,
                            Time(14, 0),
                            Time(19, 0)
                        ),
                    ),
                    false
                ),
                DayWorkInfo(
                    DayOfWeek.WEDNESDAY,
                    persistentListOf(
                        WorkPeriod(
                            4,
                            Time(8, 0),
                            Time(11, 20)
                        ),
                        WorkPeriod(
                            5,
                            Time(14, 0),
                            Time(19, 0)
                        ),
                    ),
                    false
                ),
                DayWorkInfo(
                    DayOfWeek.THURSDAY,
                    persistentListOf(
                        WorkPeriod(
                            6,
                            Time(8, 0),
                            Time(11, 20)
                        ),
                        WorkPeriod(
                            7,
                            Time(14, 0),
                            Time(19, 0)
                        ),
                    ),
                    false
                ),
                DayWorkInfo(
                    DayOfWeek.FRIDAY,
                    persistentListOf(
                        WorkPeriod(
                            8,
                            Time(9, 0),
                            Time(19, 0)
                        )
                    ),
                    true
                ),
                DayWorkInfo(
                    DayOfWeek.SATURDAY,
                    persistentListOf(
                        WorkPeriod(
                            9,
                            Time(9, 0),
                            Time(19, 0)
                        )
                    ),
                    true
                ),
            ),
            onClickPeriod = { _, _, _ -> },
            onDeletePeriod = { _ -> },
            onAddPeriod = { _ -> },
            onDinnerChange = { _, _ -> }
        )
    }
}