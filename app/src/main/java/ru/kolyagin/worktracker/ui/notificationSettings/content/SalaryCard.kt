package ru.kolyagin.worktracker.ui.notificationSettings.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.DaySalaryRate
import ru.kolyagin.worktracker.ui.notificationSettings.views.ListSalary
import ru.kolyagin.worktracker.ui.theme.OnPrimaryHighEmphasis
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import java.time.DayOfWeek

@Composable
fun SalaryCard(
    salary: ImmutableList<DaySalaryRate>,
    onSalaryAdd: (DayOfWeek, Long) -> Unit,
    onSetSalary: (DayOfWeek, Long) -> Unit,
    onDeleteSalary: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(40.dp),
        backgroundColor = OnPrimaryHighEmphasis
    ) {
        Column(
            modifier = Modifier.padding(vertical = 25.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = stringResource(id = R.string.hourly_rate),
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.primaryVariant,
                modifier = Modifier.align(CenterHorizontally)
            )
            ListSalary(
                modifier = Modifier,
                salaryRates = salary,
                onDeleteSalary = onDeleteSalary,
                onSetSalary = onSetSalary,
                onSalaryAdd = onSalaryAdd
            )
        }
    }
}

@Preview(locale = "ru")
@Composable
private fun SalaryCardPreview() {
    WorkTrackerTheme {
        SalaryCard(
            salary = persistentListOf(
                DaySalaryRate(1, DayOfWeek.MONDAY, 10),
                DaySalaryRate(2, DayOfWeek.WEDNESDAY, 2000)
            ),
            onDeleteSalary = { _ -> },
            onSalaryAdd = { _, _ -> },
            onSetSalary = { _, _ -> },
        )
    }
}