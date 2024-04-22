package ru.kolyagin.worktracker.ui.notificationSettings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.ui.notificationSettings.content.DinnerCard
import ru.kolyagin.worktracker.ui.notificationSettings.content.EndWorkCard
import ru.kolyagin.worktracker.ui.notificationSettings.content.MorningCard
import ru.kolyagin.worktracker.ui.notificationSettings.content.StartWorkCard
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import ru.kolyagin.worktracker.ui.utils.BaseMaterialTimePickerBuilder
import ru.kolyagin.worktracker.ui.utils.rememberFragmentManager
import ru.kolyagin.worktracker.ui.views.PickerDialog
import ru.kolyagin.worktracker.ui.views.Spacer
import ru.kolyagin.worktracker.ui.views.TopBar

@Destination
@Composable
fun NotificationSettingsScreen(
    navigator: DestinationsNavigator,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val fragmentManager = rememberFragmentManager()
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is NotificationSettingsEvent.ShowTimePicker -> {
                   BaseMaterialTimePickerBuilder
                        .setHour(event.time.hours)
                        .setMinute(event.time.minutes)
                        .build().apply {
                            addOnPositiveButtonClickListener {
                                event.onTimePick(Time(hour, minute))
                            }
                            show(fragmentManager, "NotificationSettingsScreen")
                        }

                }
            }
        }
    }
    if (state.morningOffsetDialogVisible) {
        PickerDialog(
            time = state.morningOffset,
            onTimePicked = viewModel::onMorningOffsetPicked,
            onCancel = viewModel::onMorningOffsetClose,
            max = remember {
                Time(6, 0)
            },
            title = stringResource(id = R.string.morning_offset)
        )
    }
    if (state.startWorkOffsetDialogVisible) {
        PickerDialog(
            time = state.startWorkOffset,
            onTimePicked = viewModel::onStartWorkOffsetPicked,
            onCancel = viewModel::onStartWorkOffsetClose,
            max = remember {
                Time(6, 0)
            },
            title = stringResource(id = R.string.start_offset)
        )
    }
    if (state.endWorkOffsetDialogVisible) {
        PickerDialog(
            time = state.endWorkOffset,
            onTimePicked = viewModel::onEndWorkOffsetPicked,
            onCancel = viewModel::onEndWorkOffsetClose,
            max = remember {
                Time(6, 0)
            },
            title = stringResource(id = R.string.end_offset)
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
) {
    val toolbarHeight = 100.dp
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize(),
            painter = painterResource(id = R.drawable.header),
            contentScale = ContentScale.FillBounds,
            contentDescription = null
        )
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
        ) {
            TopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(toolbarHeight),
                title = stringResource(id = R.string.notification_settings),
                onBackPressed = navigator::navigateUp
            )
            Spacer(size = 40.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 56.dp, topEnd = 56.dp))
                    .background(MaterialTheme.colors.background)
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    offset = state.endWorkOffset,
                    isEnable = state.isEndWorkNotificationEnable,
                    onOffsetClick = onEndWorkOffsetClick,
                    onEnableChange = onEndWorkNotificationEnableChange
                )
            }

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
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
        )
    }
}