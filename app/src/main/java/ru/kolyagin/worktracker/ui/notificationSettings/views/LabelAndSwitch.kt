package ru.kolyagin.worktracker.ui.notificationSettings.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme
import ru.kolyagin.worktracker.ui.views.CustomSwitch

@Composable
fun LabelAndSwitch(
    label: String,
    value: Boolean,
    onCheck: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle =  MaterialTheme.typography.body2,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(0.7f),
            text = label,
            style = style,
            color = MaterialTheme.colors.primary
        )
        Box(modifier = Modifier.weight(0.3f), contentAlignment = Alignment.CenterEnd) {
            CustomSwitch(
                modifier = Modifier,
                checked = value,
                onCheckedChange = onCheck
            )
        }
    }
}

@Preview
@Composable
private fun LabelAndSwitchPrev() {
    WorkTrackerTheme {
        LabelAndSwitch(
            label = "123",
            value = true,
            onCheck = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}