package com.quickpoint.snookerboard.compose.ui.styles

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.compose.ui.theme.GreenBright
import com.quickpoint.snookerboard.compose.ui.theme.GreenBrighter
import com.quickpoint.snookerboard.compose.ui.theme.spacing

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
        .padding(MaterialTheme.spacing.medium, 0.dp, MaterialTheme.spacing.medium, MaterialTheme.spacing.large)
) {
    content()
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
}
@Composable
fun DialogFragmentColumn(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) = Column(
    modifier = modifier
        .fillMaxSize(0.8f)
        .padding(MaterialTheme.spacing.medium, 0.dp, MaterialTheme.spacing.medium, MaterialTheme.spacing.large)
) {
    content()
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
}

@Composable
fun RuleSelectionItem(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
    contentIcon: (@Composable () -> Unit)? = null,
    onIconClick: (() -> Unit)? = null
) {
    Column {
        Row(
            modifier = modifier.fillMaxWidth()
            .clickable { onIconClick?.let { onIconClick() } },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically) {
            TextNavParagraphSubTitle(title)
            Spacer(modifier = Modifier.width(16.dp))
            contentIcon?.let { contentIcon() }
        }
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
    }
}

@Composable
fun DefaultSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = { },
) {
    SnackbarHost(
        hostState = snackbarHostState, snackbar = { data ->
            Snackbar(modifier = Modifier.padding(16.dp), content = {
                androidx.compose.material3.Text(
                    text = data.message, style = MaterialTheme.typography.bodyMedium
                )
            }, action = {
                data.actionLabel?.let { actionLabel ->
                    TextButton(onClick = onDismiss) {
                        androidx.compose.material3.Text(
                            text = actionLabel, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            })
        }, modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Bottom)
    )
}
