package com.quickpoint.snookerboard.ui.styles

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.ui.theme.GreenBright
import com.quickpoint.snookerboard.ui.theme.GreenBrighter
import com.quickpoint.snookerboard.ui.theme.spacing

@Composable
fun GenericSurface(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GreenBright, GreenBrighter
                    )
                )
            ), color = Color.Transparent
    ) {
        content()
    }
}

@Composable
fun FragmentColumn(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) = Column(
    modifier = modifier
        .fillMaxSize()
        .padding(MaterialTheme.spacing.medium, 0.dp)
) {
    content()
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
}