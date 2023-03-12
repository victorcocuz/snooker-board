package com.quickpoint.snookerboard.ui.fragments.navdrawer

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.quickpoint.snookerboard.BuildConfig
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.core.utils.Constants.EMAIL_SUBJECT_IMPROVE
import com.quickpoint.snookerboard.core.utils.Constants.GOOGLE_FORM_URI
import com.quickpoint.snookerboard.core.utils.sendEmail
import com.quickpoint.snookerboard.ui.components.*

@Composable
fun ScreenDrawerImprove() = FragmentContent(Modifier.verticalScroll(rememberScrollState())) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    TextHeadline(stringResource(R.string.menu_drawer_improve))
    TextParagraph(stringResource(R.string.dr_improve_tv_description))
    SingleParagraph(stringResource(R.string.dr_improve_tv_survey_header), stringResource(R.string.dr_improve_tv_survey_body))
    ClickableText(stringResource(R.string.dr_improve_tv_survey_link)) { uriHandler.openUri(GOOGLE_FORM_URI) }
    SingleParagraph(stringResource(R.string.dr_improve_tv_contact_header), stringResource(R.string.dr_improve_tv_contact_body))
    ClickableText(stringResource(R.string.dr_improve_tv_contact_email)) {
        context.sendEmail(arrayOf(BuildConfig.ADMIN_EMAIL), EMAIL_SUBJECT_IMPROVE)
    }
}

@Preview(showBackground = true)
@Composable
fun FragmentDrawerImprovePreview() = ScreenDrawerImprove()
