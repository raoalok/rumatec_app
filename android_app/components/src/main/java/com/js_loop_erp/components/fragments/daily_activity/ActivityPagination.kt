package com.js_loop_erp.components.fragments.daily_activity

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
import  com.js_loop_erp.components.databinding.ActivityPaginationBinding

class ActivityPagination : DialogFragment(), View.OnKeyListener{
    private lateinit var binding : ActivityPaginationBinding
    private lateinit var pagerAdapter: ActivityPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        isCancelable = false
        binding = ActivityPaginationBinding.inflate(inflater)
        dialog?.setTitle("Activity")
        pagerAdapter = ActivityPagerAdapter()
        getDialog()?.setCanceledOnTouchOutside(false)

        return binding.root
    }

    override fun onViewCreated(view:View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        setChildFragmentResultListener(ACTIVITY_SUBMIT_FRAGMENT, :: setChildFragmentResultAndDismiss)

        view.rootView.findViewTreeOnBackPressedDispatcherOwner()?.onBackPressedDispatcher?.addCallback{
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        return dialog
    }

    enum class ActivityPage{
        ActivitySubmitFragment,
        ActivityUpdateFragment;

        companion object {
            fun getOrNull(ordinal: Int): ActivityPage?{
                return values().getOrNull(ordinal)
            }
        }
    }

    private inner class ActivityPagerAdapter: FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int {
            return ActivityPage.values().size
        }

        override fun getItem(position: Int): Fragment = when (ActivityPage.getOrNull(position)) {
            ActivityPage.ActivitySubmitFragment -> ActivityListAdd()
            ActivityPage.ActivityUpdateFragment -> ActivityUpdate()
            null -> throw IllegalArgumentException("Pagination fragment position error")
        }

        override fun getPageTitle(position: Int): String = when (ActivityPage.getOrNull(position)) {
            ActivityPage.ActivitySubmitFragment -> "Activity Add"
            ActivityPage.ActivityUpdateFragment -> "Activity List"
            null -> throw IllegalArgumentException("Pagination fragment position error")
            else -> {
                throw IllegalArgumentException("Pagination fragment position error")
            }
        }
    }
        private fun setChildFragmentResultListener(
            key: String,
            listener: (String, Bundle) -> Unit
        ) {
            childFragmentManager.setFragmentResultListener(key, viewLifecycleOwner, listener)
        }

        private fun setChildFragmentResultAndDismiss(key: String, result: Bundle) {
            dismiss()
        }

        override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
            return true
        }

        companion object {
            val TAG: String = ActivityPagination::class.java.name
            val ACTIVITY_SUBMIT_FRAGMENT = "activitySubmitFragment"
        }

}