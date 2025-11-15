package com.js_loop_erp.components.fragments.settings

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.js_loop_erp.components.R
import com.js_loop_erp.components.databinding.FragmentSettingsBinding
import com.js_loop_erp.components.fragments.AboutDialogFragment
import com.js_loop_erp.components.fragments.AppPermissionDialogFragment
import com.js_loop_erp.components.fragments.PrivacyPolicyDialogFragment

class SettingsFragment : DialogFragment() {

    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogThemeNoTitle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        _binding!!.fullscreenContentControls.findViewById<LinearLayout>(R.id.application_app_tour).setOnClickListener {
            val appTour = AppTourDialogFragment()
            appTour.show(childFragmentManager, "App Tour Dialog")
        }

        _binding!!.fullscreenContentControls.findViewById<LinearLayout>(R.id.fullscreen_content_controls).setOnClickListener {
            val appTour = AppPermissionDialogFragment()
            appTour.show(childFragmentManager, "App Tour Dialog")
        }

        _binding!!.fullscreenContentControls.findViewById<LinearLayout>(R.id.app_about_detail).setOnClickListener {
            val app_about = AboutDialogFragment()
            app_about.show(childFragmentManager,"App About")
        }

        _binding!!.fullscreenContentControls.findViewById<LinearLayout>(R.id.app_privacy_policy).setOnClickListener {
            PrivacyPolicyDialogFragment().show(childFragmentManager, "PrivacyPolicy")
        }

        return binding.root
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Set your animation style here
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {

    }
}