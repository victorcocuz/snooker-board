package com.quickpoint.snookerboard.fragments.navdrawer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.billing.PurchaseHelper
import com.quickpoint.snookerboard.compose.ui.styles.ButtonDonate
import com.quickpoint.snookerboard.compose.ui.styles.FragmentColumn
import com.quickpoint.snookerboard.compose.ui.styles.TextHeadline
import com.quickpoint.snookerboard.compose.ui.styles.TextParagraph
import com.quickpoint.snookerboard.compose.ui.theme.spacing
import com.quickpoint.snookerboard.utils.PRODUCT_BEER
import com.quickpoint.snookerboard.utils.PRODUCT_COFFEE
import com.quickpoint.snookerboard.utils.PRODUCT_LUNCH
import timber.log.Timber

@Composable
fun FragmentDrawerSupport(
    navController: NavController,
    purchaseHelper: PurchaseHelper
) {
    val context = LocalContext.current.applicationContext

    val buyEnabled by purchaseHelper.buyEnabled.collectAsState(false)
    val consumeEnabled by purchaseHelper.consumeEnabled.collectAsState(false)
    val productName by purchaseHelper.productName.collectAsState("")
    val statusText by purchaseHelper.statusText.collectAsState("")
    val priceText by purchaseHelper.priceText.collectAsState(List(3){""})

    FragmentColumn {
        TextHeadline(stringResource(R.string.menu_drawer_support))
        TextParagraph(stringResource(R.string.f_nav_donate_tv_description))
        Spacer(Modifier.height(MaterialTheme.spacing.medium))
        Row(
            modifier = Modifier.fillMaxWidth(1f),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ButtonDonate(stringResource(R.string.f_nav_donate_tv_coffee_title), priceText[0], painterResource(R.drawable.ic_temp_donate_coffee)) {
                purchaseHelper.makePurchase(PRODUCT_COFFEE)
            }
            ButtonDonate(stringResource(R.string.f_nav_donate_tv_beer_title), priceText[1], painterResource(R.drawable.ic_temp_donate_beer)) {
                purchaseHelper.makePurchase(PRODUCT_BEER)
            }
            ButtonDonate(stringResource(R.string.f_nav_donate_tv_lunch_title), priceText[2], painterResource(R.drawable.ic_temp_donate_lunch)) {
                purchaseHelper.makePurchase(PRODUCT_LUNCH)
            }
        }
        Timber.e("message $statusText")
    }
}

//@Preview(showBackground = true)
//@Composable
//fun FragmentDrawerSupportPreview() {
//    FragmentDrawerSupport(purchaseHelper)
//}
