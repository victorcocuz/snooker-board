package com.quickpoint.snookerboard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.ui.theme.spacing

@Composable
fun FragmentContent(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(MaterialTheme.spacing.medium, 0.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalArrangement: Arrangement.Vertical = Arrangement.SpaceBetween,
    showBottomSpacer: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) = Column(
    modifier = modifier
        .fillMaxSize()
        .padding(paddingValues),
    horizontalAlignment = horizontalAlignment,
    verticalArrangement = verticalArrangement,
) {
    content()
    if (showBottomSpacer) Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
}

@Composable
fun FragmentExtras(content: @Composable ColumnScope.() -> Unit) = Column(Modifier.fillMaxSize()) { content() }


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
fun <T> StandardLazyRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    lazyItems: List<T>,
    key: ((T) -> Any)?,
    item: @Composable (item: T) -> Unit,
) = LazyRow(
    modifier = modifier,
    horizontalArrangement = horizontalArrangement,
    verticalAlignment = verticalAlignment
) { items(items = lazyItems, key = key) { choice -> item(choice) } }

@Composable
fun ContainerRow(
    modifier: Modifier = Modifier,
    title: String = "",
    trailingIcon: (@Composable () -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) = Column(modifier.fillMaxWidth()) {
    if (title != "") StandardRow(Modifier.padding(0.dp, 8.dp)) {
        TextSubtitle(title)
        Spacer(Modifier.width(16.dp))
        trailingIcon?.let { trailingIcon() }
    }
    StandardRow(Modifier.fillMaxWidth()) { content() }
}

@Composable
fun ContainerColumn(
    modifier: Modifier = Modifier,
    title: String = "",
    trailingIcon: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) = Column(modifier.fillMaxWidth()) {
    if (title != "") StandardRow {
        TextSubtitle(title)
        Spacer(Modifier.width(16.dp))
        trailingIcon?.let { it() }
    }
    content()
}

@Composable
fun SingleParagraph(
    title: String,
    paragraph: String,
) = Column {
    TextSubtitle(text = title)
    TextParagraph(text = paragraph)
}
