package com.quickpoint.snookerboard.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.models.isFrameAndMatchEqual
import com.quickpoint.snookerboard.domain.models.isFrameEqual
import com.quickpoint.snookerboard.domain.models.isFrameInProgress
import com.quickpoint.snookerboard.ui.fragments.game.GameViewModel

class MenuItem(
    val id: String,
    val title: String,
    val contentDescription: String,
    val imageVector: ImageVector,
    var isActive: Boolean = true,
)

object MenuItemIds {
    const val ID_MENU_ITEM_UNDO = "id_menu_item_undo"
    const val ID_MENU_ITEM_MORE = "id_menu_item_more"
    const val ID_MENU_ITEM_RERACK = "id_menu_item_rerack"
    const val ID_MENU_ITEM_CONCEDE_FRAME = "id_menu_item_concede_frame"
    const val ID_MENU_ITEM_CONCEDE_MATCH = "id_menu_item_concede_match"
    const val ID_MENU_ITEM_CANCEL_MATCH = "id_menu_item_cancel_match"
    const val ID_MENU_ITEM_LOG = "id_menu_item_log"
}

@Composable
fun getMenuItems() = listOf(
    MenuItem(
        id = Screen.DrawerRules.route,
        title = stringResource(R.string.menu_drawer_rules),
        contentDescription = stringResource(R.string.menu_drawer_rules),
        imageVector = Icons.Default.Rule
    ),
    MenuItem(
        id = Screen.DrawerImprove.route,
        title = stringResource(R.string.menu_drawer_improve),
        contentDescription = stringResource(R.string.menu_drawer_improve),
        imageVector = Icons.Default.ContactSupport
    ),
    MenuItem(
        id = Screen.DrawerSupport.route,
        title = stringResource(R.string.menu_drawer_support),
        contentDescription = stringResource(R.string.menu_drawer_support),
        imageVector = Icons.Default.Savings
    ),
    MenuItem(
        id = Screen.DrawerSettings.route,
        title = stringResource(R.string.menu_drawer_settings),
        contentDescription = stringResource(R.string.menu_drawer_settings),
        imageVector = Icons.Default.Settings
    ),
    MenuItem(
        id = Screen.DrawerAbout.route,
        title = stringResource(R.string.menu_drawer_about),
        contentDescription = stringResource(R.string.menu_drawer_about),
        imageVector = Icons.Default.Info
    )
)

@Composable
fun GameViewModel.getActionItems() = listOf(
    MenuItem(
        id = MenuItemIds.ID_MENU_ITEM_UNDO,
        title = stringResource(R.string.menu_item_undo),
        contentDescription = stringResource(R.string.menu_item_undo),
        imageVector = Icons.Default.Undo,
        isActive = frameStack.isFrameInProgress()
    ),
    MenuItem(
        id = MenuItemIds.ID_MENU_ITEM_MORE,
        title = stringResource(R.string.menu_item_more),
        contentDescription = stringResource(R.string.menu_item_more),
        imageVector = Icons.Default.MoreVert
    )
)

@Composable
fun GameViewModel.getActionItemsOverflow() = listOf(
    MenuItem(
        id = MenuItemIds.ID_MENU_ITEM_RERACK,
        title = stringResource(R.string.menu_item_rerack),
        contentDescription = stringResource(R.string.menu_item_rerack),
        imageVector = Icons.Default.RestartAlt,
        isActive = frameStack.isFrameInProgress()
    ),
    MenuItem(
        id = MenuItemIds.ID_MENU_ITEM_CONCEDE_FRAME,
        title = stringResource(R.string.menu_item_concede_frame),
        contentDescription = stringResource(R.string.menu_item_concede_frame),
        imageVector = Icons.Default.Done,
        isActive = !score.isFrameEqual()
    ),
    MenuItem(
        id = MenuItemIds.ID_MENU_ITEM_CONCEDE_MATCH,
        title = stringResource(R.string.menu_item_concede_match),
        contentDescription = stringResource(R.string.menu_item_concede_match),
        imageVector = Icons.Default.DoneAll,
        isActive = !score.isFrameAndMatchEqual()
    ),
    MenuItem(
        id = MenuItemIds.ID_MENU_ITEM_CANCEL_MATCH,
        title = stringResource(R.string.menu_item_cancel_match),
        contentDescription = stringResource(R.string.menu_item_cancel_match),
        imageVector = Icons.Default.Cancel
    ),
    MenuItem(
        id = MenuItemIds.ID_MENU_ITEM_LOG,
        title = stringResource(R.string.menu_item_log),
        contentDescription = stringResource(R.string.menu_item_log),
        imageVector = Icons.Default.BugReport,
    )
)
