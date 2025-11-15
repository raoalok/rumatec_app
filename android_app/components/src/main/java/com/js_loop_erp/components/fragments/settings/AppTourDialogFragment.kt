package com.js_loop_erp.components.fragments.settings

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.js_loop_erp.components.R
import com.js_loop_erp.components.adapter.AppTourAdapter
import com.js_loop_erp.components.databinding.DialogAppTourBinding
import com.js_loop_erp.components.fragments.SignInOutFragment

class AppTourDialogFragment : DialogFragment() {

    private var _binding: DialogAppTourBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAppTourBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = listOf(
            AppTourAdapter.TourItem(R.drawable.delete_remove_uncheck_svgrepo_com, "Battery optimization",
                "The App need to run continuously in the background for reliable tracking and alerts. Disabling battery optimization ensures the app is not killed by the system.",
                "⚠ If you don’t allow this, background tracking and notifications may stop when the phone goes idle."),
            AppTourAdapter.TourItem(R.drawable.baseline_add_24, "Allow Precise Background Location",
                "The Application need precise location updates even when the app is not open. This is required for accurate tracking and timely alerts.",
                "⚠ Without this, features depending on continuous location will not function correctly (e.g., live tracking, safety alerts)."),
            AppTourAdapter.TourItem(R.drawable.check_in_svgrepo_com, "Camera Permission",
                "The camera is required to capture photos/videos directly within the app.",
                "⚠ Without camera permission, you won’t be able to scan, capture or upload live images from within the app.")
        )

        binding.viewPager.adapter = AppTourAdapter(items)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val isLastPage = position == (binding.viewPager.adapter?.itemCount ?: 0) - 1
                binding.buttonContinue.visibility = if (isLastPage) View.VISIBLE else View.GONE
            }
        })

        binding.buttonContinue.setOnClickListener {
            var bundle = Bundle()
            bundle.apply {
                putInt("APP_TOUR_DISMISS",0)
            }
            setFragmentResult(AppTourDialogFragment.TAG, bundle)
            dismiss()
        }

    }


    override fun onStart() {
        super.onStart()
        //setStyle(STYLE_NORMAL, R.style.DialogThemeNoTitle)

        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawableResource(android.R.color.darker_gray) // Or custom background
            setWindowAnimations(R.style.DialogAnimation) // Optional animation
            setGravity(Gravity.CENTER)
        }
    }

    companion object{
        val TAG =  AppTourDialogFragment::class.java.name
    }
}
