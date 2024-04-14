package ru.kolyagin.worktracker.ui.settings

import android.app.TimePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
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
import ru.kolyagin.worktracker.ui.settings.models.PeriodPart
import ru.kolyagin.worktracker.ui.settings.views.ListOfWorkDays
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import ru.kolyagin.worktracker.ui.utils.max
import ru.kolyagin.worktracker.ui.views.Spacer
import ru.kolyagin.worktracker.ui.views.TopBar
import java.time.DayOfWeek
import kotlin.math.roundToInt

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
        onDinnerChange = viewModel::onDinnerChange,
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
            onClickNotificationsSettings = {}
        )
    }
}