package com.js_loop_erp.components.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.textfield.TextInputEditText
import com.js_loop_erp.components.BuildConfig
import  com.js_loop_erp.components.MainActivity
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.databinding.ExpenseSubmitFragmentBinding
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.w3c.dom.Text
import java.io.File
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class ExpenseSubmitFragment: DialogFragment() {
    private var _binding: ExpenseSubmitFragmentBinding? = null

    val mainActivity = MainActivity()
    private val binding get() = _binding!!

    private lateinit var imageView1: ImageView
    private lateinit var imageView2: ImageView
    private lateinit var imageView3: ImageView
    private lateinit var btnSubmit: Button
    private lateinit var expenseReason: TextInputEditText
    private lateinit var expenseAmount: TextInputEditText

    var uriUri : Uri? = null
    var users: MutableList<String> = mutableListOf()
    var formDataParts: MutableList<FormDataPart> = mutableListOf()
    private lateinit var pickImagesLauncher: ActivityResultLauncher<String>

    var dialog :AlertDialog? = null

/*    val formDataParts = mutableListOf(
        FormDataPart(
            name = "file1",
            file = File("/C:/Users/sanja/Downloads/To_do_29_Feb_2024.dart")
        ),
        FormDataPart(
            name = "file1",
            file = File("/C:/Users/sanja/Downloads/image(1).png")
        ),
        FormDataPart(
            name = "file2",
            file = File("/C:/Users/sanja/Downloads/runSRTReceiverTest")
        )
        // Add more FormDataPart objects if needed for additional files
    )*/

    private lateinit var mListView:ListView

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View{
        _binding = ExpenseSubmitFragmentBinding.inflate(inflater, container, false)

        btnSubmit = binding.btnSubmitExpense
        imageView1 = binding.showImage1
        imageView2 = binding.showImage2
        imageView3 = binding.showImage3

        expenseReason = binding.expenseReason
        expenseAmount = binding.expenseAmount

        mListView = binding.userImageListView.findViewById<ListView>(R.id.user_image_list_view)

        btnSubmit.setOnClickListener {
//            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
//            startActivityForResult(intent, 444)

/*            val bundle = Bundle().apply{
                putInt(ExpensePagination.EXPENSE_UPDATE_FRAGMENT, 1)
            }
            setFragmentResult(ExpensePagination.EXPENSE_UPDATE_FRAGMENT,bundle)*/

            Log.d(TAG, "onCreateView: formDataPart ${formDataParts.toString()}")
            if((formDataParts.size > 0) && (expenseAmount.text.toString().toInt() > 0) && (expenseReason.text.toString().length > 4 )){
                //uploadImagesToCloud(expenseReason.text.toString(),expenseAmount.text.toString())
                val builder = android.app.AlertDialog.Builder(requireContext())
                builder.setTitle("Submit Expense Claim?")
                //builder.setMessage("Device Location is Required.")
                builder.setPositiveButton("Ok") { _, _ ->
                    btnSubmit.isClickable = false
                    uploadImagesToCloud(expenseReason.text.toString(),expenseAmount.text.toString())
                }
                builder.setNegativeButton("Cancel") { _, _ ->
                }
                builder.setCancelable(false)
                val dialog = builder.create()
                dialog.show()
                dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
            } else {
                Toast.makeText(activity, "Check Input Field", Toast.LENGTH_LONG).show()
            }
           // dismiss()
        }

        imageView1.setOnClickListener {

//            users = mutableListOf()
//            formDataParts = mutableListOf()

            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            /*intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 444);*/
             */
            pickImagesLauncher.launch("image/*")
        }

        imageView2.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            startActivityForResult(intent, 445)
        }

        imageView3.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            startActivityForResult(intent, 446)
        }


        pickImagesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
            if (uris.isNotEmpty()) {
                for (uri in uris) {
                    Log.d("ImagePicker", "Selected URI: $uri")
                    uriUri = uri  // If you're storing the last one
                    // users.add(uri.toString()) // If you store strings
                    lifecycleScope.launch {

                        val imageurl: Uri? = uriUri

                        val query = imageurl?.let { context?.getContentResolver()?.query(it, null, null, null, null) }
                        query?.use { cursor ->
                            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                            val mediaColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)

                            Log.d(TAG, "onActivityResult: mediaStore_name ${mediaColumn}")

                            cursor.moveToFirst() // Move to the first row of the cursor

                            // Loop through the cursor using a for loop
                            for (i in 0 until cursor.count) {
                                val name_ = cursor.getString(nameColumn)
                                val size = cursor.getInt(sizeColumn)
                                Log.d("image_image_image", "$name_  $size ${cursor.count}")
                                users.add(name_)
                                formDataParts.add(FormDataPart("${name_}",File(imageurl.path), imageurl))
                                cursor.moveToNext() // Move to the next row of the cursor
                            }
                        }

                    }
                }

                val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, users)
                mListView.adapter = arrayAdapter
            } else {
                Toast.makeText(requireContext(), "No images selected", Toast.LENGTH_SHORT).show()
            }
        }


        //getDialog()?.setTitle("Submit Expense")
        return binding.root
    }

/*  this is deprecated in android 15
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode === 444 && resultCode === RESULT_OK) {
                // Get the Image from data
                val mClipData: ClipData? = data?.clipData
                val cout: Int = data?.getClipData()?.getItemCount() ?: 0

//                val users: MutableList<String> = mutableListOf()
//                var mListView = binding.userImageListView.findViewById<ListView>(R.id.user_image_list_view)
                Log.d(TAG, "onActivityResult1: url_url_url ${cout}")

                for (i in 0 until cout) {
                    // adding imageuri in array
                    val imageurl: Uri? = data!!.getClipData()?.getItemAt(i)?.getUri()

                    uriUri = imageurl

                    Log.d(TAG, "onActivityResult2: url_url_url ${imageurl}")

                    lifecycleScope.launch {

                        val query = imageurl?.let { context?.getContentResolver()?.query(it, null, null, null, null) }
                        query?.use { cursor ->
                            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                            val mediaColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)

                            Log.d(TAG, "onActivityResult: mediaStore_name ${mediaColumn}")


                            cursor.moveToFirst() // Move to the first row of the cursor

                            // Loop through the cursor using a for loop
                            for (i in 0 until cursor.count) {
                                val name_ = cursor.getString(nameColumn)
                                val size = cursor.getInt(sizeColumn)
                                Log.d("image_image_image", "$name_  $size ${cursor.count}")
                                users.add(name_)
                                formDataParts.add(FormDataPart("${name_}",File(imageurl.path), imageurl))
                                cursor.moveToNext() // Move to the next row of the cursor
                            }
                        }

                    }
                }

                Log.d("image_image_image_", "${users}")
                val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1, users)
                mListView.adapter = arrayAdapter

            } else {
                Toast.makeText(activity, "You haven't picked Image", Toast.LENGTH_LONG).show()
            }

            */
/*if(requestCode == 444){
            val selectedImageUri: Uri = data?.data!!
            if(null != selectedImageUri){
        //        activity?.runOnUiThread(Runnable {
                    imageView1.setImageURI(selectedImageUri)
         //       })
            }
        }*//*


*/
/*            if (requestCode == 445) {
                val selectedImageUri: Uri = data?.data!!
                if (null != selectedImageUri) {
                    //        activity?.runOnUiThread(Runnable {
                    imageView2.setImageURI(selectedImageUri)
                    //       })
                }
            }

            if (requestCode == 446) {
                val selectedImageUri: Uri = data?.data!!
                if (null != selectedImageUri) {
                    //        activity?.runOnUiThread(Runnable {
                    imageView3.setImageURI(selectedImageUri)
                    //       })
                }
            }*//*

        } else {
            Toast.makeText(activity, "You haven't picked Image", Toast.LENGTH_LONG).show()
        }

    }
*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView(){
        super.onDestroyView()
        _binding = null
    }

    private fun uploadImagesToCloud(description: String, amount: String) {
        if (ReceiverMediator.USER_TOKEN.length > 8) {
            var responBody: String = ""

            showTheServerSyncDialog(requireContext(), dialog, R.layout.alert_dialog_style)

            var client = OkHttpClient()

            if (BuildConfig.DEBUG_ONLY_BUILD) {

                Log.d(LoginFragment.TAG, "Debug Build Bypass the SSL check")

                val trustAllCerts = arrayOf<TrustManager>(
                    @SuppressLint("CustomX509TrustManager")
                    object : X509TrustManager {
                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkClientTrusted(
                            chain: Array<out java.security.cert.X509Certificate>?,
                            authType: String?
                        ) {
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        override fun checkServerTrusted(
                            chain: Array<out java.security.cert.X509Certificate>?,
                            authType: String?
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate> =
                            arrayOf()
                    }
                )

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory = sslContext.socketFactory

                client = OkHttpClient.Builder()
                    .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .build()
            } else {
                client = OkHttpClient.Builder()
                    .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
            }


            val mediaType = "text/plain".toMediaType()

            val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            formDataParts.forEach { formDataPart ->
                Log.d(TAG, "uploadImagesToCloudName: ${formDataPart.name}")
                Log.d(TAG, "uploadImagesToCloudFile: ${formDataPart.file}")

                val requestBody =
                    context?.contentResolver?.openInputStream(formDataPart.uriForm)?.use { input ->
                        input.readBytes().toRequestBody("multipart/form-data".toMediaType())
                    }

                requestBodyBuilder.addFormDataPart(
                    formDataPart.name,
                    formDataPart.name.toString(),
                    requestBody!!
                )
            }

            //Log.d(TAG, "uploadImagesToCloud: Token ${MainActivity.USER_ID}")

            requestBodyBuilder.addFormDataPart("description", description)
            requestBodyBuilder.addFormDataPart("amount", amount)
            //requestBodyBuilder.addFormDataPart("userId", MainActivity.USER_ID)

            val body = requestBodyBuilder.build()

            /*        val requestBody = context?.contentResolver?.openInputStream(uriUri!!)?.use { input ->
                        input.readBytes().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                    }

                    val body = MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("files[0]",
                            formDataParts[0].name.toString(),
                            requestBody!!)
                        .build()*/

            val request = Request.Builder()

                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/expenses")
                .post(body)
                .addHeader("Authorization", "Bearer ${ReceiverMediator.USER_TOKEN}")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    btnSubmit.isClickable = true
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            btnSubmit.isClickable = true
                            activity?.runOnUiThread(Runnable {
                                Toast.makeText(context, "Expense Upload Error", Toast.LENGTH_LONG)
                                    .show()
                                dialog?.dismiss()
                            })
                            throw IOException("Unexpected code $response")
                        } else {
                            responBody = response.body?.string() ?: ""
                            Log.d(TAG, "onResponse: ${responBody}")

                            /*activity?.runOnUiThread {
                                Toast.makeText(activity, "Network request failed", Toast.LENGTH_SHORT).show()
                            }*/

                            activity?.runOnUiThread(Runnable {
                                Toast.makeText(context, "Expense Uploaded", Toast.LENGTH_LONG)
                                    .show()
                                dialog?.dismiss()
                            })

                            //dismiss()
                            val bundle = Bundle().apply {
                                putInt(ExpensePagination.EXPENSE_UPDATE_FRAGMENT, 1)
                            }
                            setFragmentResult(ExpensePagination.EXPENSE_UPDATE_FRAGMENT, bundle)
                        }
                    }
                }
            })
        }
    }

    fun showTheServerSyncDialog(context: Context, alertDialog: AlertDialog?, layoutFile:Int){
        dialog = alertDialog
        val builder = AlertDialog.Builder(context)
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val dialogView: View = inflater.inflate(layoutFile, null) // R.layout.alert_dialog_style

        /*       val dialogTitle: TextView = dialogView.findViewById(R.id.dialogTitle)
                 val dialogMessage: TextView = dialogView.findViewById(R.id.dialogMessage)
                 val positiveButton: Button = dialogView.findViewById(R.id.positiveButton)
                 val negativeButton: Button = dialogView.findViewById(R.id.negativeButton)*/

        builder.setView(dialogView)
        builder.setCancelable(false)
        //dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog = builder.create()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()
    }

    companion object{
        val TAG: String = ExpenseSubmitFragment::class.java.name
    }

}

data class FormDataPart(
    val name: String,
    val file: File,
    val uriForm: Uri
)