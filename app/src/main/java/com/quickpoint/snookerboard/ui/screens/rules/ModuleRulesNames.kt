package com.quickpoint.snookerboard.ui.screens.rules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import com.quickpoint.snookerboard.domain.models.DomainPlayer
import com.quickpoint.snookerboard.ui.components.TextSubtitle
import com.quickpoint.snookerboard.ui.theme.spacing

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ModuleRulesNames(
    rulesVm: RulesViewModel,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?,
) = Row {
    val nameChangeEvent by rulesVm.eventPlayerNameChange.collectAsState(Event(Unit))
    nameChangeEvent.getContentIfNotHandled() // Used to trigger re-composition

    val players by rulesVm.players.collectAsState()
    players.forEachIndexed { index, it ->
        ComponentNameColumn(
            it,
            index,
            focusManager,
            keyboardController,
        ) { player -> rulesVm.onPlayerNameChange(player) }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RowScope.ComponentNameColumn(
    player: DomainPlayer,
    index: Int,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?,
    onChange: (DomainPlayer) -> Unit
) = Column(
    Modifier
        .weight(1f)
        .padding(start = MaterialTheme.spacing.smallMedium)
) {
    TextSubtitle("${stringResource(R.string.l_rules_main_tv_player_label)} ${index + 1}")
    PlayerNameField(
        name = player.firstName,
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) }),
        orderId = if (index == 1) 3 else 1
    ) { name -> onChange(player.copy(firstName = name)) }
    PlayerNameField(
        name = player.lastName,
        keyboardActions = KeyboardActions(
            onNext = { if (index == 0) focusManager.moveFocus(FocusDirection.Next) },
            onDone = { if (index == 1) keyboardController?.hide() }),
        orderId = if (index == 1) 4 else 2
    ) { name -> onChange(player.copy(lastName = name)) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerNameField(
    name: String,
    keyboardActions: KeyboardActions = KeyboardActions(),
    orderId: Int,
    onChange: (String) -> Unit
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
        value = name,
        keyboardOptions = KeyboardOptions(
            imeAction = if (orderId == 4) ImeAction.Done else ImeAction.Next, keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Sentences
        ),
        keyboardActions = keyboardActions,
        placeholder = { Text(text = stringResource(getPlaceholderStringIdByOrderId(orderId)), style = MaterialTheme.typography.titleMedium.copy(color = Color.LightGray)) },
        onValueChange = { onChange(it) },
    )
}

private fun getPlaceholderStringIdByOrderId(id: Int): Int = when (id) {
    1 -> R.string.l_rules_main_hint_name_first
    2 -> R.string.l_rules_main_hint_name_last
    3 -> R.string.l_rules_main_hint_name_first
    else -> R.string.l_rules_main_hint_name_last
}