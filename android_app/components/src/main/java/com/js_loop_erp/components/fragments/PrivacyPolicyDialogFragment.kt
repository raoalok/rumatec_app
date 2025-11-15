package com.js_loop_erp.components.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.DialogFragment
import com.js_loop_erp.components.R

class PrivacyPolicyDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogThemeNoTitle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_privacy_policy, null)
        val webView = view.findViewById<WebView>(R.id.privacy_policy_webview)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
        }

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        webView.loadUrl("https://rvapp-ochre.vercel.app/privacy.html")
        //webView.loadUrl("https://policies.google.com/privacy?hl=en-US")
        // or: webView.loadUrl("file:///android_asset/privacy_policy.html")

        return AlertDialog.Builder(requireContext(), R.style.AlertDialogThemeNoTitle)
            .setTitle("Privacy Policy")
            .setView(view)
            .setPositiveButton("Close") { _, _ -> dismiss() }
            .create()
    }
}
