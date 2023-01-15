package com.quickpoint.snookerboard.compose.ui.styles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.compose.ui.theme.Beige
import com.quickpoint.snookerboard.compose.ui.theme.Black
import com.quickpoint.snookerboard.compose.ui.theme.CreamBright
import com.quickpoint.snookerboard.compose.ui.theme.Green
import com.quickpoint.snookerboard.compose.ui.theme.White
import com.quickpoint.snookerboard.compose.ui.theme.spacing
import com.quickpoint.snookerboard.fragments.rules.RulesViewModel
import com.quickpoint.snookerboard.fragments.rules.RulesViewModel.*
import com.quickpoint.snookerboard.fragments.rules.RulesViewModel.RulesUpdateAction.*
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.MatchSettings.*


@Composable
fun ClickableText(text: String, onClick: () -> Unit) = Button(
    modifier = Modifier, onClick = { onClick() }, shape = RoundedCornerShape(MaterialTheme.spacing.extraSmall)
) {
    Text(
        textAlign = TextAlign.Center, text = text.uppercase(), style = MaterialTheme.typography.labelLarge
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
        Image(image, text)
        TextNavParagraph(price)
    }
}

@Composable
fun RulesHandicapLabel(
    rulesVm: RulesViewModel,
    action: RulesUpdateAction,
    ) {
    val rulesUpdateAction: Event<Unit> by rulesVm.eventRulesUpdated.observeAsState(Event(Unit))
    var handicap by remember { mutableStateOf(0)}
    LaunchedEffect(key1 = rulesUpdateAction) {
        handicap = when (action) {
            RULES_HANDICAP_FRAME -> SETTINGS.handicapFrame
            else -> SETTINGS.handicapMatch
        }
    }
    Text(
        modifier = Modifier.width(60.dp),
        textAlign = TextAlign.Center,
        text = "${SETTINGS.getHandicap(handicap, -1)} - ${SETTINGS.getHandicap(handicap, 1)}")
}

@Composable
fun ButtonStandardHoist(
    text: String,
    rulesVm: RulesViewModel,
    action: RulesUpdateAction,
    value: Int = -2,
) {
    val rulesUpdateAction: Event<Unit> by rulesVm.eventRulesUpdated.observeAsState(Event(Unit))
    LaunchedEffect(key1 = rulesUpdateAction, block = {}) // Used to refresh composition when rules change
    ButtonStandard(
        text = text,
        onClick = { rulesVm.updateAction(action, value) },
        isSelected = value == when (action) {
            RULES_STARTING_PLAYER -> SETTINGS.startingPlayer
            RULES_AVAILABLE_FRAMES -> SETTINGS.availableFrames
            RULES_AVAILABLE_REDS -> SETTINGS.availableReds
            RULES_FOUL_MODIFIER -> SETTINGS.foulModifier
            else -> -1000
        }
    )
}

@Composable
fun ButtonStandard(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean = true,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier
            .padding(top = 6.dp, bottom = 8.dp)
            .height(40.dp),
        shape = RoundedCornerShape(MaterialTheme.spacing.extraSmall),
        border = BorderStroke(1.dp, if (isSelected) Beige else Black),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Green else CreamBright
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(color = if (isSelected) White else Black)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    text: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    imeAction: ImeAction = ImeAction.Next,
    keyboardType: KeyboardType = KeyboardType.Text,
    keyBoardActions: KeyboardActions = KeyboardActions(),
    isEnabled: Boolean = true,
    placeholder: String,
    onChange: (String) -> Unit = {},
) {
    OutlinedTextField(
        textStyle = MaterialTheme.typography.titleMedium,
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Gray,
            disabledBorderColor = Color.Gray,
            disabledTextColor = Color.Black
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(0.dp, MaterialTheme.spacing.extraSmall),
        value = text,
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            keyboardType = keyboardType,
            capitalization = KeyboardCapitalization.Sentences
        ),
        keyboardActions = keyBoardActions,
        enabled = isEnabled,
        placeholder = { Text(text = placeholder, style = MaterialTheme.typography.titleMedium.copy(color = Color.LightGray)) },
        onValueChange = onChange,
    )
}