package com.js_loop_erp.components.fragments

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DbRepository(private val idbOperations: IDBOperations) {
    suspend fun insertData(data: EmpLocationData) {
        withContext(Dispatchers.IO) {
            idbOperations.insertData(data)
        }
    }

    suspend fun getData(): List<EmpLocationData> {
        return withContext(Dispatchers.IO) {
            idbOperations.getData()
        }
    }
}