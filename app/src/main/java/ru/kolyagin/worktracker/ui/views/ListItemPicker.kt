package ru.kolyagin.worktracker.ui.views

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

private fun <T> getItemIndexForOffset(
    range: List<T>,
    value: T,
    offset: Float,
    halfNumbersColumnHeightPx: Float
): Int {
    val indexOf = range.indexOf(value) - (offset / halfNumbersColumnHeightPx).toInt()
    return maxOf(0, minOf(indexOf, range.count() - 1))
}

@Composable
fun <T> ListItemPicker(
    modifier: Modifier = Modifier,
    label: @Composable (T) -> String = { it.toString() },
    value: T,
    onValueChange: (T) -> Unit,
    list: List<T>,
    textStyle: TextStyle = LocalTextStyle.current,
    selectedTextStyle: TextStyle = LocalTextStyle.current,
) {
    val textMeasurer = rememberTextMeasurer()
    val minimumAlphaNear = 0.3f
    val minimumAlpha = 0.1f
    val verticalMargin = 8.dp
    val numbersColumnHeight = with(LocalDensity.current) {
        list.map {
            label(it)
        }.maxOf {
            textMeasurer.measure(it, selectedTextStyle).size.width
        }.toDp()
    }
    val halfNumbersColumnHeight = numbersColumnHeight
    val halfNumbersColumnHeightPx = with(LocalDensity.current) { halfNumbersColumnHeight.toPx() }

    val coroutineScope = rememberCoroutineScope()

    val animatedOffset = remember { Animatable(0f) }
        .apply {
            val index = list.indexOf(value)
            val offsetRange = remember(value, list) {
                -((list.count() - 1) - index) * halfNumbersColumnHeightPx to
                        index * halfNumbersColumnHeightPx
            }
            updateBounds(offsetRange.first, offsetRange.second)
        }

    val coercedAnimatedOffset = animatedOffset.value % halfNumbersColumnHeightPx

    val indexOfElement = getItemIndexForOffset(list, value, animatedOffset.value, halfNumbersColumnHeightPx)

    Layout(
        modifier = modifier
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { deltaY ->
                    coroutineScope.launch {
                        animatedOffset.snapTo(animatedOffset.value + deltaY)
                    }
                },
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        val endValue = animatedOffset.fling(
                            initialVelocity = velocity,
                            animationSpec = exponentialDecay(frictionMultiplier = 20f),
                            adjustTarget = { target ->
                                val coercedTarget = target % halfNumbersColumnHeightPx
                                val coercedAnchors =
                                    listOf(
                                        -halfNumbersColumnHeightPx,
                                        0f,
                                        halfNumbersColumnHeightPx
                                    )
                                val coercedPoint =
                                    coercedAnchors.minByOrNull { abs(it - coercedTarget) }!!
                                val base =
                                    halfNumbersColumnHeightPx * (target / halfNumbersColumnHeightPx).toInt()
                                coercedPoint + base
                            }
                        ).endState.value

                        val result = list.elementAt(
                            getItemIndexForOffset(list, value, endValue, halfNumbersColumnHeightPx)
                        )
                        onValueChange(result)
                        animatedOffset.snapTo(0f)
                    }
                }
            )
            .padding(horizontal = numbersColumnHeight / 3 + verticalMargin * 2),
        content = {
            Box(
                modifier = Modifier
                    .padding(horizontal = verticalMargin, vertical = 15.dp)
                    .offset { IntOffset(x = coercedAnimatedOffset.roundToInt(), y = 0) }
            ) {
                val baseLabelModifier = Modifier.align(Alignment.Center)
                if (indexOfElement > 1)
                    Label(
                        text = label(list.elementAt(indexOfElement - 2)),
                        textStyle = textStyle,
                        modifier = baseLabelModifier
                            .offset(x = -halfNumbersColumnHeight * 2)
                            .alpha(
                                maxOf(
                                    minimumAlpha,
                                    coercedAnimatedOffset / (halfNumbersColumnHeightPx * 2)
                                )
                            )
                    )
                if (indexOfElement > 0)
                    Label(
                        text = label(list.elementAt(indexOfElement - 1)),
                        textStyle = textStyle,
                        modifier = baseLabelModifier
                            .offset(x = -halfNumbersColumnHeight)
                            .alpha(
                                maxOf(
                                    minimumAlphaNear,
                                    coercedAnimatedOffset / halfNumbersColumnHeightPx
                                )
                            )
                    )
                Label(
                    text = label(list.elementAt(indexOfElement)),
                    textStyle = selectedTextStyle,
                    modifier = baseLabelModifier
                        .alpha(
                            (maxOf(
                                minimumAlphaNear,
                                1 - abs(coercedAnimatedOffset) / halfNumbersColumnHeightPx
                            ))
                        )
                )
                if (indexOfElement < list.count() - 1)
                    Label(
                        text = label(list.elementAt(indexOfElement + 1)),
                        textStyle = textStyle,
                        modifier = baseLabelModifier
                            .offset(x = halfNumbersColumnHeight)
                            .alpha(
                                maxOf(
                                    minimumAlphaNear,
                                    -coercedAnimatedOffset / halfNumbersColumnHeightPx
                                )
                            )
                    )
                if (indexOfElement < list.count() - 2)
                    Label(
                        text = label(list.elementAt(indexOfElement + 2)),
                        textStyle = textStyle,
                        modifier = baseLabelModifier
                            .offset(x = halfNumbersColumnHeight * 2)
                            .alpha(
                                maxOf(
                                    minimumAlpha,
                                    -coercedAnimatedOffset / (halfNumbersColumnHeightPx * 2)
                                )
                            )
                    )
            }
        }
    ) { measurables, constraints ->
        // Don't constrain child views further, measure them with given constraints
        // List of measured children
        val placeables = measurables.map { measurable ->
            // Measure each children
            measurable.measure(constraints)
        }

        // Set the size of the layout as big as it can
        layout(placeables
            .sumOf {
                it.width
            }, placeables.first().height
        ) {
            // Track the y co-ord we have placed children up to
            var xPosition = 0

            // Place children in the parent layout
            placeables.forEach { placeable ->

                // Position item on the screen
                placeable.placeRelative(x = xPosition, y = 0)

                // Record the y co-ord placed up to
                xPosition += placeable.width
            }
        }
    }
}

@Composable
private fun Label(
    text: String,
    textStyle: TextStyle,
    modifier: Modifier
) {
    Text(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(onLongPress = {
                // FIXME: Empty to disable text selection
            })
        },
        text = text,
        style = textStyle,
        textAlign = TextAlign.Center,
    )
}

private suspend fun Animatable<Float, AnimationVector1D>.fling(
    initialVelocity: Float,
    animationSpec: DecayAnimationSpec<Float>,
    adjustTarget: ((Float) -> Float)?,
    block: (Animatable<Float, AnimationVector1D>.() -> Unit)? = null,
): AnimationResult<Float, AnimationVector1D> {
    val targetValue = animationSpec.calculateTargetValue(value, initialVelocity)
    val adjustedTarget = adjustTarget?.invoke(targetValue)
    return if (adjustedTarget != null) {
        animateTo(
            targetValue = adjustedTarget,
            initialVelocity = initialVelocity,
            block = block
        )
    } else {
        animateDecay(
            initialVelocity = initialVelocity,
            animationSpec = animationSpec,
            block = block,
        )
    }
}