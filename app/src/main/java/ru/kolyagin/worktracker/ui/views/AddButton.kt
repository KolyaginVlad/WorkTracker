package ru.kolyagin.worktracker.ui.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.ui.theme.RoundedButtonShapes

@Composable
fun AddButton(
    day: Int,
    modifier: Modifier,
    contentColor: Color = MaterialTheme.colors.primary,
    backgroundColor: Color = MaterialTheme.colors.onPrimary,
    onAddPeriod: (Int) -> Unit,
    height: Dp = 18.dp,
    width: Dp = 16.dp,
) {
    OutlinedButton(
        border = BorderStroke(2.dp, contentColor),
        modifier = modifier,
        shape = RoundedButtonShapes.medium,
        onClick = remember(day) { { onAddPeriod(day) } },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Icon(
            modifier = Modifier.padding(horizontal = width, vertical = height),
            painter = painterResource(id = R.drawable.plus),
            contentDescription = null,
            tint = contentColor
        )
    }
}
