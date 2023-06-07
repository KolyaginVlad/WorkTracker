package ru.kolyagin.worktracker.ui.main.views

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.ui.notificationSettings.views.LabelAndSwitch
import ru.kolyagin.worktracker.ui.settings.models.PeriodPart
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import ru.kolyagin.worktracker.ui.views.Spacer
import ru.kolyagin.worktracker.ui.views.TextFieldAndLabel
import ru.kolyagin.worktracker.ui.views.timePicker.TimePicker
import java.util.Locale

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EventsTimePickerDialog(
    onSubmit: (Boolean, Time, PeriodPart, String) -> Unit,
    openDialogCustom: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    period: PeriodPart,
    time: Time = Time(0, 0),
    onlyTime: Boolean = false
) {
    Dialog(
        onDismissRequest = { openDialogCustom.value = false },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            color = Color.Transparent, modifier = modifier.fillMaxWidth(0.95F)
        ) {
            EventsTimePickerDialogUI(
                openDialogCustom = openDialogCustom,
                onSubmit = onSubmit,
                modifier = Modifier,
                timeDef = time,
                period = period,
                onlyTime = onlyTime
            )
        }
    }
}


@Composable
fun EventsTimePickerDialogUI(
    modifier: Modifier,
    openDialogCustom: MutableState<Boolean>,
    onSubmit: (Boolean, Time, PeriodPart, String) -> Unit,
    period: PeriodPart,
    timeDef: Time,
    onlyTime: Boolean
) {
    var checked by remember {
        mutableStateOf(false)
    }
    val selectedHour = remember {
        mutableStateOf(timeDef.hours)
    }
    val selectedMinute = remember {
        mutableStateOf(timeDef.minutes)
    }
    val breakEvent = stringResource(id = R.string.break_work)
    val name = remember {
        mutableStateOf(breakEvent)
    }
    Card(
        shape = RoundedCornerShape(40.dp),
        modifier = modifier,
    ) {
        Column(
            Modifier
                .background(MaterialTheme.colors.onPrimary)
                .padding(start = 12.dp, end = 12.dp, top = 32.dp, bottom = 12.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.add_break),
                color = MaterialTheme.colors.primaryVariant,
                modifier = Modifier.align(CenterHorizontally)
            )
            if (!onlyTime) {
                TextFieldAndLabel(modifier = Modifier
                    .padding(
                        top = 20.dp, start = 16.dp, end = 14.dp, bottom = 4.dp
                    )
                    .fillMaxWidth(),
                    label = stringResource(id = R.string.take_for_every_day),
                    name = name,
                    onValueChange = { name.value = it })
                Spacer(size = 28.dp)
            }
            TimePicker(
                selectedHour = selectedHour,
                selectedMinute = selectedMinute,
                modifier = Modifier.align(CenterHorizontally)
            )
            if (!onlyTime) {
                LabelAndSwitch(style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(
                        top = 32.dp, start = 20.dp, end = 20.dp, bottom = 32.dp
                    ),
                    label = stringResource(id = R.string.take_for_every_day),
                    value = checked,
                    onCheck = { checked = !checked })
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    borderColor = MaterialTheme.colors.primaryVariant,
                    onClick = {
                        onSubmit(
                            checked,
                            Time(selectedHour.value, selectedMinute.value),
                            period,
                            name.value
                        )
                        openDialogCustom.value = !openDialogCustom.value
                    },
                    modifier = Modifier,
                    backgroundColor = MaterialTheme.colors.primaryVariant,
                    text = stringResource(id = R.string.submit).uppercase(Locale.ROOT),
                    contentColor = MaterialTheme.colors.onPrimary
                )
                Button(
                    modifier = Modifier,
                    onClick = { openDialogCustom.value = !openDialogCustom.value },
                    backgroundColor = MaterialTheme.colors.onPrimary,
                    text = stringResource(id = R.string.cancel).uppercase(Locale.ROOT),
                    contentColor = MaterialTheme.colors.primaryVariant,
                )
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(locale = "ru")
@Composable
private fun EventsDialogPrev() {
    WorkTrackerTheme {
        EventsTimePickerDialog(
            modifier = Modifier,
            openDialogCustom = mutableStateOf(true),
            onSubmit = { _, _, _, _ -> },
            period = PeriodPart.START
        )
    }
}