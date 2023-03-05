package ru.kolyagin.worktracker.ui.settings

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.domain.models.WorkPeriod
import ru.kolyagin.worktracker.ui.settings.models.PeriodPart
import ru.kolyagin.worktracker.ui.settings.views.ListOfWorkDays
import ru.kolyagin.worktracker.ui.settings.views.TotalScheduleInfo
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import ru.kolyagin.worktracker.ui.views.Spacer
import ru.kolyagin.worktracker.utils.models.DayOfWeek

@Destination
@Composable
fun SettingsScreen(
    navigator: DestinationsNavigator,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                is SettingsEvent.ShowTimePicker -> {
                    TimePickerDialog(
                        context,
                        { _, hour: Int, minute: Int ->
                            viewModel.onTimePicked(Time(hour, minute))
                        }, it.time.hours, it.time.minutes, true
                    ).show()
                }
            }
        }
    }
    SettingsScreenContent(
        navigator = navigator,
        state = state,
        onClickPeriod = viewModel::onClickPeriod,
        onDeletePeriod = viewModel::onDeletePeriod,
        onAddPeriod = viewModel::onAddPeriod,
        onDinnerChange = viewModel::onDinnerChange
    )
}

@Composable
private fun SettingsScreenContent(
    navigator: DestinationsNavigator,
    state: SettingsScreenState,
    onClickPeriod: (DayOfWeek, WorkPeriod, PeriodPart) -> Unit,
    onDeletePeriod: (WorkPeriod) -> Unit,
    onAddPeriod: (DayOfWeek) -> Unit,
    onDinnerChange: (DayOfWeek, Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Icon(
                        modifier = Modifier.clickable(onClick = remember {
                            {
                                navigator.navigateUp()
                            }
                        }),
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                },
                title = {
                    Text(text = stringResource(id = R.string.work_schedule))
                }
            )
        }
    ) {
        Column(
            Modifier
                .padding(it)
        ) {
            TotalScheduleInfo(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                totalTime = state.totalTime
            )
            ListOfWorkDays(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                listOfWorkPeriods = state.listOfWorkPeriods,
                onClickPeriod = onClickPeriod,
                onDeletePeriod = onDeletePeriod,
                onAddPeriod = onAddPeriod,
                onDinnerChange = onDinnerChange
            )
            Spacer(size = 8.dp)
        }
    }
}

@Preview
@Composable
private fun SettingsPreview() {
    WorkTrackerTheme {
        SettingsScreenContent(
            navigator = EmptyDestinationsNavigator,
            state = SettingsScreenState(),
            onClickPeriod = { _, _, _ -> },
            onDeletePeriod = { _ -> },
            onAddPeriod = { },
            onDinnerChange = { _, _ -> }
        )
    }
}