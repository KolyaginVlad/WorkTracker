package ru.kolyagin.worktracker.ui.main.content

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.ui.main.MainScreenState

@Composable
fun BoxScope.WorkNotStartedScreenContent(
    currentState: MainScreenState.WorkNotStarted,
) {
    Text(
        modifier = Modifier.align(Alignment.Center),
        text = stringResource(id = R.string.main_init)
    )
}