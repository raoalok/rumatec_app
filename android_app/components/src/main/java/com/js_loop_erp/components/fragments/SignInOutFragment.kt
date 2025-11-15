package com.js_loop_erp.components.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.graphics.drawable.ColorDrawable
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.media.ImageReader
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.Rotate
import com.google.gson.Gson
import com.js_loop_erp.components.BuildConfig
import  com.js_loop_erp.components.R
import com.js_loop_erp.components.data_class.TripReportApprovedModel
import com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlan
import com.js_loop_erp.components.data_flow.TripTravelReportSubmitSharedFlow
import  com.js_loop_erp.components.databinding.SignInOutFragmentBinding
import com.js_loop_erp.components.fragments.LoginFragment.Companion
import com.js_loop_erp.components.fragments.LoginFragment.Companion.LOGIN_RESULT
import com.js_loop_erp.components.fragments.LoginFragment.Companion.RESPONSE_STRING
import com.js_loop_erp.components.fragments.LoginFragment.Companion.TAG
import com.js_loop_erp.components.fragments.LoginFragment.Companion.USER_EMAIL
import com.js_loop_erp.components.fragments.tripReportSubmit.TripApprovedPlanDetails
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.RuntimeException
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

class SignInOutFragment: DialogFragment() {

    private var _binding: SignInOutFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraManager: CameraManager
    private lateinit var cameraDevice: CameraDevice
    private lateinit var captureRequestBuilder: CaptureRequest.Builder
    private lateinit var captureSession: CameraCaptureSession
    private lateinit var previewSize: Size
    private lateinit var cameraId: String
    private var isFrontCamera: Boolean = true
    private lateinit var imageReader: ImageReader

    private var initialFingerSpacing = 0f
    private var zoomLevel = 1f

    val homeViewModel: HomeViewModel by viewModels()
    var argEdit:String= ""

    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogGreyTheme)
        argEdit = arguments?.getString("ARG1_KEY").toString()

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View?{
        super.onCreateView(inflater,container,savedInstanceState)
        _binding = SignInOutFragmentBinding.inflate(inflater, container, false)
        dialog?.setTitle("Current Session ...")
        return binding.root
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val fileSelfie = File(requireContext().filesDir, "IMG_Selfie.jpg")
        val fileOdometer = File(requireContext().filesDir, "IMG_Odometer.jpg")

        if (fileSelfie.exists()) {
            fileSelfie.delete()
        }

        if (fileOdometer.exists()) {
            fileOdometer.delete()
        }

        binding.cameraView.setOnTouchListener { _, event ->
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_POINTER_DOWN -> {
                    if(!isFrontCamera) {
                    initialFingerSpacing = getFingerSpacing(event)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!isFrontCamera) {
                        val fingerSpacing = getFingerSpacing(event)
                        if (fingerSpacing > initialFingerSpacing) {
                            // Zoom in
                            zoomLevel += 0.05f // Adjust zoom level increment/decrement as needed
                        } else if (fingerSpacing < initialFingerSpacing) {
                            // Zoom out
                            zoomLevel -= 0.05f
                        }
                        setZoomLevel()
                    }
                }
            }
            true
        }

        binding.btnSelfieCamera.setOnClickListener {
            isFrontCamera = true
            closeCamera()
            if(checkSelfPermission(requireContext(),Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                openCamera()
            } else {
                Toast.makeText(requireContext(),"Camera Permission Required", Toast.LENGTH_LONG).show()
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_REQUEST_CODE
                )
            }

        }

        binding.btnOdometerCamera.setOnClickListener {
            isFrontCamera = false
            closeCamera()
            if(checkSelfPermission(requireContext(),Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                openCamera()
            } else {
                Toast.makeText(requireContext(),"Camera Permission Required", Toast.LENGTH_LONG).show()
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_REQUEST_CODE
                )
            }
        }

        binding.btnCaptureImage.setOnClickListener {
            captureStillPicture()
        }

        binding.uploadImageToCloud.setOnClickListener{
            val fileSelfie = File(requireContext().filesDir, "IMG_Selfie.jpg")
            val fileOdometer = File(requireContext().filesDir, "IMG_Odometer.jpg")
            if(fileSelfie.exists() && fileOdometer.exists()){
                uploadImagesToCloud()
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(activity, "Image missing, Capture Odometer and Selfie Image", Toast.LENGTH_LONG).show()
                }
            }
            /*val bundle = Bundle()
            bundle.putString(SIGN_IN_OUT, argEdit)
            setFragmentResult(SignInOutFragment.TAG, bundle)
            dismiss()*/
        }

        if( checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA)  == PackageManager.PERMISSION_GRANTED){
            binding.cameraView.surfaceTextureListener = surfaceTextureListener
        } else {
            activity?.runOnUiThread { Toast.makeText(activity, "Application Camera Permission, Go-To settings and grant Permission.", Toast.LENGTH_LONG).show() }
            dismiss()
        }

        loginStatusCheck()

        //parentFragmentManager.setFragmentResultListener(TAG, viewLifecycleOwner) { _, bundle ->
            ///val confirmed = bundle.getInt(TAG)

        //}
    }

    private fun dialogDismiss(){
        //activity?.runOnUiThread {
            val bundle = Bundle().apply{
                putString(SIGN_IN_OUT, argEdit)
            }
            setFragmentResult(SignInOutFragment.TAG, bundle)
            dialog!!.dismiss()
            dismiss()
        //}
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {

        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            //TODO("Not yet implemented")
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
            //TODO("Not yet implemented")
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            //TODO("Not yet implemented")
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            return false
        }

    }

    @RequiresPermission(Manifest.permission.CAMERA)
    private fun openCamera(){
        cameraManager = requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try{
            //val cameraId = cameraManager.cameraIdList[0]
            cameraId = if(isFrontCamera){
                getFrontCameraId(cameraManager)
            } else {
                getBackCameraId(cameraManager)
            }

            val charateristics = cameraManager.getCameraCharacteristics(cameraId)
            val map = charateristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            previewSize = map!!.getOutputSizes(SurfaceTexture::class.java)[0]

            val size = Size(1280, 720)
            imageReader = ImageReader.newInstance(size.width, size.height, ImageFormat.JPEG, 1)
            imageReader.setOnImageAvailableListener(onImageAvailableListener, null)

            if(checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                return
            }
            cameraManager.openCamera(cameraId, stateCallback(), null)
        } catch (ex: Exception){
            Log.d(TAG, "onViewCreated: Camera ${ex.toString()} ")
        }
    }

    private fun stateCallback() = object: CameraDevice.StateCallback(){
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            createCameraPreviewSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
        }
    }

    private fun createCameraPreviewSession(){
        try{
            val texture = binding.cameraView.surfaceTexture!!
            val size = Size(720, 720)
            texture.setDefaultBufferSize(size.width, size.height)
            val surface = Surface(texture)

            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)

            cameraDevice!!.createCaptureSession(listOf(surface, imageReader.surface), object: CameraCaptureSession.StateCallback(){
                override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                    if (cameraDevice == null) return
                    captureSession = cameraCaptureSession
                    captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)
                    captureRequestBuilder.set(CaptureRequest.JPEG_QUALITY, 50.toByte()) // set this to 0-100

                    try {
                        captureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null)
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e(TAG, "Configuration change")
                }

            }, null)
        } catch (ex: Exception){
            Log.d(TAG, "onViewCreated: Camera ${ex.toString()} ")
        }
    }

    private fun closeCamera(){
        try {
            captureSession.close()
        } catch(ex: UninitializedPropertyAccessException){
            Log.d(TAG, "closeCamera: ${ex.toString()}")
        }

        try {
            cameraDevice.close()
        } catch(ex: UninitializedPropertyAccessException){
            Log.d(TAG, "closeCamera: ${ex.toString()}")
        }
    }

    private fun getFrontCameraId(cameraManager: CameraManager): String{
        try {
            for(cameraId in cameraManager.cameraIdList){
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if(facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT){
                    return cameraId
                }
            }
        } catch (ex: Exception){
            Log.d(TAG, "getFrontCameraId: ${ex.toString()}")
        }
        activity?.runOnUiThread {
            Toast.makeText(activity, "No Camera Found", Toast.LENGTH_LONG).show()
        }
        throw RuntimeException("No Front-Facing camera found")
    }

    private fun getBackCameraId(cameraManager: CameraManager): String{
        try {
            for(cameraId in cameraManager.cameraIdList){
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if(facing != null && facing == CameraCharacteristics.LENS_FACING_BACK){
                    return cameraId
                }
            }
        } catch (ex: Exception){
            Log.d(TAG, "getFrontCameraId: ${ex.toString()}")
        }
        activity?.runOnUiThread {
            Toast.makeText(activity, "No Camera Found", Toast.LENGTH_LONG).show()
        }
        throw RuntimeException("No Front-Facing camera found")
    }

    private fun captureStillPicture(){
        try{
            if(cameraDevice == null) return

            val captureBuilder = cameraDevice!!. createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder.addTarget(imageReader.surface)
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)
            val rotation = requireActivity().windowManager.defaultDisplay.rotation
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getJpegOrientation(cameraManager.getCameraCharacteristics(cameraId!!), rotation))
            captureSession?.apply {
                stopRepeating()
                abortCaptures()
                capture(captureBuilder.build(), captureCallback, null)
            }

        }catch (ex: CameraAccessException){
            Log.d(TAG, "captureStillPicture: ${ex.toString()}")
        }
    }

    private val captureCallback = object : CameraCaptureSession.CaptureCallback(){
        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ){
            super.onCaptureCompleted(session, request, result)
            createCameraPreviewSession()
        }
    }

    private val onImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        val image = reader.acquireLatestImage()
        val buffer: ByteBuffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        saveImageToInternalStorage(bytes)
        image.close()
    }
    
    private fun saveImageToInternalStorage(imageData: ByteArray){
        try{
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName: String
            if(isFrontCamera){
                fileName = "IMG_Selfie.jpg"

            } else {
                fileName = "IMG_Odometer.jpg"
            }
            val file = File(requireContext().filesDir, fileName)

            if (file.exists()) { file.delete() }

            val imageButton: ImageButton

            FileOutputStream(file).use { outputStream ->
                outputStream.write(imageData)
            }

            if(isFrontCamera){
                imageButton = binding.btnSelfieCamera
                loadImageFromStorage(requireContext(), file, imageButton)
            } else {
                imageButton = binding.btnOdometerCamera
                loadImageFromStorage(requireContext(), file, imageButton)
            }

        } catch (ex: IOException){
            Log.d(TAG, "saveImageToInternalStorage: ${ex.message}")
        }
    }

    private fun loadImageFromStorage(context: Context, file: File, imageButton: ImageButton) {
        var orientation = 0
        if(isFrontCamera){
            orientation = 270
        } else {
            orientation = 90
        }
        Glide.with(context)
            .load(file)
            .override(dpToPx(requireContext(), 150), dpToPx(requireContext(), 150))
            .transform(Rotate(orientation))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(imageButton)
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    private fun getJpegOrientation(cameraCharacteristics: CameraCharacteristics, deviceOrientation: Int): Int {
//        val sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION) ?: 0
//        val deviceOrientationOffset = (deviceOrientation + 45) / 90 * 90
//        val totalRotation = (sensorOrientation + deviceOrientationOffset) % 360
//        return if (totalRotation < 0) 360 - totalRotation else totalRotation
        return 0
    }

    private fun getFingerSpacing(event: MotionEvent): Float {
        try{
            val x = event.getX(0) - event.getX(1)
            val y = event.getY(0) - event.getY(1)
            return sqrt((x * x + y * y).toDouble()).toFloat()
        } catch (e: Exception){
            Log.d(TAG, "getFingerSpacing: ${e.toString()}")
            return(0.0F)
        }
    }

    private fun setZoomLevel() {
        try {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId!!)
            val maxZoom = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)
                ?: throw RuntimeException("Cannot get max zoom")
            val minZoom = 1f
            zoomLevel = zoomLevel.coerceIn(minZoom, maxZoom)

            val rect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
                ?: throw RuntimeException("Cannot get active array size")
            val cropWidth = rect.width() / zoomLevel
            val cropHeight = rect.height() / zoomLevel
            val xCenter = rect.centerX()
            val yCenter = rect.centerY()
            val cropRect = Rect(
                (xCenter - cropWidth / 2).roundToInt(),
                (yCenter - cropHeight / 2).roundToInt(),
                (xCenter + cropWidth / 2).roundToInt(),
                (yCenter + cropHeight / 2).roundToInt()
            )

            captureRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, cropRect)
            captureSession?.setRepeatingRequest(captureRequestBuilder.build(), null, null)
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Error setting zoom level: ${e.message}")
        }
    }


    private fun uploadImagesToCloud() {

        binding.uploadImageToCloud.isClickable = false

        val fileSelfie = File(requireContext().filesDir, "IMG_Selfie.jpg")
        val fileOdometer = File(requireContext().filesDir, "IMG_Odometer.jpg")
        var sessionType: String
        if(argEdit == "Log In"){
            sessionType = "login"
        } else {
            sessionType = "logoff"
        }

        val builder = AlertDialog.Builder(context)
        val inflater: LayoutInflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_dialog_style, null)

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

        var client = OkHttpClient()

        if(BuildConfig.DEBUG_ONLY_BUILD){

            Log.d(TAG, "Debug Build Bypass the SSL check")
            Log.d(TAG, "uploadImagesToCloud: ${ReceiverMediator.USER_TOKEN} ")

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

        var responBody: String = ""
        var formDataParts: MutableList<FormDataPart> = mutableListOf()
        val mediaType = "text/plain".toMediaType()
        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
//            .addFormDataPart("type","selfie")
            .addFormDataPart("status",sessionType)
            .addFormDataPart("selfie","selfie.jpg", File(fileSelfie.toString()).asRequestBody("image/jpeg".toMediaType()))
            .addFormDataPart("odometer","odometer.jpg", File(fileOdometer.toString()).asRequestBody("image/jpeg".toMediaType()))

        Log.d(TAG, "uploadImagesToCloud: Selfie: ${File(fileSelfie.toString()).length()}, Odometer:${File(fileOdometer.toString()).length()}")


        val body = requestBodyBuilder.build()

/*
        val requestBody = context?.contentResolver?.openInputStream(uriUri!!)?.use { input ->
                    input.readBytes().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                }

                val body = MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("files[0]",
                        formDataParts[0].name.toString(),
                        requestBody!!)
                    .build()
*/

        val request = Request.Builder()
            .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/log-tracking-files/files")
            .post(body)
            .addHeader("Authorization", "Bearer ${ReceiverMediator.USER_TOKEN}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                binding.uploadImageToCloud.isClickable = true
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        binding.uploadImageToCloud.isClickable = true
                        activity?.runOnUiThread {
                            Toast.makeText(activity, "File Upload Error", Toast.LENGTH_LONG).show()
                            dialog?.dismiss()
                        }

                        throw IOException("Unexpected code $response")
                    } else {
                        responBody = response.body?.string() ?: ""
                        Log.d(ExpenseSubmitFragment.TAG, "onResponse: ${responBody}")

                        activity?.runOnUiThread {
                            Toast.makeText(activity, "Session File uploaded: ${argEdit}", Toast.LENGTH_LONG).show()
//                            val bundle = Bundle().apply{
//                                putString(SIGN_IN_OUT, argEdit)
//                            }
                            //setFragmentResult(SignInOutFragment.TAG, bundle)
                            /*var bundle: Bundle = Bundle()
                            bundle.putInt(TAG,123)*/
                            //setFragmentResult(TAG,bundle)

                            dialogDismiss()

                        }
                        //dialog!!.dismiss()
                    }
                }
            }
        })
    }


/*    private fun submitTripDetails(data: TripSubmitReportSelectedTourPlan){
        val tripDetails: TripReportApprovedModel = TripTravelReportSubmitSharedFlow.tripTravelReportSubmitSharedFlow.value
        if (ReceiverMediator.USER_TOKEN.length > 8 && tripDetails.id != null) {


            val builder = AlertDialog.Builder(context)
            val inflater: LayoutInflater = layoutInflater
            val dialogView: View = inflater.inflate(R.layout.alert_dialog_style, null)

            *//*       val dialogTitle: TextView = dialogView.findViewById(R.id.dialogTitle)
                     val dialogMessage: TextView = dialogView.findViewById(R.id.dialogMessage)
                     val positiveButton: Button = dialogView.findViewById(R.id.positiveButton)
                     val negativeButton: Button = dialogView.findViewById(R.id.negativeButton)*//*

            builder.setView(dialogView)
            builder.setCancelable(false)
            dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog = builder.create()
            dialog?.show()


            val gson = Gson()
            val json = gson.toJson(data)

            var client = OkHttpClient()

            if(BuildConfig.DEBUG_ONLY_BUILD){

                Log.d(LoginFragment.TAG, "Debug Build Bypass the SSL check")

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

            val mediaType = "application/json".toMediaType()
            val body = json.toRequestBody(mediaType)

            val request = Request.Builder()
                .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/tours/report/${tripDetails.id}")
                .method("PUT", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${ReceiverMediator.USER_TOKEN}")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            Log.d(TripApprovedPlanDetails.TAG, "onResponse: ${response.body?.string()} ")
                            Log.d(TripApprovedPlanDetails.TAG, "onResponse: ${json}")
                            activity?.runOnUiThread {
                                Toast.makeText(context,"Error Submitting Trip Details", Toast.LENGTH_LONG).show()
                                dialog?.dismiss()
                            }
                        } else {
                            //responBody = response.body?.string() ?: ""
                            //println("addListToView" + responBody)
                            //addListToView(responBody)
                            // activity?.runOnUiThread {
                            //Toast.makeText(context,"Submitted Trip Details", Toast.LENGTH_LONG).show()
                            //binding.submitUpdateTripPlanPlan.visibility = View.INVISIBLE
                            //dialog?.dismiss()
                            var bundle: Bundle = Bundle()
                            bundle.putInt(TripApprovedPlanDetails.TAG,123)
                            setFragmentResult(TripApprovedPlanDetails.TAG,bundle)
                            //  dialog!!.dismiss()
                            //}
                        }
                    }
                }
            })
        }
    }*/



    private fun loginStatusCheck(){

        var responBody :String =""
        var client = OkHttpClient()

        if(BuildConfig.DEBUG_ONLY_BUILD){

            Log.d(LoginFragment.TAG, "loginCheck: Debug Build Bypass the SSL check")
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

        val request = Request.Builder()
            .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/gps-locations/status") //application/gps-locations/status"
            .addHeader("Authorization", "Bearer ${ReceiverMediator.USER_TOKEN}")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful){
//                        val bundle = Bundle()
//                        bundle.putInt(LOGIN_RESULT.toString(),400)
//                        setFragmentResult(LoginFragment.TAG,bundle)
//                        loginButton?.isClickable = true
//                        loginButton?.setBackgroundTintList(ColorStateList.valueOf(resources.getColor(R.color.green)))
                        Log.d(TAG, "onResponse: login_status: ${response.body}")
                        throw IOException("Unexpected code $response")
                    }

                    responBody = response.body?.string() ?: ""
                    Log.d(TAG, "onResponse: ${responBody}")

                    addLoginStatusToUI(responBody)
//                    val bundle = Bundle()
//                    bundle.putInt(LOGIN_RESULT.toString(),200)
//                    bundle.putString(RESPONSE_STRING, responBody.toString())
//                    bundle.putString(USER_EMAIL,username)
//                    RESPONSE_STRING = responBody
//                    setFragmentResult(LoginFragment.TAG,bundle)
//                    dialog!!.dismiss()
                }
            }
        })
    }

    private fun addLoginStatusToUI(status: String){
        try {
            val statusLogin: LoginStatusCheck = Gson().fromJson(status,LoginStatusCheck::class.java )
            activity?.runOnUiThread(){
                lifecycleScope.launch {
                    if(statusLogin.status == "loggedOut"){
                        dialog?.setTitle("Current Session Log In")
                        argEdit = "Log In"
                    } else {
                        dialog?.setTitle("Current Session Log Off")
                        argEdit = "Log Off"
                    }
                }
            }
        } catch (e: Exception){
            Log.d(TAG, "addLoginStatusToUI: ${e.printStackTrace()}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        closeCamera()
    }


    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }


    companion object {
        val TAG = SignInOutFragment::class.java.name
        var SIGN_IN_OUT: String = "Update..."
        val CAMERA_REQUEST_CODE = Random.nextInt()
        fun newInstance(arg1: String): SignInOutFragment {
            val fragment = SignInOutFragment()
            val args = Bundle()
            args.putString("ARG1_KEY",arg1)
            fragment.arguments = args
            return fragment
        }
    }
}

private data class LoginStatusCheck(
    var status: String? = null // "loggedOut"
)