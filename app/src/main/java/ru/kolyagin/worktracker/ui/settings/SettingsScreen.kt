package ru.kolyagin.worktracker.ui.settings

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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import ru.kolyagin.worktracker.ui.destinations.NotificationSettingsScreenDestination
import ru.kolyagin.worktracker.ui.main.views.EventsTimePickerDialog
import ru.kolyagin.worktracker.ui.notificationSettings.content.SalaryCard
import ru.kolyagin.worktracker.ui.notificationSettings.views.CustomAddDialog
import ru.kolyagin.worktracker.ui.settings.models.PeriodPart
import ru.kolyagin.worktracker.ui.settings.views.ListOfWorkDays
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import ru.kolyagin.worktracker.ui.utils.BaseMaterialTimePickerBuilder
import ru.kolyagin.worktracker.ui.utils.rememberFragmentManager
import ru.kolyagin.worktracker.ui.views.Spacer
import ru.kolyagin.worktracker.ui.views.TopBar
import java.time.DayOfWeek

@Destination
@Composable
fun SettingsScreen(
    navigator: DestinationsNavigator,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val openAddDialog = remember { mutableStateOf(false) }
    val openEditDialog = remember { mutableStateOf(false) }
    var dayStart by remember { mutableStateOf(0) }
    val openTimeEditDialog = remember {
        mutableStateOf(false)
    }
    var selectedEditTime by remember {
        mutableStateOf(Time(0,0))
    }
    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                is SettingsEvent.ShowTimePicker -> {
                    openTimeEditDialog.value = true
                    selectedEditTime = it.time
                }

                is SettingsEvent.AddSalary -> {
                    openAddDialog.value = true
                }

                is SettingsEvent.SetSalary -> {
                    openEditDialog.value = true
                    dayStart = it.day.ordinal
                }
            }
        }
    }
    if (openTimeEditDialog.value) {
        EventsTimePickerDialog(
            onSubmit = remember {
                { _, time, _, _ ->
                    viewModel.onTimePicked(time)
                }
            },
            openDialogCustom = openTimeEditDialog,
            time = selectedEditTime,
            period = PeriodPart.START,
            onlyTime = true,
            title = stringResource(id = R.string.select_time)
        )
    }
    if (openAddDialog.value) {
        CustomAddDialog(
            onSubmit = viewModel::addSalary,
            openDialogCustom = openAddDialog
        )
    }
    if (openEditDialog.value) {
        CustomAddDialog(
            onSubmit = viewModel::setSalary,
            openDialogCustom = openEditDialog,
            daystart = dayStart,
            showDaySelector = false
        )
    }
    SettingsScreenContent(
        navigator = navigator,
        state = state,
        onClickPeriod = viewModel::onClickPeriod,
        onDeletePeriod = viewModel::onDeletePeriod,
        onAddPeriod = viewModel::onAddPeriod,
        onDinnerChange = viewModel::onDinnerChange,
        onSalaryAdd = viewModel::onAddSalary,
        onSetSalary = viewModel::onSetSalary,
        onDeleteSalary = viewModel::onDeleteSalary,
        onClickNotificationsSettings = remember {
            {
                navigator.navigate(NotificationSettingsScreenDestination) {
                    launchSingleTop = true
                }
            }
        }
    )
}

@Composable
private fun SettingsScreenContent(
    navigator: DestinationsNavigator,
    state: SettingsScreenState,
    onSalaryAdd: () -> Unit,
    onSetSalary: (DayOfWeek) -> Unit,
    onDeleteSalary: (Long) -> Unit,
    onClickPeriod: (DayOfWeek, WorkPeriod, PeriodPart) -> Unit,
    onDeletePeriod: (WorkPeriod) -> Unit,
    onAddPeriod: (DayOfWeek) -> Unit,
    onDinnerChange: (DayOfWeek, Boolean) -> Unit,
    onClickNotificationsSettings: () -> Unit
) {
    val toolbarHeight = 100.dp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
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
                title = stringResource(id = R.string.work_schedule),
                onBackPressed = navigator::navigateUp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(toolbarHeight),
                icons = {
                    IconButton(onClick = onClickNotificationsSettings) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                }
            )
            Spacer(size = 40.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 56.dp, topEnd = 56.dp))
                    .background(MaterialTheme.colors.background)
                    .padding(top = 16.dp),
            ) {
                SalaryCard(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    salary = state.salaryRates,
                    onSalaryAdd = onSalaryAdd,
                    onSetSalary = onSetSalary,
                    onDeleteSalary = onDeleteSalary
                )
                Spacer(size = 12.dp)
                ListOfWorkDays(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    listOfWorkPeriods = state.listOfWorkPeriods,
                    onClickPeriod = onClickPeriod,
                    onDeletePeriod = onDeletePeriod,
                    onAddPeriod = onAddPeriod,
                    onDinnerChange = onDinnerChange,
                    totalTime = state.totalTime
                )
                Spacer(size = 12.dp)
            }
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
            onDinnerChange = { _, _ -> },
            onClickNotificationsSettings = {},
            onSetSalary =  { },
            onDeleteSalary =  { },
            onSalaryAdd = { }
        )
    }
}