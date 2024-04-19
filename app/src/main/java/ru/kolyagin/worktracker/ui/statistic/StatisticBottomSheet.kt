package ru.kolyagin.worktracker.ui.statistic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.material.datepicker.MaterialDatePicker
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.ui.theme.PrimaryVariant
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import ru.kolyagin.worktracker.ui.utils.rememberFragmentManager
import ru.kolyagin.worktracker.ui.views.BottomSheetBaseContent
import ru.kolyagin.worktracker.ui.views.BottomSheetLabel
import ru.kolyagin.worktracker.ui.views.Spacer
import ru.kolyagin.worktracker.ui.views.WorkTimer
import ru.kolyagin.worktracker.utils.toLocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Destination(
    style = DestinationStyleBottomSheet::class,
)
@Composable
fun StatisticBottomSheet(
    viewModel: StatisticViewModel = hiltViewModel(),
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val fragmentManager = rememberFragmentManager()
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {

                is StatisticScreenEvent.ShowRangePicker -> {
                    MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText(R.string.select_date)
                        .setSelection(
                            androidx.core.util.Pair(
                                event.start,
                                event.end
                            )
                        )
                        .build().apply {
                            addOnPositiveButtonClickListener {
                                viewModel.onSelectRange(
                                    it.first.toLocalDate(),
                                    it.second.toLocalDate()
                                )
                            }
                            show(fragmentManager, "StatisticBottomSheet")
                        }

                }
            }
        }
    }

    Content(
        state = state,
        onShowDatePicker = viewModel::onShowDatePicker,
    )
}

private const val RANGE_FORMAT = "dd.MM.yy"

@Composable
private fun Content(
    state: StatisticScreenState,
    onShowDatePicker: () -> Unit,
) {
    val formatter = remember(Locale.getDefault()) {
        DateTimeFormatter.ofPattern(RANGE_FORMAT, Locale.getDefault())
    }
    BottomSheetBaseContent(state.isLoading) {
        Column {
            BottomSheetLabel()
            Spacer(size = 30.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(
                        R.string.statistic_for_date,
                        formatter.format(state.startDate),
                        formatter.format(state.endDate)
                    ),
                    style = MaterialTheme.typography.body1,
                    color = PrimaryVariant,
                    textAlign = TextAlign.Center,
                )
                IconButton(onClick = onShowDatePicker) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = PrimaryVariant
                    )
                }
            }
            Spacer(size = 36.dp)
            WorkTimer(
                time = state.workTime,
                title = stringResource(id = R.string.time_for_working),
                titleStyle = MaterialTheme.typography.body2,
            )
            Spacer(size = 52.dp)
            WorkTimer(
                time = state.plannedPauseTime,
                title = stringResource(id = R.string.time_for_plan_break),
                titleStyle = MaterialTheme.typography.body2,
            )
            Spacer(size = 52.dp)
            WorkTimer(
                time = state.unplannedPauseTime,
                title = stringResource(id = R.string.time_for_unplan_break),
                titleStyle = MaterialTheme.typography.body2,
            )
            Spacer(size = 52.dp)
        }
    }
}

@Preview
@Composable
private fun StatisticBottomSheetPreview() {
    WorkTrackerTheme {
        Content(state = StatisticScreenState(), onShowDatePicker = {})
    }
}