package com.quickpoint.snookerboard.fragments.navdrawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.quickpoint.snookerboard.MainViewModel
import com.quickpoint.snookerboard.ui.styles.FragmentColumn
import com.quickpoint.snookerboard.ui.styles.GenericSurface
import com.quickpoint.snookerboard.ui.styles.MatchToggleLayout
import com.quickpoint.snookerboard.ui.theme.SnookerBoardTheme
import com.quickpoint.snookerboard.ui.theme.spacing
import com.quickpoint.snookerboard.utils.MatchToggleType

class NavSettingsFragment : Fragment() {
    private val mainVm: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SnookerBoardTheme {
                    GenericSurface {
                        FragmentSettings(mainVm)
                    }
                }
            }
        }
    }
}

@Composable
fun FragmentSettings(mainVm: MainViewModel) {
    FragmentColumn {
        Spacer(Modifier.height(MaterialTheme.spacing.large))
        MatchToggleLayout(MatchToggleType.ADVANCED_RULES, mainVm)
        Spacer(Modifier.height(MaterialTheme.spacing.large))
        MatchToggleLayout(MatchToggleType.ADVANCED_STATISTICS, mainVm)
        Spacer(Modifier.height(MaterialTheme.spacing.large))
        MatchToggleLayout(MatchToggleType.ADVANCED_BREAKS, mainVm)
    }
}

//@Preview(showBackground = true)
//@Composable
//fun FragmentSettingsPreview() {
//    FragmentSettings()
//}