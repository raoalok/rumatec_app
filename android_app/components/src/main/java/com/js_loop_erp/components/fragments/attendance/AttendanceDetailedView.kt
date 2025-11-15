package com.js_loop_erp.components.fragments.attendance

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.findViewTreeOnBackPressedDispatcherOwner
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.databinding.AttendanceDetailViewBinding
import  com.js_loop_erp.components.fragments.TripPlanFragment
import  com.js_loop_erp.components.fragments.tripTourPlan.TripPlanDataDetailsData
import com.js_loop_erp.components.receiverMediator.ReceiverMediator
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class AttendanceDetailedView : DialogFragment() {
    private var _binding: AttendanceDetailViewBinding? = null
    private val binding get() = _binding!!

    private var tripId: String = "0"
    var responBody: String = ""
    lateinit var attendanceModel: AttendanceListModel

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogGreyTheme)
        //tripId = args.getParcelable<AttendanceListModel>("ARG_TRIP")
        arguments?.let { args ->
            attendanceModel = args.parcelable<AttendanceListModel>("ARG_TRIP") ?: AttendanceListModel()
            // Use attendanceModel here
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = AttendanceDetailViewBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Attendance Details")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        view.rootView.findViewTreeOnBackPressedDispatcherOwner()?.onBackPressedDispatcher?.addCallback {
            dismiss()
        }

        binding.attendanceDeatilsDate.text = attendanceModel.date
        binding.attendanceDeatilsRoute.text = attendanceModel.route
        binding.attendanceDeatilsActivity.text = attendanceModel.activity
        binding.attendanceDeatilsRemark.text = attendanceModel.remark
    }

    fun getTripPlanDetailsFromServer(){
        if(ReceiverMediator.USER_TOKEN.length > 8) {
            val client = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                //.url(" http://65.0.61.137/api/application/tours/list")
                .url("http://65.0.61.137/api/application/tours/view/${tripId}")
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

                //binding.tripPlanDeatils.text = Html.fromHtml(combinedText, Html.FROM_HTML_MODE_LEGACY)

            })
        } catch (e: Exception) {
            Log.d(TripPlanFragment.TAG, "filter: $e")
        }

    }

    inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelable(key) as? T
    }

    companion object{
        val TAG = AttendanceDetailedView::class.java.name

        fun newInstance(tripId: AttendanceListModel): AttendanceDetailedView {
            val fragment = AttendanceDetailedView()
            val args = Bundle()
            args.putParcelable("ARG_TRIP", tripId)
            fragment.arguments = args
            return fragment
        }
    }
}
