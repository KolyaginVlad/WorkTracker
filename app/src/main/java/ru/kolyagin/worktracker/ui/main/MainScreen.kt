package ru.kolyagin.worktracker.ui.main

import android.app.TimePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.ui.destinations.SettingsScreenDestination
import ru.kolyagin.worktracker.ui.destinations.StatisticBottomSheetDestination
import ru.kolyagin.worktracker.ui.main.content.DinneringScreenContent
import ru.kolyagin.worktracker.ui.main.content.PauseScreenContent
import ru.kolyagin.worktracker.ui.main.content.ResultsScreenContent
import ru.kolyagin.worktracker.ui.main.content.WorkStartScreenContent
import ru.kolyagin.worktracker.ui.main.content.WorkingScreenContent
import ru.kolyagin.worktracker.ui.settings.models.PeriodPart
import ru.kolyagin.worktracker.ui.theme.OnPrimaryHighEmphasis
import ru.kolyagin.worktracker.ui.utils.BaseMaterialTimePickerBuilder
import ru.kolyagin.worktracker.ui.utils.rememberFragmentManager
import ru.kolyagin.worktracker.ui.views.Spacer
import ru.kolyagin.worktracker.ui.views.TopBar

@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(
    navigator: DestinationsNavigator,
    viewModel: MainViewModel = hiltViewModel()
) {
    val fragmentManager = rememberFragmentManager()
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is MainEvent.OpenSettings -> {
                    navigator.navigate(SettingsScreenDestination.route)
                }

                is MainEvent.AddEventTime -> {
                   BaseMaterialTimePickerBuilder
                        .setHour(12)
                        .setMinute(0)
                        .build().apply {
                            addOnPositiveButtonClickListener {
                                viewModel.onTimePicked(Time(hour, minute), PeriodPart.START)
                                BaseMaterialTimePickerBuilder
                                    .setHour(14)
                                    .setMinute(0)
                                    .build().apply {
                                        addOnPositiveButtonClickListener {
                                            viewModel.onTimePicked(Time(hour, minute), PeriodPart.END)
                                        }
                                        show(fragmentManager, "MainScreen")
                                    }
                            }
                            show(fragmentManager, "MainScreen")
                        }
                }

                is MainEvent.ChangeEventTime -> {
                    BaseMaterialTimePickerBuilder
                        .setHour(event.timeStart.hours)
                        .setMinute(event.timeStart.minutes)
                        .build().apply {
                            addOnPositiveButtonClickListener {
                                viewModel.onTimeChanging(Time(hour, minute), PeriodPart.START)
                                BaseMaterialTimePickerBuilder
                                    .setHour(event.timeEnd.hours)
                                    .setMinute(event.timeEnd.minutes)
                                    .build().apply {
                                        addOnPositiveButtonClickListener {
                                            viewModel.onTimeChanging(Time(hour, minute), PeriodPart.END)
                                        }
                                        show(fragmentManager, "MainScreen")
                                    }
                            }
                            show(fragmentManager, "MainScreen")
                        }
                }
            }
        }
    }
    val toolbarHeight = 100.dp
    val goToStatistic = remember {
        {
            navigator.navigate(StatisticBottomSheetDestination) {
                launchSingleTop = true
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        val scrollState = rememberScrollState()
        Image(
            modifier = Modifier
                .fillMaxSize(),
            painter = painterResource(id = R.drawable.header),
            contentScale = ContentScale.FillBounds,
            contentDescription = null
        )
        Column(Modifier.verticalScroll(scrollState)) {
            TopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(toolbarHeight),
                title = stringResource(id = R.string.work_periods)
            ) {
                IconButton(
                    onClick = goToStatistic
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = OnPrimaryHighEmphasis
                    )
                }

                IconButton(onClick = viewModel::onClickOpenSettings) {
                    Icon(
                        painter = painterResource(id = R.drawable.settings),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }
            }
            Spacer(size = 40.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 56.dp, topEnd = 56.dp))
                    .background(MaterialTheme.colors.background)
                    .padding(top = 16.dp)
            ) {
                state.days.forEach {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                        shape = RoundedCornerShape(40.dp),
                        backgroundColor = OnPrimaryHighEmphasis,
                        elevation = 0.dp
                    ) {
                        when (val currentState = it) {
                            is CardState.WorkStart -> WorkStartScreenContent(
                                state = currentState,
                                onClickStartWork = viewModel::onClickStartWork,
                                onClickDeleteEvent = viewModel::onClickDeleteEvent,
                                onAddPeriod = viewModel::onClickAddEvent,
                                onClickEvent = viewModel::onClickEvent,
                                onClickShowMore = goToStatistic,
                            )

                            is CardState.Dinnering -> DinneringScreenContent(
                                state = currentState,
                                onClickReturnFromDinner = viewModel::onClickReturnFromDinner,
                                onClickDeleteEvent = viewModel::onClickDeleteEvent,
                                onClickEvent = viewModel::onClickEvent,
                                onAddPeriod = viewModel::onClickAddEvent,
                                onClickEndWork = viewModel::onClickFinishWork,
                                onClickShowMore = goToStatistic,
                            )

                            is CardState.Working -> WorkingScreenContent(
                                state = currentState,
                                onClickStartPause = viewModel::onClickStartPause,
                                onClickGoToDinner = viewModel::onClickGoToDinner,
                                onClickDeleteEvent = viewModel::onClickDeleteEvent,
                                onClickEvent = viewModel::onClickEvent,
                                onAddPeriod = viewModel::onClickAddEvent,
                                onClickEndWork = viewModel::onClickFinishWork,
                                onClickShowMore = goToStatistic,
                            )

                            is CardState.Pause -> PauseScreenContent(
                                state = currentState,
                                onClickEndPause = viewModel::onClickEndPause,
                                onClickDeleteEvent = viewModel::onClickDeleteEvent,
                                onClickEvent = viewModel::onClickEvent,
                                onAddPeriod = viewModel::onClickAddEvent,
                                onClickEndWork = viewModel::onClickFinishWork,
                                onClickShowMore = goToStatistic,
                            )

                            is CardState.Results -> ResultsScreenContent(
                                state = currentState,
                                onClickStartWork = viewModel::onClickStartWork,
                                onClickDeleteEvent = viewModel::onClickDeleteEvent,
                                onClickEvent = viewModel::onClickEvent,
                                onAddPeriod = viewModel::onClickAddEvent,
                                onClickShowMore = goToStatistic,
                            )
                        }
                    }
                }
            }
        }
    }

}