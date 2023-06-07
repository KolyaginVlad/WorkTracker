package ru.kolyagin.worktracker.ui.main

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.ui.destinations.SettingsScreenDestination
import ru.kolyagin.worktracker.ui.main.content.DinneringScreenContent
import ru.kolyagin.worktracker.ui.main.content.PauseScreenContent
import ru.kolyagin.worktracker.ui.main.content.ResultsScreenContent
import ru.kolyagin.worktracker.ui.main.content.WorkStartScreenContent
import ru.kolyagin.worktracker.ui.main.content.WorkingScreenContent
import ru.kolyagin.worktracker.ui.settings.models.PeriodPart
import ru.kolyagin.worktracker.ui.theme.OnPrimaryHighEmphasis
import ru.kolyagin.worktracker.ui.utils.max
import ru.kolyagin.worktracker.ui.views.Spacer
import ru.kolyagin.worktracker.ui.views.TopBar
import kotlin.math.roundToInt

@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(
    navigator: DestinationsNavigator,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                is MainEvent.OpenSettings -> {
                    navigator.navigate(SettingsScreenDestination.route)
                }

                is MainEvent.AddEventTime -> {
                    TimePickerDialog(
                        context,
                        { _, hour: Int, minute: Int ->
                            viewModel.onTimePicked(Time(hour, minute), PeriodPart.END)
                        }, 14, 0, true
                    ).show()
                    TimePickerDialog(
                        context,
                        { _, hour: Int, minute: Int ->
                            viewModel.onTimePicked(Time(hour, minute), PeriodPart.START)
                        }, 12, 0, true
                    ).show()
                }

                is MainEvent.ChangeEventTime -> {
                    TimePickerDialog(
                        context,
                        { _, hour: Int, minute: Int ->
                            viewModel.onTimeChanging(Time(hour, minute), PeriodPart.END)
                        }, it.timeEnd.hours, it.timeEnd.minutes, true
                    ).show()
                    TimePickerDialog(
                        context,
                        { _, hour: Int, minute: Int ->
                            viewModel.onTimeChanging(Time(hour, minute), PeriodPart.START)
                        }, it.timeStart.hours, it.timeStart.minutes, true
                    ).show()
                }
            }
        }
    }
    val toolbarHeight = 200.dp
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
    val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = toolbarOffsetHeightPx.value + delta
                toolbarOffsetHeightPx.value = newOffset.coerceIn(-toolbarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .nestedScroll(nestedScrollConnection),
    ) {
        val scrollState = rememberScrollState()
        TopBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(toolbarHeight)
                .offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt()) },
            title = stringResource(id = R.string.work_periods)
        ) {
            Icon(
                modifier = Modifier.clickable(onClick = viewModel::onClickOpenSettings),
                painter = painterResource(id = R.drawable.settings),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
        Column(
            modifier = Modifier
                .padding(
                    top = max(
                        toolbarHeight - 48.dp + with(LocalDensity.current) {
                            toolbarOffsetHeightPx.value.toDp()
                        },
                        0.dp
                    )
                )
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 56.dp, topEnd = 56.dp))
                .background(MaterialTheme.colors.background)
                .verticalScroll(scrollState)
                .padding(top = 16.dp)
                .offset { IntOffset(x = 0, y = -toolbarOffsetHeightPx.value.roundToInt()) }
        ) {
            state.days.forEach {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(40.dp),
                    backgroundColor = OnPrimaryHighEmphasis
                ) {
                    when (val currentState = it) {
                        is CardState.WorkStart -> WorkStartScreenContent(
                            state = currentState,
                            onClickStartWork = viewModel::onClickStartWork,
                            onClickDeleteEvent = viewModel::onClickDeleteEvent,
                            onAddPeriod = viewModel::onClickAddEvent,
                            onClickEvent = viewModel::onClickEvent
                        )

                        is CardState.Dinnering -> DinneringScreenContent(
                            state = currentState,
                            onClickReturnFromDinner = viewModel::onClickReturnFromDinner,
                            onClickDeleteEvent = viewModel::onClickDeleteEvent,
                            onClickEvent = viewModel::onClickEvent,
                            onAddPeriod = viewModel::onClickAddEvent,
                            onClickEndWork = viewModel::onClickFinishWork
                        )

                        is CardState.Working -> WorkingScreenContent(
                            state = currentState,
                            onClickStartPause = viewModel::onClickStartPause,
                            onClickGoToDinner = viewModel::onClickGoToDinner,
                            onClickDeleteEvent = viewModel::onClickDeleteEvent,
                            onClickEvent = viewModel::onClickEvent,
                            onAddPeriod = viewModel::onClickAddEvent,
                            onClickEndWork = viewModel::onClickFinishWork
                        )

                        is CardState.Pause -> PauseScreenContent(
                            state = currentState,
                            onClickEndPause = viewModel::onClickEndPause,
                            onClickDeleteEvent = viewModel::onClickDeleteEvent,
                            onClickEvent = viewModel::onClickEvent,
                            onAddPeriod = viewModel::onClickAddEvent,
                            onClickEndWork = viewModel::onClickFinishWork
                        )

                        is CardState.Results -> ResultsScreenContent(
                            state = currentState,
                            onClickStartWork = viewModel::onClickStartWork,
                            onClickDeleteEvent = viewModel::onClickDeleteEvent,
                            onClickEvent = viewModel::onClickEvent,
                            onAddPeriod = viewModel::onClickAddEvent
                        )
                    }
                }
            }
            Spacer(-with(LocalDensity.current) {
                toolbarOffsetHeightPx.value.toDp()
            })
        }
    }

}