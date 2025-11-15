package com.js_loop_erp.components

import android.net.Uri

public interface FragmentActivityI {
    fun showMessage(myString: String?): Int?
    fun openUri(url: Uri)
}