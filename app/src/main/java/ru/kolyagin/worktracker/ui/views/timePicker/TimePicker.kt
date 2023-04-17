package ru.kolyagin.worktracker.ui.views.timePicker

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.kolyagin.worktracker.ui.theme.PrimaryVariant
import ru.kolyagin.worktracker.ui.theme.StatePrimaryWhite38
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TimePicker(
    selectedHour: MutableState<Int>,
    selectedMinute: MutableState<Int>,
    modifier: Modifier = Modifier,
    colorSelect: Color = MaterialTheme.colors.primaryVariant,
    colorUnselect: Color = StatePrimaryWhite38,
    clockColor: Color = MaterialTheme.colors.background,
    backgroundColor: Color = MaterialTheme.colors.onPrimary
) {
    val selectedPart = remember { mutableStateOf(TimePart.Hour) }
    val selectedTime by remember {
        derivedStateOf { if (selectedPart.value == TimePart.Hour) selectedHour.value else selectedMinute.value / 5 }
    }

    val onTime: (Int) -> Unit = remember {
        {
            if (selectedPart.value == TimePart.Hour) selectedHour.value =
                it else selectedMinute.value = it * 5
        }
    }

    Column(
        modifier = modifier
            .background(
                color = backgroundColor
            )
    ) {
        TimeString(
            selectedHour = selectedHour.value,
            selectedMinute = selectedMinute.value,
            selectedPart = selectedPart,
            modifier = Modifier.align(CenterHorizontally),
            colorSelect = colorSelect,
            colorUnselect = colorUnselect
        )
        Spacer(modifier = Modifier.height(2.dp))
        Clock(
            time = selectedTime,
            modifier = Modifier
                .size(230.dp)
                .align(CenterHorizontally),
            backgroundColor = clockColor
        ) {
            ClockMarks24h(selectedPart.value, selectedTime, onTime)
        }
    }
}

@Composable
fun Clock(
    time: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.background,
    selectedColor: Color = MaterialTheme.colors.primaryVariant,
    hourCirclePx: Float = 46f,
    padding: Dp = 4.dp,
    content: @Composable () -> Unit,
) {
    var radiusPx by remember { mutableStateOf(0) }
    var radiusInsidePx by remember { mutableStateOf(0) }

    fun posX(index: Int) =
        ((if (index < 12) radiusPx else radiusInsidePx) * cos(angleForIndex(index))).toInt()

    fun posY(index: Int) =
        ((if (index < 12) radiusPx else radiusInsidePx) * sin(angleForIndex(index))).toInt()

    Box(modifier = modifier) {
        Surface(
            color = backgroundColor,
            shape = CircleShape,
            modifier = Modifier.fillMaxSize()
        ) {}
        Layout(
            content = content,
            modifier = Modifier
                .padding(padding)
                .drawBehind {
                    val end = Offset(
                        x = size.width / 2 + posX(time),
                        y = size.height / 2 + posY(time)
                    )

                    drawCircle(
                        radius = 9f,
                        color = selectedColor,
                    )

                    drawLine(
                        start = center,
                        end = end,
                        color = selectedColor,
                        strokeWidth = 4f
                    )

                    drawCircle(
                        radius = hourCirclePx,
                        center = end,
                        color = selectedColor,
                    )
                }
        ) { measurables, constraints ->
            val placeables = measurables.map { it.measure(constraints) }
            assert(placeables.count() == 12 || placeables.count() == 24) { "Missing items: should be 12 or 24, is ${placeables.count()}" }

            layout(constraints.maxWidth, constraints.maxHeight) {
                val size = constraints.maxWidth
                val maxSize = maxOf(placeables.maxOf { it.width }, placeables.maxOf { it.height })

                radiusPx = (constraints.maxWidth - maxSize) / 2
                radiusInsidePx = (radiusPx * 0.67).toInt()

                placeables.forEachIndexed { index, placeable ->
                    placeable.place(
                        size / 2 - placeable.width / 2 + posX(index),
                        size / 2 - placeable.height / 2 + posY(index),
                    )
                }
            }
        }
    }
}

@Composable
fun Mark(
    text: String,
    index: Int, // 0..23
    onIndex: (Int) -> Unit,
    isSelected: Boolean,
    textStyle: TextStyle = MaterialTheme.typography.h6,
    colorUnselect: Color = PrimaryVariant,
    colorSelect: Color = StatePrimaryWhite38
) {
    Text(
        text = text,
        style = textStyle,
        color = if (isSelected) colorSelect else colorUnselect,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = { onIndex(index) }
        )
    )
}

@Composable
fun TimeString(
    selectedHour: Int,
    selectedMinute: Int,
    selectedPart: MutableState<TimePart>,
    colorSelect: Color,
    colorUnselect: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        TimeCard(
            time = selectedHour,
            colorSelect = colorSelect,
            colorUnselect = colorUnselect,
            isSelected = selectedPart.value == TimePart.Hour,
            onClick = { selectedPart.value = TimePart.Hour }
        )

        Text(
            text = ":",
            style = MaterialTheme.typography.h2,
            color = StatePrimaryWhite38,
        )

        TimeCard(
            colorSelect = colorSelect,
            colorUnselect = colorUnselect,
            time = selectedMinute,
            isSelected = selectedPart.value == TimePart.Minute,
            onClick = { selectedPart.value = TimePart.Minute }
        )
    }
}

@Composable
fun TimeCard(
    time: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorSelect: Color = MaterialTheme.colors.primaryVariant,
    colorUnselect: Color = StatePrimaryWhite38,
    textStyle: TextStyle = MaterialTheme.typography.h2
) {
    Text(
        text = if (time == 0) "00" else time.toString(),
        style = textStyle,
        color = if (isSelected) colorSelect else colorUnselect,
        modifier = modifier.clickable { onClick() }
    )

}

enum class TimePart { Hour, Minute }

private const val step = PI * 2 / 12
private fun angleForIndex(hour: Int) = -PI / 2 + step * hour

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun TimePickerPreview() {
    WorkTrackerTheme {
        Box(contentAlignment = Alignment.Center) {
            TimePicker(
                selectedHour = mutableStateOf(0),
                selectedMinute = mutableStateOf(0)
            )
        }
    }
}