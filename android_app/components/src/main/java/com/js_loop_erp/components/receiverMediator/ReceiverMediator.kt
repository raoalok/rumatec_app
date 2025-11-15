package com.js_loop_erp.components.receiverMediator

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.js_loop_enterprise.common_mediator.Receiver
import com.js_loop_erp.components.fragments.CnfFragment
import com.js_loop_erp.components.fragments.ExpensePagination
import com.js_loop_erp.components.fragments.InventoryFragment
import com.js_loop_erp.components.fragments.LeavePagination
import com.js_loop_erp.components.fragments.ProductListFragment
import com.js_loop_erp.components.fragments.SaleInvoiceFragment
import com.js_loop_erp.components.fragments.SignInOutFragment
import com.js_loop_erp.components.fragments.TripPlanFragment
import com.js_loop_erp.components.fragments.TripReportFragment
import com.js_loop_erp.components.fragments.access_controlled.CheckUserLoginStatus
import com.js_loop_erp.components.fragments.access_controlled.LeaveApproveReject
import com.js_loop_erp.components.fragments.access_controlled.TripApproveFragment
import com.js_loop_erp.components.fragments.attendance.AttendancePagination
import com.js_loop_erp.components.fragments.daily_activity.ActivityPagination
import com.js_loop_erp.components.fragments.settings.SettingsFragment

class ReceiverMediator(private val fragmentManager: FragmentManager, token :String): Receiver {
    override fun receiveMessage(message: String) {
        when(message) {
            "PRODUCT_LIST_FRAGMENT" -> ProductListFragment().show(fragmentManager, null)
            "SCHEDULE_INVOICE_FRAGMENT" -> SaleInvoiceFragment().show(fragmentManager, null)
            "CNF_FRAGMENT" -> CnfFragment().show(fragmentManager, null)
            "EXPENSE_PAGINATION"-> ExpensePagination().show(fragmentManager, null)
            "INVENTORY_FRAGMENT"-> InventoryFragment().show(fragmentManager,null)
            "CHECK_USER_LOGIN_STATUS"-> CheckUserLoginStatus().show(fragmentManager, null)
            "LEAVE_APPROVE_REJECT"-> LeaveApproveReject().show(fragmentManager, null)
            "TRIP_APPROVE_FRAGMENT"-> TripApproveFragment().show(fragmentManager, null)
            "TRIP_REPORTING_FRAGMENT"-> TripReportFragment().show(fragmentManager,null)
            "TRIP_PLAN_FRAGMENT"-> TripPlanFragment().show(fragmentManager,null)
            "LEAVE_PAGINATION"-> LeavePagination().show(fragmentManager,null)
            "ACTIVITY_PAGINATION"-> ActivityPagination().show(fragmentManager, null)
            "ATTENDANCE_PAGINATION"-> AttendancePagination().show(fragmentManager, null)
            "SETTINGS_FRAGMENT"-> SettingsFragment().show(fragmentManager,null)
            "SIGN_IN_OUT_FRAGMENT"->  SignInOutFragment().show(fragmentManager, null)
            else -> println("Other number")
        }
    }

    init {
        USER_TOKEN = token
    }
    companion object{
        val TAG = ReceiverMediator::class.java.name
        var USER_TOKEN: String = ""
        val SERVER_SYNC_URI : String = "https://rverp.in" //"https://65.0.61.137"
    }
}