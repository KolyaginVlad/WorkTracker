package ru.kolyagin.worktracker.ui.notificationSettings.views

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.kolyagin.worktracker.ui.utils.toShortStringId
import ru.kolyagin.worktracker.ui.views.ListItemPicker
import java.time.DayOfWeek

@Composable
fun DaySelector(
    dayOfWeek: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    ListItemPicker(
        modifier = modifier,
        value = DayOfWeek.of(dayOfWeek.value + 1),
        onValueChange = { dayOfWeek.value = it.value - 1 },
        list = DayOfWeek.values().toList(),
        label = {
            stringResource(
                id = it.toShortStringId()
            )
        },
        textStyle = MaterialTheme.typography.h5.copy(
            color = MaterialTheme.colors.primary
        ),
        selectedTextStyle = MaterialTheme.typography.h4.copy(
            color = MaterialTheme.colors.primary
        )
    )
}

@Preview
@Composable
private fun DaySelectorPrev() {
    MaterialTheme {
        val day = remember {
            mutableIntStateOf(0)
        }
        DaySelector(dayOfWeek = day)
    }
}