package ru.kolyagin.worktracker.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import ru.kolyagin.worktracker.domain.models.TimeWithSeconds
import ru.kolyagin.worktracker.ui.theme.PrimaryVariantDisabled

@Composable
fun WorkTimer(
    time: TimeWithSeconds?,
    title: String,
    primaryColor: Color = MaterialTheme.colors.primaryVariant,
    disableColor: Color = PrimaryVariantDisabled,
    titleStyle: TextStyle = MaterialTheme.typography.h5,
) {
    time?.let {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = titleStyle,
                color = primaryColor
            )
            Timer(
                time = it,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 40.dp, top = 8.dp, end = 40.dp, bottom = 24.dp)
                    .fillMaxWidth(),
                primaryColor = primaryColor,
                disableColor = disableColor,
            )
        }
    }
}