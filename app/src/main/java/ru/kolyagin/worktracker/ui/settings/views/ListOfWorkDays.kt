package ru.kolyagin.worktracker.ui.settings.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import ru.kolyagin.worktracker.ui.theme.OnPrimaryHighEmphasis
import ru.kolyagin.worktracker.ui.theme.PrimaryVariant
import ru.kolyagin.worktracker.ui.theme.RoundedButtonShapes
import ru.kolyagin.worktracker.ui.theme.SurfaceDisabled
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import ru.kolyagin.worktracker.ui.utils.toShortStringId
import ru.kolyagin.worktracker.ui.views.AddButton
import ru.kolyagin.worktracker.ui.views.CustomSwitch
import ru.kolyagin.worktracker.ui.views.Spacer
import java.time.DayOfWeek

@Composable
fun ListOfWorkDays(
    listOfWorkPeriods: ImmutableList<DayWorkInfo>,
    onClickPeriod: (DayOfWeek, WorkPeriod, PeriodPart) -> Unit,
    onDeletePeriod: (WorkPeriod) -> Unit,
    onAddPeriod: (DayOfWeek) -> Unit,
    onDinnerChange: (DayOfWeek, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    totalTime: Time
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TotalScheduleInfo(
            modifier = Modifier
                .fillMaxWidth(),
            totalTime = totalTime
        )
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
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colors.primaryVariant,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(40.dp),
        backgroundColor = OnPrimaryHighEmphasis
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = dayWorkInfo.day.toShortStringId()),
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.weight(1F),
                    color = contentColor
                )
                Text(
                    text = dayWorkInfo.timeWithOutConflux.toString(),
                    color = contentColor,
                    style = MaterialTheme.typography.h4,
                )
            }
            Spacer(size = 5.dp)
            Dinner(
                isInclude = dayWorkInfo.isDinnerInclude,
                onDinnerChange = remember(dayWorkInfo) {
                    {
                        onDinnerChange(dayWorkInfo.day, it)
                    }
                },
                contentColor = contentColor
            )
            Spacer(size = 10.dp)
            ProvideTextStyle(
                value = TextStyle(
                    color = SurfaceDisabled
                )
            ) {
                Periods(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    dayWorkInfo = dayWorkInfo,
                    onClickPeriod = onClickPeriod,
                    onDeletePeriod = onDeletePeriod
                )
            }
            AddButton(
                day = dayWorkInfo.day.ordinal,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                onAddPeriod = remember(dayWorkInfo) {
                    {
                        onAddPeriod(dayWorkInfo.day)
                    }
                },
                height = 16.dp
            )
        }
    }
}

@Composable
fun Dinner(
    isInclude: Boolean,
    onDinnerChange: (Boolean) -> Unit,
    contentColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.dinner),
            fontSize = 18.sp,
            color = contentColor
        )
        CustomSwitch(
            checked = isInclude,
            onCheckedChange = onDinnerChange,
            modifier = Modifier,
        )
    }
}

@Composable
private fun ColumnScope.Periods(
    dayWorkInfo: DayWorkInfo,
    modifier: Modifier,
    onClickPeriod: (DayOfWeek, WorkPeriod, PeriodPart) -> Unit,
    onDeletePeriod: (WorkPeriod) -> Unit,
    contentColor: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = MaterialTheme.colors.onPrimary,
) {
    dayWorkInfo.periods.forEach {
        OutlinedButton(
            border = BorderStroke(2.dp, contentColor),
            modifier = modifier,
            shape = RoundedButtonShapes.medium,
            onClick = {},
            colors = ButtonDefaults.buttonColors(
                backgroundColor = backgroundColor,
                contentColor = contentColor
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
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
                    tint = PrimaryVariant
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
}

@Preview
@Composable
private fun ListOfDaysPrev() {
    WorkTrackerTheme {
        ListOfWorkDays(
            totalTime = Time(0, 0),
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
                        WorkPeriod(
                            2,
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
                    true
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
            onDinnerChange = { _, _ -> },
        )
    }
}