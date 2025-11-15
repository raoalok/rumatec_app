package com.js_loop_erp.components.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.findViewTreeOnBackPressedDispatcherOwner
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.databinding.ExpensePaginationBinding

class ExpensePagination: DialogFragment() {

    private lateinit var binding: ExpensePaginationBinding
    private lateinit var pagerAdapter: ExpensePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        isCancelable = false
        binding = ExpensePaginationBinding.inflate(inflater)
        dialog?.setTitle("Expense")

        pagerAdapter = ExpensePagerAdapter()
        getDialog()?.setCanceledOnTouchOutside(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        setChildFragmentResultListener(EXPENSE_UPDATE_FRAGMENT, ::setFragmentResultAndDismiss)

        view.rootView.findViewTreeOnBackPressedDispatcherOwner()?.onBackPressedDispatcher?.addCallback {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Set your animation style here
        return dialog
    }

    enum class ExpenseTabs {
        ExpenseSubmit,
        ExpenseEdit;

        companion object {
            fun getOrNull(ordinal: Int): ExpenseTabs? {
                return values().getOrNull(ordinal)
            }
        }
    }

    private inner class ExpensePagerAdapter:FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
        override fun getCount(): Int{
            return ExpenseTabs.values().size
        }

        override fun getItem(position: Int): Fragment = when (ExpenseTabs.getOrNull(position)){
            ExpenseTabs.ExpenseSubmit -> ExpenseSubmitFragment()
            ExpenseTabs.ExpenseEdit   -> ExpenseEditFragment()
            null -> throw IllegalArgumentException("Pagination fragment getItem Error")
        }

        override fun getPageTitle(position: Int): String = when (ExpenseTabs.getOrNull(position)){
            ExpenseTabs.ExpenseSubmit -> "Expense Submit"
            ExpenseTabs.ExpenseEdit   ->   "Submitted Expense"
            null -> throw IllegalArgumentException("Position Invalid")
        }
    }

    private fun setChildFragmentResultListener(key: String, listener:(String, Bundle)-> Unit){
        childFragmentManager.setFragmentResultListener(key, viewLifecycleOwner, listener)
    }

    private fun setFragmentResultAndDismiss(key:String, result: Bundle){
        dismiss()
    }

    companion object {
        val TAG = ExpensePagination::class.java.name
        val EXPENSE_UPDATE_FRAGMENT = "expenseUpdateFragment"
    }
}