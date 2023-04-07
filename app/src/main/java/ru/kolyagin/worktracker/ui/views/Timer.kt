package ru.kolyagin.worktracker.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.ui.theme.PrimaryVariant
import ru.kolyagin.worktracker.ui.theme.PrimaryVariantDisabled
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Timer(
    time: TimeWithSeconds,
    modifier: Modifier = Modifier,
    primaryColor: Color = PrimaryVariant,
    disableColor: Color = PrimaryVariantDisabled
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        TimerPart(
            partValue = time.hours,
            partLabel = pluralStringResource(R.plurals.hours, time.hours),
            primaryColor = primaryColor,
            disableColor = disableColor
        )
        TimerPart(
            partValue = time.minutes,
            partLabel = pluralStringResource(R.plurals.minutes, time.minutes),
            primaryColor = primaryColor,
            disableColor = disableColor
        )
        TimerPart(
            partValue = time.seconds,
            partLabel = pluralStringResource(R.plurals.seconds, time.seconds),
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