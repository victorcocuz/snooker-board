package com.quickpoint.snookerboard.ui.styles

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
            ),
        color = Color.Transparent
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


@Composable
fun TextNavHeadline(text: String) {
    Text(
        modifier = Modifier
            .padding(0.dp, MaterialTheme.spacing.small, 0.dp, 0.dp)
            .fillMaxWidth(),
        textAlign = TextAlign.Center,
        text = text,
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
    )
}

@Composable
fun TextNavTitle(text: String) {
    Text(
        modifier = Modifier.padding(0.dp, MaterialTheme.spacing.smallMedium, 0.dp, 0.dp),
        text = text,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
    )
}

@Composable
fun TextNavParagraphSubTitle(text: String) {
    Text(
        modifier = Modifier.padding(0.dp, MaterialTheme.spacing.small, 0.dp, 0.dp),
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
    )
}

@Composable
fun TextNavParagraph(text: String) {
    Text(
        text = text, style = MaterialTheme.typography.bodySmall
    )
}

@Composable
fun ClickableText(text: String, onClick: () -> Unit) =
    Button(
        modifier = Modifier,
        onClick = { onClick() },
        shape = RoundedCornerShape(MaterialTheme.spacing.extraSmall)
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge
        )
    }

@Composable
fun ButtonDonate(text: String, price: String, image: Painter, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .width(100.dp)
            .height(100.dp)
            .background(MaterialTheme.colorScheme.tertiary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextNavParagraphSubTitle(text)
        Image(image,text)
        TextNavParagraph(price)
    }
}