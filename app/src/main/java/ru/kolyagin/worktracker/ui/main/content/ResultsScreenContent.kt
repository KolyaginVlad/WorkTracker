package ru.kolyagin.worktracker.ui.main.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.kolyagin.worktracker.ui.main.CardState

@Composable
fun ResultsScreenContent(
    state: CardState.Results,
) {
    Column(modifier = Modifier.padding(bottom = 12.dp))  {
        Text(text = state.statistic.toString())
    }
}