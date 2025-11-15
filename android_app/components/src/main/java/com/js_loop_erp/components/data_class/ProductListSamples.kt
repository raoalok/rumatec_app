package com.js_loop_erp.components.data_class

import java.util.Date

data class ProductListSamples(
    val id: Int? = null,
    val name: String? = "",
    val typeId: Int? = null,
    val packing: String? = "",
    val composition: String? = "",
    val formulation: String? = "N/A",
    val hsnId: Int? = null,
    val unitId: Int? = null,
    val shipper: String? = "N/A",
    val manualRate: String? = "0.00",
    val maximumStock: Int? = 0,
    val remark: String? = "",
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val createdBy: String? = "",
    val updatedBy: String? = "",
    val type: String? = "",
    val hsn: String? = "",
    val tax: Int? = 0,
    val unit: String? = "",
    var isSelected: Boolean? = false,
    var quantity: Int? = 0
)