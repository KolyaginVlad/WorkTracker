package ru.kolyagin.worktracker.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import ru.kolyagin.worktracker.ui.destinations.SettingsScreenDestination
import ru.kolyagin.worktracker.ui.main.content.DinneringScreenContent
import ru.kolyagin.worktracker.ui.main.content.PauseScreenContent
import ru.kolyagin.worktracker.ui.main.content.ResultsScreenContent
import ru.kolyagin.worktracker.ui.main.content.WorkEndScreenContent
import ru.kolyagin.worktracker.ui.main.content.WorkStartScreenContent
import ru.kolyagin.worktracker.ui.main.content.WorkingScreenContent
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
    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                is MainEvent.OpenSettings -> {
                    navigator.navigate(SettingsScreenDestination.route)
                }
            }
        }
    }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
    ) {
        val (topBar, content) = createRefs()
        TopBar(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
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
                .fillMaxHeight(0.7f)
                .padding(horizontal = 16.dp)
                .constrainAs(content) {
                    top.linkTo(topBar.bottom, margin = (-48).dp)
                },
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            state.days.forEach {
                Card(
                    modifier = Modifier
                        .fillMaxSize(),
                    backgroundColor = OnPrimaryHighEmphasis
                ) {
                    when (val currentState = it) {
                        is CardState.WorkStart -> WorkStartScreenContent(
                            currentState,
                            viewModel::onClickStartWork
                        )

                        is CardState.Dinnering -> DinneringScreenContent(
                            currentState,
                            viewModel::onClickReturnFromDinner
                        )

                        is CardState.Working -> WorkingScreenContent(
                            currentState,
                            viewModel::onClickStartPause,
                            viewModel::onClickGoToDinner
                        )

                        is CardState.Pause -> PauseScreenContent(
                            currentState,
                            viewModel::onClickEndPause
                        )

                        is CardState.WorkEnd -> WorkEndScreenContent(
                            currentState,
                            viewModel::onClickFinishWork
                        )

                        is CardState.Results -> ResultsScreenContent(currentState)
                        is CardState.Init -> { //TODO сделать экран заглушку
                        }
                    }
                }
            }
        }
    }

}