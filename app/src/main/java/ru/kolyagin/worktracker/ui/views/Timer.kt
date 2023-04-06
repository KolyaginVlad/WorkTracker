package ru.kolyagin.worktracker.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.ui.theme.PrimaryVariant
import ru.kolyagin.worktracker.ui.theme.PrimaryVariantDisabled
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Timer(
    time: TimeWithSeconds,
    modifier: Modifier = Modifier,
    secondsChanger: Int = 1,
    primaryColor: Color = PrimaryVariant,
    disableColor: Color = PrimaryVariantDisabled
) {
    var newTime by remember {
        mutableStateOf(time)
    }
    LaunchedEffect(time) {
        while (isActive) {
            val current = LocalTime.now()
            newTime += TimeWithSeconds.fromSeconds(secondsChanger.toLong())
            delay(ChronoUnit.MILLIS.between(LocalTime.now(), current.plusSeconds(1)))
        }
    }
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        TimerPart(
            partValue = newTime.hours,
            partLabel = pluralStringResource(R.plurals.hours, newTime.hours),
            primaryColor = primaryColor,
            disableColor = disableColor
        )
        TimerPart(
            partValue = newTime.minutes,
            partLabel = pluralStringResource(R.plurals.minutes, newTime.minutes),
            primaryColor = primaryColor,
            disableColor = disableColor
        )
        TimerPart(
            partValue = newTime.seconds,
            partLabel = pluralStringResource(R.plurals.seconds, newTime.seconds),
            primaryColor = primaryColor,
            disableColor = disableColor
        )
    }
}

@Composable
private fun RowScope.TimerPart(
    partValue: Int,
    partLabel: String,
    primaryColor: Color,
    disableColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "%02d".format(partValue),
            style = MaterialTheme.typography.h2,
            color = if (partValue == 0) disableColor
            else primaryColor
        )
        Text(
            text = partLabel,
            style = MaterialTheme.typography.subtitle1,
            color = disableColor
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, locale = "ru")
@Composable
private fun TimerPrev() {
    WorkTrackerTheme {
        Timer(time = TimeWithSeconds(0, 30, 60), modifier = Modifier.fillMaxSize())
    }
}