package com.quickpoint.snookerboard.compose.ui.styles

import android.widget.LinearLayout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.quickpoint.snookerboard.compose.ui.theme.*
import com.quickpoint.snookerboard.domain.objects.*
import com.quickpoint.snookerboard.domain.objects.MatchSettings.*
import com.quickpoint.snookerboard.fragments.rules.RulesViewModel
import com.quickpoint.snookerboard.fragments.rules.RulesViewModel.*
import com.quickpoint.snookerboard.utils.*
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
        TextParagraphSubTitle(text)
        Image(image, text)
        TextParagraph(price)
    }
}

@Composable
fun RulesHandicapLabel(
    rulesVm: RulesViewModel,
    key: String,
) {
    val rulesUpdateAction by rulesVm.eventMatchSettingsChange.collectAsState(Event(Unit))
    var handicap by remember { mutableStateOf(0) }
    LaunchedEffect(key1 = rulesUpdateAction) {
        handicap = when (key) {
            K_INT_MATCH_HANDICAP_FRAME -> Settings.handicapFrame
            else -> Settings.handicapMatch
        }
    }
    Text(
        modifier = Modifier.width(60.dp),
        textAlign = TextAlign.Center,
        text = "${getHandicap(handicap, -1)} - ${getHandicap(handicap, 1)}"
    )
}

@Composable
fun NumberPickerHoist(
    modifier: Modifier = Modifier,
    rulesVm: RulesViewModel,
) {
    val rulesUpdateAction by rulesVm.eventMatchSettingsChange.collectAsState(Event(Unit))
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
                    rulesVm.onMatchSettingsChange(K_INT_MATCH_AVAILABLE_FRAMES, newVal)
                }
            }
        },
        update = {
            it.value = newValue
        })
}

@Composable
fun ButtonStandardHoist(
    rulesVm: RulesViewModel,
    key: String,
    value: Int = -2,
) {
    val rulesUpdateAction by rulesVm.eventMatchSettingsChange.collectAsState(Event(Unit))
    LaunchedEffect(key1 = rulesUpdateAction, block = {}) // Used to refresh composition when rules change
    ButtonStandard(
        text = stringResource(getSettingsTextIdByKeyAndValue(key, value)),
        isSelected = isSettingsButtonSelected(key, value),
        onClick = { rulesVm.onMatchSettingsChange(key, value) }
    )
}

@Composable
fun ButtonStandard(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier.height(40.dp),
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

@Composable
fun ToggleButton(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean = false,
    painter: Painter,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier,
        shape = RoundedCornerShape(MaterialTheme.spacing.extraSmall),
        border = BorderStroke(1.dp, if (isSelected) Beige else Black),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Green else CreamBright
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(color = if (isSelected) White else Black)
        )
        Icon(painter = painter, contentDescription = null)
    }
}

@Composable
fun AppTextFieldHoist(
    modifier: Modifier = Modifier,
    rulesVm: RulesViewModel,
    key: String,
    keyboardActions: KeyboardActions = KeyboardActions(),
) {
    val nameChangeEvent by rulesVm.eventPlayerNameChange.collectAsState(Event(Unit))
//    nameChangeEvent.getContentIfNotHandled() // Simply used to call the observer, only to trigger composition
    AppTextField(
        modifier = modifier,
        text = getPlayerNameByKey(key),
        placeholder = stringResource(getPlaceholderStringIdByKey(key)),
        imeAction = if (key == K_PLAYER02_LAST_NAME) ImeAction.Done else ImeAction.Next,
        keyboardActions = keyboardActions,
        onChange = { rulesVm.onPlayerNameChange(key, it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTextField(
    modifier: Modifier,
    text: String,
    placeholder: String,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions(),
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
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction,
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Sentences
        ),
        keyboardActions = keyboardActions,
        placeholder = { Text(text = placeholder, style = MaterialTheme.typography.titleMedium.copy(color = Color.LightGray)) },
        onValueChange = onChange,
    )
}