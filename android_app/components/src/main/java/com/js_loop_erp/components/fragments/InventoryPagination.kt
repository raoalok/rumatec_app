package com.js_loop_erp.components.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.findViewTreeOnBackPressedDispatcherOwner
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.databinding.InventoryPaginationBinding


class InventoryPagination: DialogFragment(), View.OnKeyListener {
    private lateinit var binding: InventoryPaginationBinding
    private lateinit var pagerAdapter: InventoryPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {

        isCancelable = false
        binding = InventoryPaginationBinding.inflate(inflater)
        dialog?.setTitle("Sample Inventory")

        pagerAdapter = InventoryPagerAdapter()

        getDialog()?.setCanceledOnTouchOutside(false)

        return binding.root
    }

    override fun onViewCreated(view:View, savedInstanceState:Bundle?){
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        //setChildFragmentResultListener()
        setChildFragmentResultListener(INVENTORY_UPDATE_FRAGMENT,::setFragmentResultAndDismiss)

        view.rootView.findViewTreeOnBackPressedDispatcherOwner()?.onBackPressedDispatcher?.addCallback {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Set your animation style here
        return dialog
    }

    enum class InventoryPage {
        InventoryFragment,
        InventoryFragment1,
        SubmittedInventoryCheckEditFragment;

        companion object {
            fun getOrNull(ordinal: Int): InventoryPage? {
                return values().getOrNull(ordinal)
            }
        }
    }

    private inner class InventoryPagerAdapter:FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
        override fun getCount(): Int {
            return InventoryPage.values().size
        }

        override fun getItem(position: Int): Fragment  = when (InventoryPage.getOrNull(position)){
            InventoryPage.InventoryFragment -> InventoryFragment()
            InventoryPage.InventoryFragment1 -> InventoryUpdateFragment()
            InventoryPage.SubmittedInventoryCheckEditFragment -> SubmittedInventoryCheckEditFragment()
            null -> throw IllegalArgumentException("Pagination fragment getItem error")
        }

        override fun getPageTitle(position: Int): String = when (InventoryPage.getOrNull(position)){
            InventoryPage.InventoryFragment -> "Inventory"
            InventoryPage.InventoryFragment1 -> "Distribution"
            InventoryPage.SubmittedInventoryCheckEditFragment -> "Distributed"
            null -> throw IllegalArgumentException( "Position invalid")
        }

    }

    private fun setChildFragmentResultListener(key:String, listener:(String, Bundle) ->Unit){
        childFragmentManager.setFragmentResultListener(key, viewLifecycleOwner, listener)
    }

    private fun setFragmentResultAndDismiss(key:String, result: Bundle){
        dismiss()
    }


    companion object{
        val TAG: String = InventoryPagination::class.java.name
        val INVENTORY_UPDATE_FRAGMENT = "inventoryUpdateFragment"
        val INVENTORY_UPDATE_FRAGMENT_DISMISS: String = "inventoryUpdateFragmentDismiss"
    }

    override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
        // TODO("Not yet implemented")
        Log.d(InventoryFragment.TAG, "onKey: dkfsdjfskdjfd")
        return true
    }
}
