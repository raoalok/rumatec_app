package com.js_loop_erp.components.fragments.attendance

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.adapter.AttendanceListAdapter
import  com.js_loop_erp.components.data_flow.AttendanceListDataFlow
import  com.js_loop_erp.components.databinding.AttendanceListBinding
import  com.js_loop_erp.components.fragments.tripReportSubmit.TripApprovedPlanDetails
import  com.js_loop_erp.components.fragments.tripTourPlan.TripPlanFormFragmnet
import kotlinx.coroutines.launch
import kotlin.random.Random

class AttendanceList: DialogFragment(), AttendanceListI  {
    private var _binding: AttendanceListBinding? = null
    private val binding get() = _binding!!

    private lateinit var attendanceSubmit: Button
    private lateinit var attendanceRecyclerView: RecyclerView
    private lateinit var attendanceListAdapter: AttendanceListAdapter
    private lateinit var createNewAttendance: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogGreyTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        _binding = AttendanceListBinding.inflate(inflater, container, false)
        getDialog()?.setTitle("Attendance List")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        attendanceSubmit = view.findViewById<Button>(R.id.attendance_submit_button)
        attendanceRecyclerView = view.findViewById<RecyclerView>(R.id.attendance_list_recycler_view)
        createNewAttendance = view.findViewById<FloatingActionButton>(R.id.create_new_attendance)

        attendanceRecyclerView.layoutManager = LinearLayoutManager(context)

        val attendanceListData = ArrayList<AttendanceListModel>()
        attendanceListAdapter = AttendanceListAdapter(this, attendanceListData)
        attendanceRecyclerView.adapter = attendanceListAdapter

        lifecycleScope.launch {
            AttendanceListDataFlow.updateData.collect { updates ->
                Log.d(TripApprovedPlanDetails.TAG, "onViewCreated: .....")
                val data = AttendanceListDataFlow._attendanceList.value
                if (data.size > 0) {
                    activity?.runOnUiThread(Runnable {
                        attendanceListAdapter.filterList(data.toList())
                    })
                } else {
                    attendanceListAdapter.filterList(emptyList())
                }
            }
        }

        createNewAttendance.setOnClickListener {
            val childDialog = AttendanceCreate()
            childDialog.show(childFragmentManager, AttendanceCreate.TAG)
        }

    }
    override fun onItemClick(position: Int) {
        AttendanceListDataFlow.removeAttendanceItemById(position)
        AttendanceListDataFlow.updateAllData(Random.nextInt(1,100))
    }

    override fun onItemLongClick(item: AttendanceListModel) {
        val childDialog = AttendanceDetailedView.newInstance(item)
        childDialog.show(childFragmentManager, "AttendanceDetailedView")
    }
}

interface AttendanceListI {
    fun onItemClick(position: Int)
    fun onItemLongClick(item: AttendanceListModel)
}