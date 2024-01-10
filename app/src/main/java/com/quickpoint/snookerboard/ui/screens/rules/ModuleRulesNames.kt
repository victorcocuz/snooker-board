package com.quickpoint.snookerboard.ui.screens.rules

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.core.base.Event
import com.quickpoint.snookerboard.domain.utils.getPlaceholderStringIdByKey
import com.quickpoint.snookerboard.domain.utils.getPlayerNameByKey
import com.quickpoint.snookerboard.ui.components.TextSubtitle
import com.quickpoint.snookerboard.ui.theme.spacing
import com.quickpoint.snookerboard.data.K_PLAYER01_FIRST_NAME
import com.quickpoint.snookerboard.data.K_PLAYER01_LAST_NAME
import com.quickpoint.snookerboard.data.K_PLAYER02_FIRST_NAME
import com.quickpoint.snookerboard.data.K_PLAYER02_LAST_NAME

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ModuleRulesNames(
    rulesVm: RulesViewModel,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?,
) = Row {
    val nameChangeEvent by rulesVm.eventPlayerNameChange.collectAsState(Event(Unit))
    nameChangeEvent.getContentIfNotHandled() // Used to trigger re-composition
    ComponentNameColumn(
        K_PLAYER01_FIRST_NAME,
        K_PLAYER01_LAST_NAME,
        focusManager,
        keyboardController,
    ) { key, value -> rulesVm.onPlayerNameChange(key, value) }

    ComponentNameColumn(
        K_PLAYER02_FIRST_NAME,
        K_PLAYER02_LAST_NAME,
        focusManager,
        keyboardController,
    ) { key, value -> rulesVm.onPlayerNameChange(key, value) }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RowScope.ComponentNameColumn(
    key_first_name: String,
    key_last_name: String,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?,
    onChange: (String, String) -> Unit
) = Column(
    Modifier
        .weight(1f)
        .padding(start = MaterialTheme.spacing.smallMedium)
) {
    TextSubtitle(stringResource(R.string.l_rules_main_tv_player_a_label))
    AppTextField(
        key = key_first_name,
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) }),
    ) { key, value -> onChange(key, value) }
    AppTextField(
        key = key_last_name,
        keyboardActions = KeyboardActions(
            onNext = { if (key_last_name != K_PLAYER02_LAST_NAME) focusManager.moveFocus(FocusDirection.Next) },
            onDone = { if (key_last_name == K_PLAYER02_LAST_NAME) keyboardController?.hide() }),
    ) { key, value -> onChange(key, value) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTextField(
    key: String,
    keyboardActions: KeyboardActions = KeyboardActions(),
    onChange: (String, String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, MaterialTheme.spacing.extraSmall),
        textStyle = MaterialTheme.typography.titleMedium,
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Black,
            unfocusedBorderColor = Color.Gray,
            disabledBorderColor = Color.Gray,
            disabledTextColor = Color.Black
        ),
        value = getPlayerNameByKey(key),
        keyboardOptions = KeyboardOptions(
            imeAction = if (key == K_PLAYER02_LAST_NAME) ImeAction.Done else ImeAction.Next, keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Sentences
        ),
        keyboardActions = keyboardActions,
        placeholder = { Text(text = stringResource(getPlaceholderStringIdByKey(key)), style = MaterialTheme.typography.titleMedium.copy(color = Color.LightGray)) },
        onValueChange = { onChange(key, it) },
    )
}