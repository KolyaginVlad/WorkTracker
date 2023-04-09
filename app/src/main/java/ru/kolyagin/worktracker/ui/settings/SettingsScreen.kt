package ru.kolyagin.worktracker.ui.settings

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
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
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import ru.kolyagin.worktracker.ui.views.Spacer
import ru.kolyagin.worktracker.ui.views.TopBar
import java.time.DayOfWeek

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
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        val (topBar, content) = createRefs()
        val scrollState = rememberScrollState()
        TopBar(
            title = stringResource(id = R.string.work_schedule),
            onBackPressed = { navigator.navigateUp() },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.25f)
                .constrainAs(topBar) {
                    top.linkTo(parent.top)
                },
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.80f)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(MaterialTheme.colors.background)
                .padding(top = 16.dp)
                .constrainAs(content) {
                    top.linkTo(topBar.bottom, margin = (-48).dp)
                },
        ) {
            ListOfWorkDays(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState),
                listOfWorkPeriods = state.listOfWorkPeriods,
                onClickPeriod = onClickPeriod,
                onDeletePeriod = onDeletePeriod,
                onAddPeriod = onAddPeriod,
                onDinnerChange = onDinnerChange,
                totalTime = state.totalTime
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