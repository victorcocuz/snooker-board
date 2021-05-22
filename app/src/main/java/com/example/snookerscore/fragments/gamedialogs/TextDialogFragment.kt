package com.example.snookerscore.fragments.gamedialogs

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.snookerscore.GenericEventsViewModel
import com.example.snookerscore.R
import com.example.snookerscore.databinding.FragmentTextFieldDialogBinding
import com.example.snookerscore.domain.MatchAction
import com.example.snookerscore.utils.EventObserver
import com.example.snookerscore.utils.getSharedPref
import com.example.snookerscore.utils.hideKeyboard
import com.example.snookerscore.utils.setSize


class TextDialogFragment : DialogFragment() {
    private val eventsViewModel: GenericEventsViewModel by activityViewModels()
    private val textDialogViewModel: TextDialogViewModel by viewModels()
    private lateinit var sharedPref: SharedPreferences
    private lateinit var imm: InputMethodManager

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setSize(resources.getDimension(R.dimen.dialog_factor))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentTextFieldDialogBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_text_field_dialog, container, false)

        isCancelable = false
        sharedPref = requireActivity().getSharedPref()
        val name = TextDialogFragmentArgs.fromBundle(requireArguments()).matchAction

        binding.apply {
            lifecycleOwner = this@TextDialogFragment
            varEventsViewModel = eventsViewModel
            varName = sharedPref.getString(
                when (name) {
                    MatchAction.NAME_CHANGE_A_QUERIED -> getString(R.string.shared_pref_match_player_a_name)
                    else -> getString(R.string.shared_pref_match_player_b_name)
                }, "Name"
            )

            dialogTextField.requestFocus()
            imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        eventsViewModel.apply {
            eventMatchActionQueried.observe(viewLifecycleOwner, EventObserver {
                if (it == MatchAction.NAME_CHANGE_CONFIRM) {
                    sharedPref.edit().putString(
                        when (name) {
                            MatchAction.NAME_CHANGE_A_QUERIED -> getString(R.string.shared_pref_match_player_a_name)
                            else -> getString(R.string.shared_pref_match_player_b_name)
                        }, binding.varName
                    ).apply()
                    onEventMatchActionConfirmed(it)
                }
                hideKeyboard()
                dismiss()
            })
        }
        return binding.root
    }
}