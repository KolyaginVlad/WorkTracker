package ru.kolyagin.worktracker.ui.notificationSettings

import android.app.TimePickerDialog
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.NonDisposableHandle.parent
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.ui.notificationSettings.content.DinnerCard
import ru.kolyagin.worktracker.ui.notificationSettings.content.EndWorkCard
import ru.kolyagin.worktracker.ui.notificationSettings.content.MorningCard
import ru.kolyagin.worktracker.ui.notificationSettings.content.StartWorkCard
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import ru.kolyagin.worktracker.ui.views.Spacer
import ru.kolyagin.worktracker.ui.views.TopBar

@Destination
@Composable
fun NotificationSettingsScreen(
    navigator: DestinationsNavigator,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.screenState.collectAsStateWithLifecycle()
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
            }
        }
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