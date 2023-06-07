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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.kolyagin.worktracker.R
import ru.kolyagin.worktracker.ui.theme.OnPrimaryHighEmphasis
import ru.kolyagin.worktracker.ui.theme.WorkTrackerTheme

@Composable
fun TopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBackPressed: (() -> Unit)? = null,
    icons: @Composable RowScope.() -> Unit = {},
) {
    Box(
        modifier = modifier
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            painter = painterResource(id = R.drawable.header),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            contentDescription = null
        )
        onBackPressed?.let {
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp, start = 16.dp)
                    .clickable(onClick = it),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left_24px),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
                Text(
                    text = stringResource(id = R.string.back),
                    color = OnPrimaryHighEmphasis,
                    style = MaterialTheme.typography.overline
                )
            }
        }
        Text(
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp),
            text = title,
            style = MaterialTheme.typography.h5,
            color = OnPrimaryHighEmphasis
        )
        Row(modifier = Modifier.align(Alignment.TopEnd).padding(end = 16.dp, top = 16.dp)) {
            icons()
        }
    }
}

@Preview
@Composable
private fun TopBarPrev() {
    WorkTrackerTheme {
        TopBar("Рабочие периоды",
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f), {}) {
            Icon(
                painter = painterResource(id = R.drawable.settings),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.fillMaxSize())
    }
}