package ru.kolyagin.worktracker.ui.views

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.kolyagin.worktracker.ui.theme.OnPrimaryHighEmphasis
import ru.kolyagin.worktracker.ui.theme.PrimaryVariant
import ru.kolyagin.worktracker.ui.theme.PrimaryVariantDisabled
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 40.dp,
    height: Dp = 24.dp,
    checkedThumbColor: Color = OnPrimaryHighEmphasis,
    uncheckedThumbColor: Color = OnPrimaryHighEmphasis,
    checkedTrackColor: Color = PrimaryVariant,
    uncheckedTrackColor: Color = PrimaryVariantDisabled,
    gapBetweenThumbAndTrackEdge: Dp = 3.dp,
    cornerSize: Int = 50,
    iconInnerPadding: Dp = 0.dp,
    thumbSize: Dp = 16.dp
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val alignment by animateAlignmentAsState(if (checked) 1f else -1f)
    Box(
        modifier = modifier
            .background(
                color = if (checked) checkedTrackColor else uncheckedTrackColor,
                shape = RoundedCornerShape(percent = cornerSize)
            )
            .size(width = width, height = height)
            .clickable
                (
                indication = null,
                interactionSource = interactionSource,
            )
            {
                onCheckedChange(!checked)
            },
    ) {
        Box(
            modifier = Modifier
                .padding(
                    start = gapBetweenThumbAndTrackEdge,
                    end = gapBetweenThumbAndTrackEdge
                )
                .fillMaxSize(),
            contentAlignment = alignment
        ) {
            Canvas(
                Modifier
                    .size(size = thumbSize)
                    .fillMaxSize()
                    .padding(all = iconInnerPadding)
            )
            {
                drawTrack(
                    if (checked) checkedThumbColor else uncheckedThumbColor,
                    thumbSize.toPx(),
                    thumbSize.toPx()
                )
            }
        }
    }
}


private fun DrawScope.drawTrack(trackColor: Color, trackWidth: Float, strokeWidth: Float) {
    val strokeRadius = strokeWidth / 2
    drawLine(
        trackColor,
        Offset(strokeRadius, center.y),
        Offset(trackWidth - strokeRadius, center.y),
        strokeWidth,
        StrokeCap.Round
    )
}

@SuppressLint("UnrememberedMutableState")
@Composable
private fun animateAlignmentAsState(
    targetBiasValue: Float
): State<BiasAlignment> {
    val bias by animateFloatAsState(targetBiasValue, label = "")
    return derivedStateOf { BiasAlignment(horizontalBias = bias, verticalBias = 0f) }
}

@Preview
@Composable
private fun SwitcherPrev() {
    WorkTrackerTheme {
        CustomSwitch(checked = false, onCheckedChange = { _ -> }, modifier = Modifier)
    }
}