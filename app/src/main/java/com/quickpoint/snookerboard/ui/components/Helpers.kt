package com.quickpoint.snookerboard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.ui.theme.Beige
import com.quickpoint.snookerboard.ui.theme.spacing

@Composable
fun HorizontalDivider() = Divider(color = Beige)

@Composable
fun RowScope.RowHorizontalDivider() = Divider(
    modifier = Modifier
        .weight(1f)
        .padding(MaterialTheme.spacing.extraSmall),
    color = Beige, thickness = 1.dp
)


@Composable
fun VerticalDivider(spacing: Dp = 0.dp) = Row {
    Spacer(Modifier.width(spacing))
    Divider(
        Modifier
            .fillMaxHeight()
            .width(MaterialTheme.spacing.border), color = Beige
    )
    Spacer(Modifier.width(spacing))
}

@Composable
fun HorizontalDivider(spacing: Dp = 0.dp) = Column {
    Spacer(Modifier.width(spacing))
    Divider(
        Modifier
            .fillMaxWidth()
            .height(MaterialTheme.spacing.border), color = Beige
    )
    Spacer(Modifier.width(spacing))
}
