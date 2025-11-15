package com.js_loop_erp.components.fragments.attendance

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.findViewTreeOnBackPressedDispatcherOwner
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.js_loop_erp.components.R
import com.js_loop_erp.components.databinding.AttendancePaginationBinding
import com.js_loop_erp.components.fragments.attendance.AttendanceList
import com.js_loop_erp.components.fragments.attendance.UpdateAttendanceFragment

class AttendancePagination : DialogFragment() {
    private lateinit var binding: AttendancePaginationBinding
    private lateinit var pagerAdapter: AttendancePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }
    override fun onCreateView(inflater:LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?{
        isCancelable = false
        binding = AttendancePaginationBinding.inflate(inflater)
        dialog?.setTitle("Attendance")
        pagerAdapter = AttendancePagerAdapter()
        getDialog()?.setCanceledOnTouchOutside(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pagerAdapter = AttendancePagerAdapter()
        binding.viewPager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        setChildFragmentResultListener(Attendance_SUBMIT_FRAGMENT,:: setChildFragmentResultAndDismiss)

        view.rootView.findViewTreeOnBackPressedDispatcherOwner()?.onBackPressedDispatcher?.addCallback {
            dismiss()
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        return dialog
    }

    enum class AttendancePage {
        AttendanceSubmitFragment,
        AttendanceUpdateFragment;

        companion object {
            fun getOrNull(ordinal: Int): AttendancePage? = values().getOrNull(ordinal)
        }
    }

    private inner class AttendancePagerAdapter : FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount(): Int = AttendancePage.values().size

        override fun getItem(position: Int): Fragment = when (AttendancePage.getOrNull(position)) {
            AttendancePage.AttendanceSubmitFragment -> AttendanceList()
            AttendancePage.AttendanceUpdateFragment -> UpdateAttendanceFragment()
            null -> throw IllegalArgumentException("Pagination fragment getItem Error")
        }

        override fun getPageTitle(position: Int): CharSequence = when (AttendancePage.getOrNull(position)) {
            AttendancePage.AttendanceSubmitFragment -> "Attendance Create"
            AttendancePage.AttendanceUpdateFragment -> "Attendance Update"
            null -> throw IllegalArgumentException("Position Invalid")
        }
    }

    private fun setChildFragmentResultListener(key:String, listener:(String, Bundle)-> Unit){
        childFragmentManager.setFragmentResultListener(key, viewLifecycleOwner, listener)
    }

    private fun setChildFragmentResultAndDismiss(key:String, result: Bundle){
        dismiss()
    }

    companion object {
        val TAG: String = AttendancePagination::class.java.name
        val Attendance_SUBMIT_FRAGMENT = "attendanceSubmitFragment"
    }
}