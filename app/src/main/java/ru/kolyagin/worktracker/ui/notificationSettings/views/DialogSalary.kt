package ru.kolyagin.worktracker.ui.notificationSettings.views

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
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
import ru.kolyagin.worktracker.ui.theme.StatePrimaryWhite38
import ru.kolyagin.worktracker.ui.theme.StatePrimaryWhite74
import ru.kolyagin.worktracker.ui.theme.SurfaceHighEmphasis
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import ru.kolyagin.worktracker.ui.utils.toShortStringId
import ru.kolyagin.worktracker.ui.views.Spacer
import java.lang.Math.abs
import java.time.DayOfWeek
import java.util.Locale

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CustomAddDialog(
    onSubmit: (Boolean, Int, Long) -> Unit,
    openDialogCustom: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    daystart: Int = 0
) {
    Dialog(
        onDismissRequest = { openDialogCustom.value = false },
        properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
        Surface(
            color = Color.Transparent,
            modifier = modifier.width(360.dp),
            elevation = 49.dp
        ) {
            CustomDialogUI(
                openDialogCustom = openDialogCustom,
                onSubmit = onSubmit,
                modifier = Modifier,
                daystart = daystart
            )
        }
    }
}

@Composable
fun CustomDialogUI(
    modifier: Modifier,
    openDialogCustom: MutableState<Boolean>,
    onSubmit: (Boolean, Int, Long) -> Unit,
    daystart: Int = 0
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
                text = stringResource(id = R.string.hourly_rate),
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
            if (!checked && daystart==0) DaySelector(
                day,
                modifier = Modifier.padding(start = 59.dp, end = 59.dp, bottom = 24.dp)
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
                    .fillMaxWidth()
            ) {
                Button(
                    borderColor = MaterialTheme.colors.primaryVariant,
                    onClick = {
                        onSubmit(checked, day.value, salaryRate.toLong())
                        openDialogCustom.value = !openDialogCustom.value
                    },
                    modifier = Modifier.weight(1f),
                    backgroundColor = MaterialTheme.colors.primaryVariant,
                    text = stringResource(id = R.string.submit).uppercase(Locale.ROOT),
                    contentColor = MaterialTheme.colors.onPrimary
                )
                Spacer(size = 10.dp)
                Button(
                    modifier = Modifier.weight(1f),
                    // .padding(top = 32.dp, start = 16.dp)
                    onClick = { openDialogCustom.value = !openDialogCustom.value },
                    backgroundColor = MaterialTheme.colors.onPrimary,
                    text = stringResource(id = R.string.cancel).uppercase(Locale.ROOT),
                    contentColor = MaterialTheme.colors.primaryVariant,
                )
            }
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DaySelector(
    dayOfWeek: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    var isSwipeToTheLeft = false
    val draggableState = DraggableState { delta ->
        isSwipeToTheLeft = delta >= 0
    }
    Row(modifier = modifier) {
        AnimatedContent(
            targetState = dayOfWeek.value,
            transitionSpec = {
                fadeIn() with fadeOut()
                /* Анимация перелистывания
                if (targetState > initialState) {
                  slideInHorizontally { height -> height } + fadeIn() with
                          slideOutHorizontally { height -> -height } + fadeOut()
              } else {
                  slideInHorizontally  { height -> -height } + fadeIn() with
                          slideOutHorizontally { height -> height } + fadeOut()
              }.using(
                  SizeTransform(clip = false)
              )*/
            }
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .draggable(
                        state = draggableState,
                        orientation = Orientation.Horizontal,
                        onDragStarted = { },
                        onDragStopped = {
                            if (!isSwipeToTheLeft) {
                                if (dayOfWeek.value >= 6) dayOfWeek.value = 0
                                else dayOfWeek.value++
                            } else {
                                if (dayOfWeek.value <= 0) dayOfWeek.value = 6
                                else dayOfWeek.value--
                            }
                        })

            ) {
                for (i in dayOfWeek.value - 2 until dayOfWeek.value + 3) {
                    var intkodOfDay = i
                    var delta = abs(intkodOfDay - dayOfWeek.value)
                    when (i) {
                        -1 -> {
                            intkodOfDay = 6
                            delta = 1
                        }

                        -2 -> {
                            intkodOfDay = 5
                            delta = 2
                        }

                        7 -> {
                            intkodOfDay = 0
                            delta = 1
                        }

                        8 -> {
                            intkodOfDay = 1
                            delta = 2
                        }
                    }
                    val str =
                        stringResource(
                            id = DayOfWeek.of(intkodOfDay + 1).toShortStringId()
                        )
                    when (delta) {
                        0 -> Text(
                            text = str,
                            style = MaterialTheme.typography.h4,
                            color = MaterialTheme.colors.primary
                        )

                        1 -> Text(
                            text = str,
                            style = MaterialTheme.typography.h5,
                            color = StatePrimaryWhite38
                        )

                        2 -> Text(
                            text = str,
                            style = MaterialTheme.typography.h5,
                            color = StatePrimaryWhite74
                        )

                    }
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = false, locale = "ru", backgroundColor = 0xFFFFFFFF)
@Composable
private fun SalaryDialogPrev() {
    WorkTrackerTheme {
        CustomDialogUI(Modifier, mutableStateOf(true), { _, _, _ -> })
    }
}

