package com.js_loop_erp.components.fragments

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
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.databinding.LeavePaginationBinding
import java.lang.IllegalArgumentException

class LeavePagination : DialogFragment(), View.OnKeyListener {
    private lateinit var binding: LeavePaginationBinding
    private lateinit var pagerAdapter: LeavePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?{
        isCancelable = false
        binding = LeavePaginationBinding.inflate(inflater)
        dialog?.setTitle("Leave")
        pagerAdapter = LeavePagerAdapter()
        getDialog()?.setCanceledOnTouchOutside(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        setChildFragmentResultListener(LEAVE_SUBMIT_FRAGMENT,:: setChildFragmentResultAndDismiss)

        view.rootView.findViewTreeOnBackPressedDispatcherOwner()?.onBackPressedDispatcher?.addCallback {
            dismiss()
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        return dialog
    }

    enum class LeavePage{
        LeaveSubmitFragment,
        LeaveUpdateFragment;

        companion object {
            fun getOrNull(ordinal: Int): LeavePage? {
                return values().getOrNull(ordinal)
            }
        }
    }

    private inner class LeavePagerAdapter: FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
        override fun getCount():Int {
            return LeavePage.values().size
        }

        override fun getItem(position: Int): Fragment = when (LeavePage.getOrNull(position)){
            LeavePage.LeaveSubmitFragment -> LeaveCreateFragment()
            LeavePage.LeaveUpdateFragment -> UpdateLeaveFragment()
            null -> throw IllegalArgumentException("Pagination fragment getItem Error")
            else -> {throw IllegalArgumentException("Position Invalid")}
        }

        override fun getPageTitle(position: Int): String = when (LeavePage.getOrNull(position)){
            LeavePage.LeaveSubmitFragment -> "Leave Create"
            LeavePage.LeaveUpdateFragment -> "Leave Update"
            null -> throw IllegalArgumentException("Position Invalid")
            else -> {throw IllegalArgumentException("Position Invalid")}
        }

    }

    private fun setChildFragmentResultListener(key:String, listener:(String, Bundle)-> Unit){
        childFragmentManager.setFragmentResultListener(key, viewLifecycleOwner, listener)
    }

    private fun setChildFragmentResultAndDismiss(key:String, result: Bundle){
        dismiss()
    }

    override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean{
        return true
    }

    companion object {
        val TAG: String = LeavePagination::class.java.name
        val LEAVE_SUBMIT_FRAGMENT = "LeaveSubmitFragment"
    }
}