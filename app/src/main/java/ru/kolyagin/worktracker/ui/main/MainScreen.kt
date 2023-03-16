package ru.kolyagin.worktracker.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.ui.destinations.SettingsScreenDestination
import ru.kolyagin.worktracker.ui.main.content.DinneringScreenContent
import ru.kolyagin.worktracker.ui.main.content.PauseScreenContent
import ru.kolyagin.worktracker.ui.main.content.ResultsScreenContent
import ru.kolyagin.worktracker.ui.main.content.WorkEndScreenContent
import ru.kolyagin.worktracker.ui.main.content.WorkNotStartedScreenContent
import ru.kolyagin.worktracker.ui.main.content.WorkStartScreenContent
import ru.kolyagin.worktracker.ui.main.content.WorkingScreenContent
import ru.kolyagin.worktracker.ui.main.views.TopBar

@RootNavGraph(start = true)
@Destination
@Composable
fun MainScreen(
    navigator: DestinationsNavigator,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                is MainEvent.OpenSettings -> {
                    navigator.navigate(SettingsScreenDestination.route)
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(id = getTitle(state)),
                onSettingsClick = viewModel::onClickOpenSettings
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (val currentState = state) {
                is MainScreenState.WorkNotStarted -> WorkNotStartedScreenContent(currentState)
                is MainScreenState.WorkStart -> WorkStartScreenContent(
                    currentState,
                    viewModel::onClickStartWork
                )

                is MainScreenState.Dinnering -> DinneringScreenContent(
                    currentState,
                    viewModel::onClickReturnFromDinner
                )

                is MainScreenState.Working -> WorkingScreenContent(
                    currentState,
                    viewModel::onClickStartPause,
                    viewModel::onClickGoToDinner
                )

                is MainScreenState.Pause -> PauseScreenContent(
                    currentState,
                    viewModel::onClickEndPause
                )

                is MainScreenState.WorkEnd -> WorkEndScreenContent(
                    currentState,
                    viewModel::onClickFinishWork
                )

                is MainScreenState.Results -> ResultsScreenContent(currentState)
                is MainScreenState.Init -> { //TODO сделать экран заглушку
                }
            }
        }
    }
}

fun getTitle(state: MainScreenState) = when (state) {
    is MainScreenState.WorkNotStarted -> R.string.work_not_started
    is MainScreenState.WorkStart -> R.string.work_start
    is MainScreenState.Dinnering -> R.string.dinner
    is MainScreenState.Working -> R.string.working
    is MainScreenState.Pause -> R.string.pause
    is MainScreenState.WorkEnd -> R.string.work_end
    is MainScreenState.Results -> R.string.result
    is MainScreenState.Init -> R.string.main_init
}