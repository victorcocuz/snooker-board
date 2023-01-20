package com.quickpoint.snookerboard.compose.ui.styles

import android.widget.LinearLayout
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
import androidx.compose.ui.viewinterop.AndroidView
import com.quickpoint.snookerboard.compose.ui.theme.Beige
import com.quickpoint.snookerboard.compose.ui.theme.Black
import com.quickpoint.snookerboard.compose.ui.theme.CreamBright
import com.quickpoint.snookerboard.compose.ui.theme.Green
import com.quickpoint.snookerboard.compose.ui.theme.White
import com.quickpoint.snookerboard.compose.ui.theme.spacing
import com.quickpoint.snookerboard.fragments.rules.RulesViewModel
import com.quickpoint.snookerboard.fragments.rules.RulesViewModel.*
import com.quickpoint.snookerboard.utils.Event
import com.quickpoint.snookerboard.utils.KEY_INT_MATCH_AVAILABLE_FRAMES
import com.quickpoint.snookerboard.utils.KEY_INT_MATCH_AVAILABLE_REDS
import com.quickpoint.snookerboard.utils.KEY_INT_MATCH_FOUL_MODIFIER
import com.quickpoint.snookerboard.utils.KEY_INT_MATCH_HANDICAP_FRAME
import com.quickpoint.snookerboard.utils.KEY_INT_MATCH_STARTING_PLAYER
import com.quickpoint.snookerboard.utils.MatchSettings.*
import com.shawnlin.numberpicker.NumberPicker


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
    key: String,
) {
    val rulesUpdateAction: Event<Unit> by rulesVm.eventRulesUpdated.observeAsState(Event(Unit))
    var handicap by remember { mutableStateOf(0) }
    LaunchedEffect(key1 = rulesUpdateAction) {
        handicap = when (key) {
            KEY_INT_MATCH_HANDICAP_FRAME -> Settings.handicapFrame
            else -> Settings.handicapMatch
        }
    }
    Text(
        modifier = Modifier.width(60.dp),
        textAlign = TextAlign.Center,
        text = "${Settings.getHandicap(handicap, -1)} - ${Settings.getHandicap(handicap, 1)}"
    )
}

@Composable
fun NumberPickerHoist(
    modifier: Modifier = Modifier,
    rulesVm: RulesViewModel,
) {
    val rulesUpdateAction: Event<Unit> by rulesVm.eventRulesUpdated.observeAsState(Event(Unit))
    LaunchedEffect(key1 = rulesUpdateAction, block = {}) // Used to refresh composition when rules change
    NumberPicker(modifier, rulesVm, Settings.availableFrames)
}

@Composable
fun NumberPicker(
    modifier: Modifier,
    rulesVm: RulesViewModel,
    newValue: Int
) {
    AndroidView( // Number Picker
        modifier = modifier
            .height(40.dp)
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium, MaterialTheme.spacing.small, 0.dp, 0.dp),
        factory = { context ->
            NumberPicker(context).apply {
                dividerColor = android.graphics.Color.WHITE
                setDividerDistance(360)
                orientation = LinearLayout.HORIZONTAL
                selectedTextColor = android.graphics.Color.WHITE
                textColor = android.graphics.Color.WHITE
                minValue = 1
                maxValue = 19
                value = newValue
                displayedValues = (minValue until maxValue * 2).filter { it % 2 != 0 }.map { it.toString() }.toTypedArray()
                setOnValueChangedListener { _, _, newVal ->
                    rulesVm.updateAction(KEY_INT_MATCH_AVAILABLE_FRAMES, newVal)
                }
            }
        },
        update = {
            it.value = newValue
        })
}

@Composable
fun ButtonStandardHoist(
    text: String,
    rulesVm: RulesViewModel,
    key: String,
    value: Int = -2,
) {
    val rulesUpdateAction: Event<Unit> by rulesVm.eventRulesUpdated.observeAsState(Event(Unit))
    LaunchedEffect(key1 = rulesUpdateAction, block = {}) // Used to refresh composition when rules change
    ButtonStandard(
        text = text,
        onClick = { rulesVm.updateAction(key, value) },
        isSelected = value == when (key) {
            KEY_INT_MATCH_STARTING_PLAYER -> Settings.startingPlayer
            KEY_INT_MATCH_AVAILABLE_FRAMES -> Settings.availableFrames
            KEY_INT_MATCH_AVAILABLE_REDS -> Settings.availableReds
            KEY_INT_MATCH_FOUL_MODIFIER -> Settings.foulModifier
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
    keyboardActions: KeyboardActions = KeyboardActions(),
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
        keyboardActions = keyboardActions,
        enabled = isEnabled,
        placeholder = { Text(text = placeholder, style = MaterialTheme.typography.titleMedium.copy(color = Color.LightGray)) },
        onValueChange = onChange,
    )
}