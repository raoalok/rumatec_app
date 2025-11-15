package com.js_loop_erp.components.data_class

data class TripTourPlanStockistSelection(
    val id: Int? = 0,
    val name: String? = "",
    val paymentTerms: Int? = 0,
    val gstin: String? = "",
    val isTCS: Boolean? = false,
    val isTDS: Boolean? = false,
    var isSelected: Boolean? = false
) {

}