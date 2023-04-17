package ru.kolyagin.worktracker.ui.notificationSettings.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.DaySalaryRate
import ru.kolyagin.worktracker.ui.theme.RoundedButtonShapes
import ru.kolyagin.worktracker.ui.utils.toShortStringId
import java.time.DayOfWeek
import java.util.Locale


@Composable
fun ListSalary(
    modifier: Modifier,
    salaryRates: ImmutableList<DaySalaryRate>,
    contentColor: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = MaterialTheme.colors.onPrimary,
    onSalaryAdd: () -> Unit,
    onSetSalary: (DayOfWeek) -> Unit,
    onDeleteSalary: (Long) -> Unit,
) {
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        salaryRates.forEach {
            OutlinedButton(
                border = BorderStroke(2.dp, contentColor),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedButtonShapes.medium,
                onClick = remember(it) { { onSetSalary(it.day) } },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = backgroundColor,
                    contentColor = contentColor
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = it.rate.toString() + stringResource(id = R.string.money_per_hour),
                        color = MaterialTheme.colors.primaryVariant,
                        modifier = Modifier.weight(1F)
                    )
                    Text(
                        text = stringResource(id = it.day.toShortStringId()).uppercase(Locale.ROOT),
                        color = MaterialTheme.colors.primaryVariant,
                    )
                    Icon(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 16.dp, top = 0.dp, end = 0.dp, bottom = 0.dp)
                            .clickable(onClick = remember(it) {
                                {
                                    onDeleteSalary(
                                        it.id,
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
        AddButtonSalary(
            modifier = Modifier.fillMaxWidth(),
            onSalaryAdd = onSalaryAdd,
            backgroundColor = backgroundColor,
            contentColor = contentColor
        )
    }
}

@Composable
fun AddButtonSalary(
    modifier: Modifier,
    contentColor: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = MaterialTheme.colors.onPrimary,
    onSalaryAdd: () -> Unit,
    height: Dp = 18.dp,
    width: Dp = 16.dp,
) {
    OutlinedButton(
        border = BorderStroke(2.dp, contentColor),
        modifier = modifier,
        shape = RoundedButtonShapes.medium,
        onClick = onSalaryAdd,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Icon(
            modifier = Modifier.padding(horizontal = width, vertical = height),
            painter = painterResource(id = R.drawable.plus),
            contentDescription = null,
            tint = contentColor
        )
    }
}
