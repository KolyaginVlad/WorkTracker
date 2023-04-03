package ru.kolyagin.worktracker.ui.main

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
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
import ru.kolyagin.worktracker.ui.views.TopBar

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
        viewModel.init()
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
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        val (topBar, content) = createRefs()
        val scrollState = rememberScrollState()
        TopBar(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.25f)
                .constrainAs(topBar) {
                    top.linkTo(parent.top)
                },
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
                .fillMaxWidth()
                .fillMaxHeight(0.80f)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(MaterialTheme.colors.background)
                .padding(top = 16.dp)
                .verticalScroll(scrollState)
                .constrainAs(content) {
                    top.linkTo(topBar.bottom, margin = (-48).dp)
                },
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            state.days.forEach {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
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
                            onClickEndWork = viewModel::onClickFinishWork
                        )

                        is CardState.Working -> WorkingScreenContent(
                            state = currentState,
                            onClickStartPause = viewModel::onClickStartPause,
                            onClickGoToDinner = viewModel::onClickGoToDinner,
                            onClickDeleteEvent = viewModel::onClickDeleteEvent,
                            onClickEvent = viewModel::onClickEvent,
                            onClickEndWork = viewModel::onClickFinishWork
                        )

                        is CardState.Pause -> PauseScreenContent(
                            state = currentState,
                            onClickEndPause = viewModel::onClickEndPause,
                            onClickDeleteEvent = viewModel::onClickDeleteEvent,
                            onClickEvent = viewModel::onClickEvent,
                            onClickEndWork = viewModel::onClickFinishWork
                        )

                        is CardState.Results -> ResultsScreenContent(
                            state = currentState,
                            onClickDeleteEvent = viewModel::onClickDeleteEvent,
                            onClickEvent = viewModel::onClickEvent,
                            onAddPeriod = viewModel::onClickAddEvent
                        )
                    }
                }
            }
        }
    }

}