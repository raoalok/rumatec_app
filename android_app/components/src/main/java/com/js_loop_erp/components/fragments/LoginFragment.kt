package com.js_loop_erp.components.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.js_loop_erp.components.BuildConfig
import com.js_loop_erp.components.MainActivity
import  com.js_loop_erp.components.R
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.X509Certificate


class LoginFragment: DialogFragment()
{
    private var customView: View? = null;
    private var loginButton: Button? = null
    private var userName: EditText? = null
    private var password: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view: View = getLayoutInflater().inflate(R.layout.login_fragment, null);

        val builder = AlertDialog.Builder(requireContext())
        isCancelable = false

        loginButton = view.findViewById(R.id.loginButton)
        userName    = view.findViewById(R.id.username)
        password    = view.findViewById(R.id.password)

        loginButton?.setOnClickListener {
            if(userName?.text != null && userName?.text?.length!! > 1 && password?.text != null && password?.text?.length!! >7){
                loginCheck(userName?.text.toString().trimIndent(), password?.text.toString())
                //loginCheck("sunilsuwal22@gmail.com", "alok*2527" )
                //alokrao56@gmail.com
                //pass: alok*2527

                loginButton?.isClickable = false
                loginButton?.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.lightgreen)))
            } else {
                Toast.makeText(context, "Check Input parameters.", Toast.LENGTH_LONG).show()
                //loginCheck("sunilsuwal22@gmail.com", "alok*2527" )
                //loginCheck("rashi@panavbiotech.com", "rashi@123" )
            }
//            dialog!!.dismiss()
        }

        builder.setView(view)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onStart() {
        super.onStart()


        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val window = requireActivity().window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dialog?.window?.setDecorFitsSystemWindows(false)
            dialog?.window?.decorView?.findViewById<View>(android.R.id.content)?.setOnApplyWindowInsetsListener { v, insets ->
                val imeInsets = insets.getInsets(WindowInsets.Type.ime())
                val navInsets = insets.getInsets(WindowInsets.Type.systemBars())

                v.setPadding(
                    v.paddingLeft,
                    v.paddingTop,
                    v.paddingRight,
                    maxOf(imeInsets.bottom, navInsets.bottom)
                )
                insets
            }
        } else {
            // Fallback for pre-Android 11
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    )
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Set your animation style here
        return dialog
    }

    private fun loginCheck(username: String, password: String){

        Log.d(TAG, "loginCheck: login_token_get_url ${ReceiverMediator.SERVER_SYNC_URI}/api/auth/login")
        var responBody :String =""
        var client = OkHttpClient()

        if(BuildConfig.DEBUG_ONLY_BUILD){

            Log.d(TAG, "loginCheck: Debug Build Bypass the SSL check")
            val trustAllCerts = arrayOf<TrustManager>(
                @SuppressLint("CustomX509TrustManager")
                object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkClientTrusted(
                        chain: Array<out java.security.cert.X509Certificate>?,
                        authType: String?
                    ) {}
                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkServerTrusted(
                        chain: Array<out java.security.cert.X509Certificate>?,
                        authType: String?
                    ) {}
                    override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate> = arrayOf()
                }
            )

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = sslContext.socketFactory

            client = OkHttpClient.Builder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .build()
        } else {
            client = OkHttpClient.Builder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .build()
        }

        val mediaType = "application/x-www-form-urlencoded".toMediaType()
        //val body = "email=sunilsuwal22@gmail.com&password=alok*2527".toRequestBody(mediaType)
        val body = "email=${username}&password=${password}".toRequestBody(mediaType)
        val request = Request.Builder()
            .url("${ReceiverMediator.SERVER_SYNC_URI}/api/auth/login")
            .post(body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful){
                        val bundle = Bundle()
                        bundle.putInt(LOGIN_RESULT.toString(),400)
                        setFragmentResult(LoginFragment.TAG,bundle)

                        loginButton?.isClickable = true
                        loginButton?.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.green)))

                        throw IOException("Unexpected code $response")
                    }

                    responBody = response.body?.string() ?: ""

                    val bundle = Bundle()
                    bundle.putInt(LOGIN_RESULT.toString(),200)
                    bundle.putString(RESPONSE_STRING, responBody.toString())
                    bundle.putString(USER_EMAIL,username)
                    RESPONSE_STRING = responBody
                    setFragmentResult(LoginFragment.TAG,bundle)
                    dialog!!.dismiss()
                }
            }
        })
    }

    companion object {
        val TAG = LoginFragment::class.java.simpleName

        var LOGIN_RESULT: Int = 0
        var RESPONSE_STRING: String = "responseString"
        var USER_EMAIL: String = " "

    }
}