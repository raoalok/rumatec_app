package com.js_loop_erp.components.fragments

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import  com.js_loop_erp.components.ApplicationDb

@Dao
interface IDBOperations {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(data: EmpLocationData)

    @Query("SELECT * FROM ${ApplicationDb.TABLE_NAME}")
    suspend fun getData(): List<EmpLocationData>
}
