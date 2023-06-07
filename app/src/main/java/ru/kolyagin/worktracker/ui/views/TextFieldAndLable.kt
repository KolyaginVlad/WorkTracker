package ru.kolyagin.worktracker.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.kolyagin.worktracker.ui.theme.SurfaceHighEmphasis


@Composable
fun TextFieldAndLabel(
    modifier: Modifier,
    label: String,
    name: MutableState<String>,
    onValueChange:(String)->Unit
) {
    Column(modifier = Modifier) {
        TextField(colors = TextFieldDefaults.textFieldColors(
            textColor = SurfaceHighEmphasis,
            disabledTextColor = Color.Transparent,
            backgroundColor = MaterialTheme.colors.background,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
            textStyle = MaterialTheme.typography.subtitle1,
            shape = RoundedCornerShape(0.dp),
            value = name.value,
            modifier = modifier,
            onValueChange = {
                onValueChange(it)
            }
        )
        Text(
            text = label,
            modifier = Modifier.padding(start = 28.dp),
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.primaryVariant,
        )
    }
}
