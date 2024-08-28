package ru.kolyagin.worktracker.ui.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.delay

@Composable
fun BottomSheetBaseContent(
    additionalLoadingPredicate : Boolean = false,
    content: @Composable () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { //Костыль для решения проблемы с открытием Bottom sheet
        delay(200)
        showContent = true
    }
    DimensionSubcomposeLayout(
        modifier = Modifier.fillMaxWidth(),
        placeMainContent = false,
        mainContent = {
            content()
        }
    ) { contentSize ->
        if (showContent && !additionalLoadingPredicate) {
            content()
        } else {
            Loading(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(LocalDensity.current) { contentSize.height.toDp() })
            )
        }
    }
}

@Composable
private fun Loading(
    modifier: Modifier
) {
    Box(
        modifier = modifier
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colors.primary
        )
    }
}