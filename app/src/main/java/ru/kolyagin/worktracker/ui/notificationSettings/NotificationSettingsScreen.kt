package ru.kolyagin.worktracker.ui.notificationSettings

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import ru.kolyagin.worktracker.ui.notificationSettings.content.DinnerCard
import ru.kolyagin.worktracker.ui.notificationSettings.content.EndWorkCard
import ru.kolyagin.worktracker.ui.notificationSettings.content.MorningCard
import ru.kolyagin.worktracker.ui.notificationSettings.content.SalaryCard
import ru.kolyagin.worktracker.ui.notificationSettings.content.StartWorkCard
import ru.kolyagin.worktracker.ui.notificationSettings.views.CustomAddDialog
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import ru.kolyagin.worktracker.ui.views.TopBar
import java.time.DayOfWeek

@Destination
@Composable
fun NotificationSettingsScreen(
    navigator: DestinationsNavigator,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val openAddDialog = remember { mutableStateOf(false) }
    val openEditDialog = remember { mutableStateOf(false) }
    var dayStart by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                is NotificationSettingsEvent.ShowTimePicker -> {
                    TimePickerDialog(
                        context,
                        { _, hour: Int, minute: Int ->
                            it.onTimePick(Time(hour, minute))
                        }, it.time.hours, it.time.minutes, false
                    ).show()
                }

                is NotificationSettingsEvent.AddSalary -> {
                    openAddDialog.value = true
                }

                is NotificationSettingsEvent.SetSalary -> {
                    openEditDialog.value = true
                    dayStart = it.day.ordinal
                }
            }
        }
    }
    if (openAddDialog.value) {
        CustomAddDialog(onSubmit = viewModel::addSalary, openDialogCustom = openAddDialog)
    }
    if (openEditDialog.value) {
        CustomAddDialog(
            onSubmit = viewModel::setSalary,
            openDialogCustom = openEditDialog,
            daystart = dayStart,
            showDaySelector = false
        )
    }
    NotificationSettingsScreenContent(
        navigator = navigator,
        state = state,
        onMorningNotificationEnableChange = viewModel::onMorningNotificationEnableChange,
        onMorningStartTimeClick = viewModel::onMorningStartTimeClick,
        onMorningEndTimeClick = viewModel::onMorningEndTimeClick,
        onMorningOffsetClick = viewModel::onMorningOffsetClick,
        onDinnerNotificationEnableChange = viewModel::onDinnerNotificationEnableChange,
        onDinnerTimeClick = viewModel::onDinnerTimeClick,
        onStartWorkNotificationEnableChange = viewModel::onStartWorkNotificationEnableChange,
        onStartWorkOffsetClick = viewModel::onStartWorkOffsetClick,
        onEndWorkNotificationEnableChange = viewModel::onEndWorkNotificationEnableChange,
        onEndWorkOffsetClick = viewModel::onEndWorkOffsetClick,
        onSalaryAdd = viewModel::onAddSalary,
        onSetSalary = viewModel::onSetSalary,
        onDeleteSalary = viewModel::onDeleteSalary
    )
}

@Composable
private fun NotificationSettingsScreenContent(
    navigator: DestinationsNavigator,
    state: NotificationSettingsScreenState,
    onMorningNotificationEnableChange: (Boolean) -> Unit,
    onMorningStartTimeClick: () -> Unit,
    onMorningEndTimeClick: () -> Unit,
    onMorningOffsetClick: () -> Unit,
    onDinnerNotificationEnableChange: (Boolean) -> Unit,
    onDinnerTimeClick: () -> Unit,
    onStartWorkNotificationEnableChange: (Boolean) -> Unit,
    onStartWorkOffsetClick: () -> Unit,
    onEndWorkNotificationEnableChange: (Boolean) -> Unit,
    onEndWorkOffsetClick: () -> Unit,
    onSalaryAdd: () -> Unit,
    onSetSalary: (DayOfWeek) -> Unit,
    onDeleteSalary: (Long) -> Unit,
) {
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
            title = stringResource(id = R.string.notification_settings),
            onBackPressed = navigator::navigateUp
        )
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
            SalaryCard(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                salary = state.salaryRates,
                onSalaryAdd = onSalaryAdd,
                onSetSalary = onSetSalary,
                onDeleteSalary = onDeleteSalary
            )
            MorningCard(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                isEnable = state.isMorningNotificationEnable,
                startTime = state.morningNotificationRange.start,
                endTime = state.morningNotificationRange.endInclusive,
                offset = state.morningOffset,
                onEnableChange = onMorningNotificationEnableChange,
                onStartTimeClick = onMorningStartTimeClick,
                onEndTimeClick = onMorningEndTimeClick,
                onOffsetClick = onMorningOffsetClick,
            )
            DinnerCard(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                time = state.dinnerTime,
                isEnable = state.isDinnerNotificationEnable,
                onTimeClick = onDinnerTimeClick,
                onEnableChange = onDinnerNotificationEnableChange
            )

            StartWorkCard(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                offset = state.startWorkOffset,
                isEnable = state.isStartWorkNotificationEnable,
                onOffsetClick = onStartWorkOffsetClick,
                onEnableChange = onStartWorkNotificationEnableChange
            )

            EndWorkCard(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                offset = state.endWorkOffset,
                isEnable = state.isEndWorkNotificationEnable,
                onOffsetClick = onEndWorkOffsetClick,
                onEnableChange = onEndWorkNotificationEnableChange
            )
        }
    }
}

@Preview
@Composable
private fun NotificationSettingsPreview() {
    WorkTrackerTheme {
        NotificationSettingsScreenContent(
            navigator = EmptyDestinationsNavigator,
            state = NotificationSettingsScreenState(),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, { }, { }, { _ -> }
        )
    }
}