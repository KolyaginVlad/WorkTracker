package ru.kolyagin.worktracker.ui.notificationSettings.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.kolyagin.worktracker.ui.theme.StatePrimaryWhite38
import ru.kolyagin.worktracker.ui.theme.StatePrimaryWhite74
import ru.kolyagin.worktracker.ui.utils.toShortStringId
import java.time.DayOfWeek

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
                    val delta =
                        Math.abs(intkodOfDay - dayOfWeek.value) //расположение дня относительно текущего
                    when (i) { //крайние варианты
                        -1 -> {
                            intkodOfDay = 6
                        }

                        -2 -> {
                            intkodOfDay = 5
                        }

                        7 -> {
                            intkodOfDay = 0
                        }

                        8 -> {
                            intkodOfDay = 1
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