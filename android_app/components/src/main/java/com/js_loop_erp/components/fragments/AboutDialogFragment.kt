package com.js_loop_erp.components.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.DialogFragment.STYLE_NO_TITLE
import com.js_loop_erp.components.BuildConfig
import com.js_loop_erp.components.R
import com.js_loop_erp.components.databinding.AboutDialogLayoutBinding
import com.js_loop_erp.components.databinding.AppPermissionDialogBinding


class AboutDialogFragment: DialogFragment() {
    var binding_ : AboutDialogLayoutBinding? = null
    private val binding get() = binding_!!

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogThemeNoTitle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding_ = AboutDialogLayoutBinding.inflate(inflater, container, false)

        val versionName = BuildConfig.VERSION_NAME

        binding.versionInfo.text = "Version: $versionName".toString()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}