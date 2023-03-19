package ru.kolyagin.worktracker.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.ui.theme.White
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme

@Composable
fun TopBar(
    title: String,
    onBackPressed: (() -> Unit)? = null,
    icons: @Composable RowScope.() -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f)
            .clip(shape = RoundedCornerShape(bottomEnd = 40.dp, bottomStart = 40.dp))
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter),
            painter = painterResource(id = R.drawable.header),
            contentScale = ContentScale.FillHeight,
            contentDescription = null
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            onBackPressed?.let {
                Row(
                    modifier = Modifier
                        .clickable(onClick = it)
                        .align(Alignment.TopStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_left_24px),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                    Text(
                        text = stringResource(id = R.string.back),
                        color = White,
                        fontSize = 10.sp,
                        letterSpacing = 3.sp
                    )
                }
            }
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = title,
                color = White,
                fontSize = 24.sp
            )
            Row(modifier = Modifier.align(Alignment.TopEnd)) {
                icons()
            }
        }
    }
}

@Preview
@Composable
private fun TopBarPrev() {
    WorkTrackerTheme {
        TopBar("Рабочие периоды", {}) {
            Icon(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.fillMaxSize())
    }
}