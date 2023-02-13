package com.quickpoint.snookerboard.ui.fragments.rules

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.ui.components.TextSubtitle
import com.quickpoint.snookerboard.ui.theme.spacing

@Composable
fun RuleSelectionItem(
    title: String,
    content: @Composable RowScope.() -> Unit,
    contentIcon: (@Composable () -> Unit)? = null,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextSubtitle(title)
            Spacer(modifier = Modifier.width(16.dp))
            contentIcon?.let { contentIcon() }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
    }
}