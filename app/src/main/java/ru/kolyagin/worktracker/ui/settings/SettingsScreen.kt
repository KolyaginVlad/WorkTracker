package ru.kolyagin.worktracker.ui.settings

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
    val toolbarHeight = 200.dp
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
    val toolbarOffsetHeightPx = remember { mutableStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = toolbarOffsetHeightPx.value + delta
                toolbarOffsetHeightPx.value = newOffset.coerceIn(-toolbarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .nestedScroll(nestedScrollConnection)
    ) {
        val scrollState = rememberScrollState()
        TopBar(
            title = stringResource(id = R.string.work_schedule),
            onBackPressed = navigator::navigateUp,
            modifier = Modifier
                .fillMaxWidth()
                .height(toolbarHeight)
                .offset { IntOffset(x = 0, y = toolbarOffsetHeightPx.value.roundToInt()) },
        )
        Column(
            modifier = Modifier
                .padding(
                    top = max(
                        toolbarHeight - 48.dp + with(LocalDensity.current) {
                            toolbarOffsetHeightPx.value.toDp()
                        },
                        0.dp
                    )
                )
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(MaterialTheme.colors.background)
                .verticalScroll(scrollState)
                .padding(top = 16.dp)
                .offset { IntOffset(x = 0, y = -toolbarOffsetHeightPx.value.roundToInt()) },
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
            Spacer(-with(LocalDensity.current) {
                toolbarOffsetHeightPx.value.toDp()
            })
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