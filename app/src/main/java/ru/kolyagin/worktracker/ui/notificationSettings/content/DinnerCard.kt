package ru.kolyagin.worktracker.ui.notificationSettings.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.domain.models.Time
import ru.kolyagin.worktracker.ui.notificationSettings.views.LabelAndSwitch
import ru.kolyagin.worktracker.ui.notificationSettings.views.LabelAndTime
import ru.kolyagin.worktracker.ui.theme.OnPrimaryHighEmphasis

@Composable
fun DinnerCard(
    isEnable: Boolean,
    time: Time,
    onEnableChange: (Boolean) -> Unit,
    onTimeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(40.dp),
        backgroundColor = OnPrimaryHighEmphasis
    ) {
        Column(
            modifier = Modifier.padding(vertical = 40.dp, horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            LabelAndSwitch(
                modifier = modifier.fillMaxWidth(),
                label = stringResource(id = R.string.enable_dinner_notification),
                value = isEnable,
                onCheck = onEnableChange
            )
            Divider(Modifier.padding(horizontal = 12.dp))
            LabelAndTime(
                modifier = modifier.fillMaxWidth(),
                label = stringResource(id = R.string.time_dinner),
                time = time,
                onTimeClick = onTimeClick
            )
        }
    }
}