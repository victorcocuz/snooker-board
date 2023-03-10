package com.quickpoint.snookerboard.ui.fragments.navdrawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.billing.PurchaseHelper
import com.quickpoint.snookerboard.ui.components.FragmentContent
import com.quickpoint.snookerboard.ui.components.TextHeadline
import com.quickpoint.snookerboard.ui.components.TextParagraph
import com.quickpoint.snookerboard.ui.components.TextSubtitle
import com.quickpoint.snookerboard.ui.theme.spacing
import com.quickpoint.snookerboard.utils.Constants
import com.quickpoint.snookerboard.utils.Constants.PRODUCT_BEER
import com.quickpoint.snookerboard.utils.Constants.PRODUCT_COFFEE
import com.quickpoint.snookerboard.utils.Constants.PRODUCT_LUNCH

@Composable
fun ScreenDrawerSupport(purchaseHelper: PurchaseHelper) {
    val context = LocalContext.current.applicationContext

    val buyEnabled by purchaseHelper.buyEnabled.collectAsState(false)
    val consumeEnabled by purchaseHelper.consumeEnabled.collectAsState(false)
    val productName by purchaseHelper.productName.collectAsState(Constants.EMPTY_STRING)
    val statusText by purchaseHelper.statusText.collectAsState(Constants.EMPTY_STRING)
    val priceText by purchaseHelper.priceText.collectAsState(List(3) { Constants.EMPTY_STRING })

    FragmentContent {
        TextHeadline(stringResource(R.string.menu_drawer_support))
        TextParagraph(stringResource(R.string.dr_donate_tv_description))
        Spacer(Modifier.height(MaterialTheme.spacing.medium))
        Row(
            modifier = Modifier.fillMaxWidth(1f),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ButtonDonate(
                stringResource(R.string.dr_donate_tv_coffee_title),
                priceText[0],
                painterResource(R.drawable.ic_temp_donate_coffee)
            ) { purchaseHelper.makePurchase(PRODUCT_COFFEE) }
            ButtonDonate(
                stringResource(R.string.dr_donate_tv_beer_title),
                priceText[1],
                painterResource(R.drawable.ic_temp_donate_beer)
            ) { purchaseHelper.makePurchase(PRODUCT_BEER) }
            ButtonDonate(
                stringResource(R.string.dr_donate_tv_lunch_title),
                priceText[2],
                painterResource(R.drawable.ic_temp_donate_lunch)
            ) { purchaseHelper.makePurchase(PRODUCT_LUNCH) }
        }
    }
}

@Composable
fun ButtonDonate(text: String, price: String, image: Painter, onClick: () -> Unit = {}) = Column(
    modifier = Modifier
        .clickable { onClick() }
        .width(100.dp)
        .height(100.dp)
        .background(MaterialTheme.colorScheme.tertiary),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    TextSubtitle(text)
    Image(image, text)
    TextParagraph(price)
}



