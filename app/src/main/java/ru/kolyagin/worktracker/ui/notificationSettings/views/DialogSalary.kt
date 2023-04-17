package ru.kolyagin.worktracker.ui.notificationSettings.views

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.text.isDigitsOnly
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.ui.main.views.Button
import ru.kolyagin.worktracker.ui.theme.SurfaceHighEmphasis
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import java.util.Locale

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CustomAddDialog(
    onSubmit: (Boolean, Int, Long) -> Unit,
    openDialogCustom: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    daystart: Int = 0,
    showDaySelector: Boolean = true
) {
    Dialog(
        onDismissRequest = { openDialogCustom.value = false },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            color = Color.Transparent,
            modifier = modifier.fillMaxWidth(0.95F)
        ) {
            CustomDialogUI(
                openDialogCustom = openDialogCustom,
                onSubmit = onSubmit,
                modifier = Modifier,
                daystart = daystart,
                showDaySelector = showDaySelector
            )
        }
    }
}

@Composable
fun CustomDialogUI(
    modifier: Modifier,
    openDialogCustom: MutableState<Boolean>,
    onSubmit: (Boolean, Int, Long) -> Unit,
    daystart: Int,
    showDaySelector: Boolean
) {
    var salaryRate by remember {
        mutableStateOf("0")
    }
    var checked by remember {
        mutableStateOf(false)
    }
    val day = remember {
        mutableStateOf(daystart)
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
                text = stringResource(id = R.string.dialog_hourly_rate),
                color = MaterialTheme.colors.primaryVariant,
                modifier = Modifier.align(CenterHorizontally)
            )
            LabelAndSwitch(
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(
                    top = 32.dp,
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 32.dp
                ),
                label = stringResource(id = R.string.take_for_every_day),
                value = checked,
                onCheck = { checked = !checked }
            )
            if (showDaySelector && !checked) DaySelector(
                day,
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .fillMaxWidth(0.7F)
                    .align(CenterHorizontally)
            )
            TextField(colors = TextFieldDefaults.textFieldColors(
                textColor = SurfaceHighEmphasis,
                disabledTextColor = Color.Transparent,
                backgroundColor = MaterialTheme.colors.background,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
                textStyle = MaterialTheme.typography.subtitle1,
                shape = RoundedCornerShape(0.dp),
                value = salaryRate,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .padding(
                        start = 16.dp, end = 14.dp, bottom = 14.dp
                    )
                    .fillMaxWidth(),
                onValueChange = {
                    if (it != "" && it.isDigitsOnly()) salaryRate = it
                }
            )
            Text(
                text = stringResource(id = R.string.money_per_hour).lowercase(Locale.getDefault()),
                modifier = Modifier.padding(start = 28.dp),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.primaryVariant,
            )
            Row(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    borderColor = MaterialTheme.colors.primaryVariant,
                    onClick = {
                        onSubmit(checked, day.value, salaryRate.toLong())
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
@Preview(showBackground = false, locale = "ru", backgroundColor = 0xFFFFFFFF)
@Composable
private fun SalaryDialogPrev() {
    WorkTrackerTheme {
        CustomAddDialog(
            modifier = Modifier,
            openDialogCustom = mutableStateOf(true),
            onSubmit = { _, _, _ -> })
    }
}

