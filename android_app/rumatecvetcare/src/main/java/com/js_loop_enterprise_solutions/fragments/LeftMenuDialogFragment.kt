package com.js_loop_enterprise_solutions.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.js_loop_enterprise_solutions.MainActivity
import com.js_loop_enterprise_solutions.MainActivity.Companion
import com.js_loop_erp.components.fragments.HomeViewModel
import com.js_loop_erp.rumatec_vetcare_erp.R
import com.js_loop_erp.rumatec_vetcare_erp.databinding.LeftMenuDialogFragmentBinding
import java.io.File
import java.io.FileOutputStream

class LeftMenuDialogFragment: DialogFragment(), IOnMenuItemClickListener {

    private var _binding: LeftMenuDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    val homeViewModel: HomeViewModel by activityViewModels()

    override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, com.js_loop_erp.components.R.style.DialogGreyTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        _binding = LeftMenuDialogFragmentBinding.inflate(inflater, container, false)

        val cachedFile = File(requireContext().cacheDir, "profile_image.jpg")
        if (cachedFile.exists()) {
            binding.profileImage.setImageURI(Uri.fromFile(cachedFile))
        }

        /*val toolbar = binding.root.findViewById<androidx.appcompat.widget.Toolbar>(R.id.dialog_toolbar)
        toolbar.title = "Dialog Title"

        toolbar.inflateMenu(R.menu.left_drawer_menu)

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home_nav_menu -> {
                    Toast.makeText(requireContext(), "Attendance Selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.trip_activity_nav_menu -> {
                    Toast.makeText(requireContext(), "Activity Selected", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
*/
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.menu_item_recycler_view)

        /*homeViewModel._userName_.observe(viewLifecycleOwner) {
            binding.nameText.text = it
        }*/
        homeViewModel._userId_.observe(viewLifecycleOwner) {
            binding.userIdText.text = it
        }

        binding.settingsButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        recyclerView.layoutManager = GridLayoutManager(context, 3)
        val mainMenuItems = listOf(
            MainMenuItem(
                id = 1,
                menuItemName = "Attendance",
                menuItemImage = R.drawable.attendance,
                isMenuItemAdmin = false
            ),
            MainMenuItem(
                id = 2,
                menuItemName = "Activity",
                menuItemImage = R.drawable.activity,
                isMenuItemAdmin = false
            ),
            MainMenuItem(
                id = 3,
                menuItemName = "Leave",
                menuItemImage = R.drawable.leave,
                isMenuItemAdmin = false
            ),
            MainMenuItem(
                id = 4,
                menuItemName = "Trip Plan",
                menuItemImage = R.drawable.trip_plan,
                isMenuItemAdmin = false
            ),
            MainMenuItem(
                id = 5,
                menuItemName = "Reporting",
                menuItemImage = R.drawable.approve_reporting,
                isMenuItemAdmin = false
            ),
            MainMenuItem(
                id = 6,
                menuItemName = "Approve Trip",
                menuItemImage = R.drawable.approve_trip,
                isMenuItemAdmin = !MainActivity.USER_PERMISSION
            ),
            MainMenuItem(
                id = 7,
                menuItemName = "Approve Leave",
                menuItemImage = R.drawable.approve_leave,
                isMenuItemAdmin = !MainActivity.USER_PERMISSION
            ),
            MainMenuItem(
                id = 8,
                menuItemName = "Login Status",
                menuItemImage = R.drawable.checklist,
                isMenuItemAdmin = !MainActivity.USER_PERMISSION
            ),
            MainMenuItem(
                id = 9,
                menuItemName = "Sign Out",
                menuItemImage = com.js_loop_erp.components.R.drawable.baseline_add_24,
                isMenuItemAdmin = false
            )/*,
            MainMenuItem(
                id = 10,
                menuItemName = "Profile",
                menuItemImage = com.js_loop_erp.components.R.drawable.baseline_add_24,
                isMenuItemAdmin = false
            )*/
        )
        val adapter: MenuLayoutAdapter = MenuLayoutAdapter( mainMenuItems.filter { !it.isMenuItemAdmin }, this@LeftMenuDialogFragment)
        recyclerView.adapter = adapter
    }

    override fun onItemClick(item: MainMenuItem) {
        when(item.menuItemName){
            "Attendance"->{
                val bundle = Bundle()
                bundle.putInt(MENU_ITEM_CLICKED,MenuItemClick.ATTENDANCE.ordinal)
                setFragmentResult(LeftMenuDialogFragment.TAG,bundle)
                dialog!!.dismiss()
            }
            "Activity"->{
                val bundle = Bundle()
                bundle.putInt(MENU_ITEM_CLICKED,MenuItemClick.ACTIVITY.ordinal)
                setFragmentResult(LeftMenuDialogFragment.TAG,bundle)
                dialog!!.dismiss()
            }
            "Leave"->{
                val bundle = Bundle()
                bundle.putInt(MENU_ITEM_CLICKED,MenuItemClick.LEAVE.ordinal)
                setFragmentResult(LeftMenuDialogFragment.TAG,bundle)
                dialog!!.dismiss()
            }
            "Trip Plan"->{
                val bundle = Bundle()
                bundle.putInt(MENU_ITEM_CLICKED,MenuItemClick.TRIP_PLAN.ordinal)
                setFragmentResult(LeftMenuDialogFragment.TAG,bundle)
                dialog!!.dismiss()
            }
            "Reporting"->{
                val bundle = Bundle()
                bundle.putInt(MENU_ITEM_CLICKED,MenuItemClick.REPORTING.ordinal)
                setFragmentResult(LeftMenuDialogFragment.TAG,bundle)
                dialog!!.dismiss()
            }
            "Approve Trip"->{
                val bundle = Bundle()
                bundle.putInt(MENU_ITEM_CLICKED,MenuItemClick.APPROVE_TRIP.ordinal)
                setFragmentResult(LeftMenuDialogFragment.TAG,bundle)
                dialog!!.dismiss()
            }
            "Approve Leave"->{
                val bundle = Bundle()
                bundle.putInt(MENU_ITEM_CLICKED,MenuItemClick.APPROVE_LEAVE.ordinal)
                setFragmentResult(LeftMenuDialogFragment.TAG,bundle)
                dialog!!.dismiss()
            }
            "Login Status"->{
                val bundle = Bundle()
                bundle.putInt(MENU_ITEM_CLICKED,MenuItemClick.LOGIN_STATUS.ordinal)
                setFragmentResult(LeftMenuDialogFragment.TAG,bundle)
                dialog!!.dismiss()
            }
            "Sign Out"->{
                val bundle = Bundle()
                bundle.putInt(MENU_ITEM_CLICKED,MenuItemClick.SIGN_OUT.ordinal)
                setFragmentResult(LeftMenuDialogFragment.TAG,bundle)
                dialog!!.dismiss()
            }

            else->{
                Log.d(TAG, "onStart: InvalidMenuItemClick")
            }
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.profileImage.setImageURI(it)
            saveImageToCache(it)
        }
    }

    private fun saveImageToCache(imageUri: Uri) {
        val inputStream = requireContext().contentResolver.openInputStream(imageUri)
        val file = File(requireContext().cacheDir, "profile_image.jpg")
        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        Log.d("ProfileDialog", "Saved image to cache: ${file.absolutePath}")
    }

    companion object {
        val TAG = LeftMenuDialogFragment::class.java.simpleName
        val MENU_ITEM_CLICKED: String = "MENU_ITEM_CLICKED"
    }
}

interface IOnMenuItemClickListener {
    fun onItemClick(item: MainMenuItem)
}