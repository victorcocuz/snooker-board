package com.quickpoint.snookerboard.ui.components

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.quickpoint.snookerboard.ui.theme.BrownDark
import com.quickpoint.snookerboard.ui.theme.White

@Composable
fun IconInfo(modifier: Modifier = Modifier) = IconDefault(
    modifier = modifier,
    imageVector = Icons.Default.Info,
    contentDescription = Icons.Default.Info.name,
    tint = White
)

@Composable
fun IconArrowBack() = Icon(
    imageVector = Icons.Default.ArrowBack,
    contentDescription = "Back",
)

@Composable
fun IconMenu() = IconDefault(
    imageVector = Icons.Default.Menu,
    contentDescription = "Toggle drawer",
)

@Composable
fun IconDefault(modifier: Modifier = Modifier, imageVector: ImageVector, contentDescription: String, tint: Color = BrownDark) = Icon(
    modifier = modifier,
    imageVector = imageVector,
    contentDescription = contentDescription,
    tint = tint
)