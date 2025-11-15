package com.js_loop_erp.components.fragments.tripTourPlan

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.findViewTreeOnBackPressedDispatcherOwner
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import com.js_loop_erp.components.BuildConfig
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.databinding.TripPlanDetailsViewFragmentBinding
import com.js_loop_erp.components.fragments.LoginFragment
import  com.js_loop_erp.components.fragments.TripPlanFragment
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.security.SecureRandom
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class TripPlanDetailsViewFragment: DialogFragment() {
    private var _binding: TripPlanDetailsViewFragmentBinding? = null
    private val binding get() = _binding!!

    private var tripId: String = "0"
    var responBody: String = ""

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogGreyTheme)
        tripId = (arguments?.getString("ARG1_KEY")?.toInt() ?: 0).toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = TripPlanDetailsViewFragmentBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Trip Details")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        view.rootView.findViewTreeOnBackPressedDispatcherOwner()?.onBackPressedDispatcher?.addCallback {
            dismiss()
        }

        if(tripId.toInt() >0){
            getTripPlanDetailsFromServer()
        } else {
            Log.d(TAG, "onViewCreated: tripId: ${tripId}")
        }

        binding.tripPlanDeatils.text = ""
    }

    fun getTripPlanDetailsFromServer(){
            if(ReceiverMediator.USER_TOKEN.length > 8) {

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



                val request = Request.Builder()
                    //.url(" http://65.0.61.137/api/application/tours/list")
                    .url("${ReceiverMediator.SERVER_SYNC_URI}/api/application/tours/view/${tripId}")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Authorization", "Bearer ${ReceiverMediator.USER_TOKEN}")
                    .build()

                client.newCall(request).enqueue(object: Callback {
                    override fun onFailure(call: Call, e: IOException){
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response){
                        response.use{
                            if(!response.isSuccessful){
                                Log.d(TAG, "onResponse: error")

                            } else {
                                responBody= response.body?.string() ?: ""
                                Log.d(TAG, "onResponse: ${responBody}")
                                println(responBody)
                                addDetailsToView()
                            }
                        }
                    }
                })
            }
    }

    fun addDetailsToView() {

        try {
            activity?.runOnUiThread(Runnable {
                val trip: TripPlanDataDetailsData = Gson().fromJson(responBody, TripPlanDataDetailsData::class.java)

                val companionList = mutableListOf<String>()
                val doctorsList = mutableListOf<String>()
                val partiesList = mutableListOf<String>()
                val hospitalList = mutableListOf<String>()
                val petShopsList = mutableListOf<String>()
                val institutesList = mutableListOf<String>()


                trip.companions.forEachIndexed { index, companion ->
                    println("TripPlanDetailsViewFragment Companion ${index + 1}: ${companion.name}")
                    companionList.add(companion.name.toString())
                }

                trip.doctors.forEachIndexed { index, doctors ->
                    println("TripPlanDetailsViewFragment Companion ${index + 1}: ${doctors.name}")
                    doctorsList.add(doctors.name.toString())
                }

                trip.parties.forEachIndexed { index, parties ->
                    println("TripPlanDetailsViewFragment Companion ${index + 1}: ${parties.name}")
                    partiesList.add(parties.name.toString())
                }

                trip.hospitals.forEachIndexed { index, hospitals ->
                    println("TripPlanDetailsViewFragment Companion ${index + 1}: ${hospitals.name}")
                    hospitalList.add(hospitals.name.toString())
                }

                trip.petshops.forEachIndexed { index, petshops ->
                    println("TripPlanDetailsViewFragment Companion ${index + 1}: ${petshops.name}")
                    petShopsList.add(petshops.name.toString())
                }

                trip.institutes.forEachIndexed { index, institutes ->
                    println("TripPlanDetailsViewFragment Companion ${index + 1}: ${institutes.name}")
                    institutesList.add(institutes.name.toString())
                }


                val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")  //2027-01-24T06:26:11.962Z
/*
                val date = "<b>Date:</b> ${LocalDateTime.parse(trip.date, pattern).toLocalDate().toString()}"
                val fromArea = "<b>fromArea:</b> ${LocalDateTime.parse(trip.fromArea, pattern).toLocalDate().toString()}"
                val toArea = "<b>toArea:</b> ${LocalDateTime.parse(trip.toArea, pattern).toLocalDate().toString()}"
                val mode = "<b>mode:</b> ${LocalDateTime.parse(trip.mode, pattern).toLocalDate().toString()}"

                val tripCompanionList = "<br><br><b>Companions:</b> ${companionList.joinToString(separator = " "){it}}"
                val tripDoctorsList = "<br><br><b>Doctors:</b> ${doctorsList.joinToString(separator = " ")}"
                val tripPartiesList = "<br><br><b>Parties:</b> ${partiesList.joinToString(separator = " ")}"
                val tripHospitalList = "<br><br><b>Hospitals:</b> ${hospitalList.joinToString(separator = " ")}"
                val tripPetShopsList = "<br><br><b>Pet Shops:</b> ${petShopsList.joinToString(separator = " ")}"
                val tripInstitutesList = "<br><br><b>Institutes:</b> ${institutesList.joinToString(separator = " ")}"
*/

                val combinedText = """
                            <b>Date:</b> ${LocalDateTime.parse(trip.date, pattern).toLocalDate().toString()}<br><br>
                            <b>From:</b> ${trip.fromArea}<br><br>
                            <b>To:</b> ${trip.toArea}<br><br>
                            <b>Mode:</b> ${trip.mode}<br><br>
                            <b>Companions:</b> ${companionList.joinToString(separator = ", ")}<br><br>
                            <b>Doctors:</b> ${doctorsList.joinToString(separator = ", ")}<br><br>
                            <b>Parties:</b> ${partiesList.joinToString(separator = ", ")}<br><br>
                            <b>Hospitals:</b> ${hospitalList.joinToString(separator = ", ")}<br><br>
                            <b>Pet Shops:</b> ${petShopsList.joinToString(separator = ", ")}<br><br>
                            <b>Institutes:</b> ${institutesList.joinToString(separator = ", ")}<br><br>
                            <b>Remark:</b> ${trip.remark}                      
                            """.trimIndent()

                binding.tripPlanDeatils.text = Html.fromHtml(combinedText, Html.FROM_HTML_MODE_LEGACY)

            })
        } catch (e: Exception) {
            Log.d(TripPlanFragment.TAG, "filter: $e")
        }

    }

    companion object{
        val TAG = TripPlanDetailsViewFragment::class.java.name

        fun newInstance(tripId: String): TripPlanDetailsViewFragment {
            val fragment = TripPlanDetailsViewFragment()
            val args = Bundle()
            args.putString("ARG1_KEY", tripId)
            fragment.arguments = args
            return fragment
        }
    }
}

data class TripPlanDataDetailsData(
    val id: Int? = 0,
    val routeId: Int? = 0,
    val fromAreaId: Int? = 0,
    val toAreaId: Int? = 0,
    val mode: String? = "",
    val date: String? = "",
    val remark: String? = "",
    val createdAt: String? = "",
    val createdBy: String? = "",
    val updatedAt: String? = "",
    val updatedBy: String? = "",
    val deletedAt: String? = null,
    val deletedBy: String? = null,
    val approveAt: String? = null,
    val approveBy: String? = null,
    val approveRemark: String? = null,
    val rejectAt: String? = null,
    val rejectBy: String? = null,
    val rejectRemark: String? = null,
    val route: String? = "",
    val fromArea: String? = "",
    val toArea: String? = "",
    val companions: List<Companion> = listOf(),
    val doctors: List<Doctor> = listOf(),
    val parties: List<Party> = listOf(),
    val hospitals: List<Hospital> = listOf(),
    val petshops: List<Petshop> = listOf(),
    val institutes: List<Institute> = listOf()
)

data class Companion(
    val id: Int? = 0,
    val name: String? = "",
    val isSelected: Boolean? = false
)

data class Doctor(
    val id: Int? = 0,
    val name: String? = "",
    val isSelected: Boolean? = false
)

data class Party(
    val id: Int? = 0,
    val name: String? = "",
    val isSelected: Boolean? = false
)

data class Hospital(
    val id: Int? = 0,
    val name: String? = "",
    val isSelected: Boolean? = false
)

data class Petshop(
    val id: Int? = 0,
    val name: String? = "",
    val isSelected: Boolean? = false
)

data class Institute(
    val id: Int? = 0,
    val name: String? = "",
    val isSelected: Boolean? = false
)
