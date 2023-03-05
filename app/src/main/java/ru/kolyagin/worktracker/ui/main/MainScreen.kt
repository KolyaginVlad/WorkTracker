package ru.kolyagin.worktracker.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import ru.kolyagin.worktracker.ui.destinations.SettingsScreenDestination
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme

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
    MainScreenContent(
        state = state,
        onSettingsClick = viewModel::onClickOpenSettings,
    )
}

@Composable
private fun MainScreenContent(
    state: MainScreenState,
    onSettingsClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopBar(title = state.title, onSettingsClick = onSettingsClick)
        }
    ) {
        Column(Modifier.padding(it)) {

        }
    }
}

@Composable
fun TopBar(
    title: String,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = { Text(text = title) },
        actions = {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                modifier = Modifier
                    .clickable(onClick = onSettingsClick)
                    .padding(horizontal = 16.dp)
            )
        },
    )
}

@Preview
@Composable
private fun MainPreview() {
    WorkTrackerTheme {
        MainScreenContent(state = MainScreenState("Best title"), { })
    }
}