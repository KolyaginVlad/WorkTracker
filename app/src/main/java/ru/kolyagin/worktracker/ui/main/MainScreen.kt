package ru.kolyagin.worktracker.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import ru.kolyagin.worktracker.ui.destinations.SettingsScreenDestination
import ru.kolyagin.worktracker.ui.main.content.DinneringScreenContent
import ru.kolyagin.worktracker.ui.main.content.PauseScreenContent
import ru.kolyagin.worktracker.ui.main.content.ResultsScreenContent
import ru.kolyagin.worktracker.ui.main.content.WorkEndScreenContent
import ru.kolyagin.worktracker.ui.main.content.WorkStartScreenContent
import ru.kolyagin.worktracker.ui.main.content.WorkingScreenContent

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

    when (val currentState = state) {
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