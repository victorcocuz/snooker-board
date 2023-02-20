package com.quickpoint.snookerboard.ui.components

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.ui.theme.GreenBright
import com.quickpoint.snookerboard.ui.theme.GreenBrighter
import com.quickpoint.snookerboard.ui.theme.spacing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch

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
            ),
        color = Color.Transparent
    ) {
        content()
    }
}

@Composable
fun FragmentContent(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(MaterialTheme.spacing.medium, 0.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.SpaceEvenly,
    withBottomSpacer: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) = Column(
    modifier = modifier
        .fillMaxSize()
        .padding(paddingValues),
    horizontalAlignment = horizontalAlignment,
    verticalArrangement = verticalArrangement,
) {
    content()
    if (withBottomSpacer) Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
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

@Composable
fun StandardRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceEvenly,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit,
) = Row(
    modifier = modifier,
    horizontalArrangement = horizontalArrangement,
    verticalAlignment = verticalAlignment
) { content() }

@Composable
fun VerticalGrid(
    modifier: Modifier = Modifier,
    columns: Int = 2,
    content: @Composable () -> Unit,
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val itemWidth = constraints.maxWidth / columns
        // Keep given height constraints, but set an exact width
        val itemConstraints = constraints.copy(
            minWidth = itemWidth,
            maxWidth = itemWidth
        )
        // Measure each item with these constraints
        val placeables = measurables.map { it.measure(itemConstraints) }
        // Track each columns height so we can calculate the overall height
        val columnHeights = Array(columns) { 0 }
        placeables.forEachIndexed { index, placeable ->
            val column = index % columns
            columnHeights[column] += placeable.height
        }
        val height = (columnHeights.maxOrNull() ?: constraints.minHeight)
            .coerceAtMost(constraints.maxHeight)
        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            // Track the Y co-ord per column we have placed up to
            val columnY = Array(columns) { 0 }
            placeables.forEachIndexed { index, placeable ->
                val column = index % columns
                placeable.placeRelative(
                    x = column * itemWidth,
                    y = columnY[column]
                )
                columnY[column] += placeable.height
            }
        }
    }
}

fun LazyListState.disableScrolling(scope: CoroutineScope) {
    scope.launch {
        scroll(scrollPriority = MutatePriority.PreventUserInput) {
            // Await indefinitely, blocking scrolls
            awaitCancellation()
        }
    }
}

@Composable
fun DialogCard(
    content: @Composable ColumnScope.() -> Unit,
) = ElevatedCard(
    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
    shape = RoundedCornerShape(8.dp),
    modifier = Modifier
        .fillMaxWidth(0.95f)
        .border(1.dp, color = Color.Red, shape = RoundedCornerShape(8.dp)),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)
) { content() }