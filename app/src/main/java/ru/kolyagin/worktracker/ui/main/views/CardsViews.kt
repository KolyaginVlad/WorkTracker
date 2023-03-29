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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.ui.models.DayStartEvent
import ru.kolyagin.worktracker.ui.theme.PrimaryVariantDisabled
import ru.kolyagin.worktracker.ui.theme.RoundedButtonShapes
import ru.kolyagin.worktracker.ui.utils.toShortStringId
import ru.kolyagin.worktracker.ui.views.Timer
import java.time.DayOfWeek

@Composable
fun HeaderDay(
    modifier: Modifier,
    day: DayOfWeek,
    contentColor: Color = MaterialTheme.colors.primaryVariant,
    backgroundColor: Color = MaterialTheme.colors.onPrimary,
    onClickDeleteDay: () -> Unit
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
        Icon(
            modifier = Modifier
                .clickable(onClick = onClickDeleteDay)
                .align(Alignment.CenterVertically)
                .padding(start = 18.dp, top = 0.dp, end = 0.dp, bottom = 0.dp),
            painter = painterResource(id = R.drawable.delete),
            contentDescription = null,
            tint = contentColor
        )
    }
}

@Composable
fun WorkTimer(
    time: TimeWithSeconds?,
    title: String,
    primaryColor: Color = MaterialTheme.colors.primaryVariant,
    disableColor: Color = PrimaryVariantDisabled,
) {
    time?.let {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.h5,
                color = primaryColor
            )
            Timer(
                time = it,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 40.dp, top = 8.dp, end = 40.dp, bottom = 24.dp)
                    .fillMaxWidth(),
                primaryColor = primaryColor,
                disableColor = disableColor
            )
        }
    }
}

@Composable
fun EventList(
    modifier: Modifier,
    events: ImmutableList<DayStartEvent>,
    contentColor: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = MaterialTheme.colors.onPrimary,
    onClickDeleteMeal: () -> Unit,
    onAddPeriod: () -> Unit,
    onClickEvent: () -> Unit,
) {
    Column(
        modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        events.forEach {
            OutlinedButton(
                border = BorderStroke(2.dp, contentColor),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedButtonShapes.medium,
                onClick = onClickEvent,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = backgroundColor,
                    contentColor = contentColor
                )
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
                            .align(Alignment.CenterVertically)
                            .padding(start = 18.dp, top = 0.dp, end = 0.dp, bottom = 0.dp),
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = null,
                        tint = contentColor
                    )
                }
            }
        }
        OutlinedButton(
            border = BorderStroke(2.dp, contentColor),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedButtonShapes.medium,
            onClick = onAddPeriod,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = backgroundColor,
                contentColor = contentColor
            )
        ) {
            Icon(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                painter = painterResource(id = R.drawable.plus),
                contentDescription = null,
                tint = contentColor
            )
        }
    }
}


@Composable
fun Button(
    text: String,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    borderColor: Color = contentColor,
    onClick: () -> Unit = {}
) {
    Button(
        modifier = modifier,
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