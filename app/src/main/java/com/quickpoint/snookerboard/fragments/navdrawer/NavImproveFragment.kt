package com.quickpoint.snookerboard.fragments.navdrawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.quickpoint.snookerboard.BuildConfig
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.ui.styles.ClickableText
import com.quickpoint.snookerboard.ui.styles.FragmentColumn
import com.quickpoint.snookerboard.ui.styles.GenericSurface
import com.quickpoint.snookerboard.ui.styles.TextNavHeadline
import com.quickpoint.snookerboard.ui.styles.TextNavParagraph
import com.quickpoint.snookerboard.ui.styles.TextNavParagraphSubTitle
import com.quickpoint.snookerboard.ui.theme.SnookerBoardTheme
import com.quickpoint.snookerboard.utils.EMAIL_SUBJECT_IMPROVE
import com.quickpoint.snookerboard.utils.GOOGLE_FORM_URI
import com.quickpoint.snookerboard.utils.sendEmail

class NavImproveFragment : androidx.fragment.app.Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SnookerBoardTheme {
                    GenericSurface {
                        FragmentNavImprove()
                    }
                }
            }
        }
    }
}

@Composable
fun FragmentNavImprove() {
    FragmentColumn(Modifier.verticalScroll(rememberScrollState())) {
        TextNavHeadline(stringResource(R.string.menu_drawer_improve))
        TextNavParagraph(stringResource(R.string.f_nav_improve_tv_description))
        TextNavParagraphSubTitle(stringResource(R.string.f_nav_improve_tv_survey_header))
        TextNavParagraph(stringResource(R.string.f_nav_improve_tv_survey_body))
        val context = LocalContext.current.applicationContext
        val uriHandler = LocalUriHandler.current
        ClickableText(stringResource(R.string.f_nav_improve_tv_survey_link)) {
            uriHandler.openUri(GOOGLE_FORM_URI)
        }
        TextNavParagraphSubTitle(stringResource(R.string.f_nav_improve_tv_contact_header))
        TextNavParagraph(stringResource(R.string.f_nav_improve_tv_contact_body))
        ClickableText(stringResource(R.string.f_nav_improve_tv_contact_email)) {
            context.sendEmail(arrayOf(BuildConfig.ADMIN_EMAIL), EMAIL_SUBJECT_IMPROVE)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FragmentNavImprovePreview() {
    FragmentNavImprove()
}