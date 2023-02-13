package com.quickpoint.snookerboard.ui.fragments.navdrawer

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.quickpoint.snookerboard.BuildConfig
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.ui.components.ClickableText
import com.quickpoint.snookerboard.ui.components.FragmentContent
import com.quickpoint.snookerboard.ui.components.TextHeadline
import com.quickpoint.snookerboard.ui.components.TextParagraph
import com.quickpoint.snookerboard.ui.components.TextSubtitle
import com.quickpoint.snookerboard.utils.Constants.EMAIL_SUBJECT_IMPROVE
import com.quickpoint.snookerboard.utils.Constants.GOOGLE_FORM_URI
import com.quickpoint.snookerboard.utils.sendEmail

@Composable
fun ScreenDrawerImprove(
    navController: NavController
) {
    FragmentContent(Modifier.verticalScroll(rememberScrollState())) {
        TextHeadline(stringResource(R.string.menu_drawer_improve))
        TextParagraph(stringResource(R.string.f_nav_improve_tv_description))
        TextSubtitle(stringResource(R.string.f_nav_improve_tv_survey_header))
        TextParagraph(stringResource(R.string.f_nav_improve_tv_survey_body))
        val context = LocalContext.current.applicationContext
        val uriHandler = LocalUriHandler.current
        ClickableText(stringResource(R.string.f_nav_improve_tv_survey_link)) {
            uriHandler.openUri(GOOGLE_FORM_URI)
        }
        TextSubtitle(stringResource(R.string.f_nav_improve_tv_contact_header))
        TextParagraph(stringResource(R.string.f_nav_improve_tv_contact_body))
        ClickableText(stringResource(R.string.f_nav_improve_tv_contact_email)) {
            context.sendEmail(arrayOf(BuildConfig.ADMIN_EMAIL), EMAIL_SUBJECT_IMPROVE)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FragmentDrawerImprovePreview() {
    ScreenDrawerImprove(rememberNavController())
}