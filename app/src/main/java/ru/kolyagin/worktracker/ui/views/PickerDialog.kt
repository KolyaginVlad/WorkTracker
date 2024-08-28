package ru.kolyagin.worktracker.ui.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.ui.theme.OnPrimaryHighEmphasis
import ru.kolyagin.worktracker.ui.theme.Primary
import ru.kolyagin.worktracker.ui.theme.PrimaryVariant
import ru.kolyagin.worktracker.ui.theme.PrimaryVariantDisabled
import ru.kolyagin.worktracker.ui.theme.RoundedButtonShapes

@Composable
fun PickerDialog(
    time: Time,
    title: String,
    onTimePicked: (Time) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    min: Time = Time.MIN,
    max: Time = Time.MAX,
) {
    val hourPickerState = rememberPickerState()
    val minutePickerState = rememberPickerState()
    val hours = remember(min, max) {
        (min.hours..max.hours).map { it.toString() }
    }
    val minutes = remember(min, max, hourPickerState.selectedItem) {
        (0..59).map { it.toString() }
    }
    val startMinuteIndex = remember {
        minutes.indexOf(time.minutes.toString())
    }
    val startHourIndex = remember {
        hours.indexOf(time.hours.toString())
    }
    val selected =
        Time(
            hourPickerState.selectedItem.toIntOrNull() ?: 0,
            minutePickerState.selectedItem.toIntOrNull() ?: 0
        )
    Dialog(onDismissRequest = onCancel) {
        Column(
            modifier = modifier
                .background(MaterialTheme.colors.background, shape = RoundedButtonShapes.medium)
                .padding(12.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = title,
                style = MaterialTheme.typography.subtitle1,
                color = PrimaryVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(size = 32.dp)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Picker(
                    modifier = Modifier.weight(1f),
                    items = hours,
                    state = hourPickerState,
                    startIndex = startHourIndex,
                    textStyle = MaterialTheme.typography.h5.copy(
                        color = Primary
                    ),
                    dividerColor = Primary,
                )
                Picker(
                    modifier = Modifier.weight(1f),
                    items = minutes,
                    state = minutePickerState,
                    startIndex = startMinuteIndex,
                    textStyle = MaterialTheme.typography.h5.copy(
                        color = Primary
                    ),
                    dividerColor = Primary,
                )
            }
            Spacer(size = 32.dp)
            Row {
                Button(
                    modifier = Modifier.weight(1f),
                    enabled = selected in min..max,
                    elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                    shape = RoundedButtonShapes.medium,
                    onClick = {
                        onTimePicked(selected)
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Primary,
                        disabledBackgroundColor = PrimaryVariantDisabled,
                        disabledContentColor = MaterialTheme.colors.background,
                        contentColor = MaterialTheme.colors.background
                    )
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = MaterialTheme.typography.button,
                        text = stringResource(id = R.string.apply),
                    )
                }
                Spacer(size = 12.dp)
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(2.dp, Primary),
                    shape = RoundedButtonShapes.medium,
                    elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = OnPrimaryHighEmphasis,
                        contentColor = Primary,
                    )
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 8.dp),
                        style = MaterialTheme.typography.button,
                        text = stringResource(id = R.string.cancel),
                    )
                }
            }
        }
    }
}