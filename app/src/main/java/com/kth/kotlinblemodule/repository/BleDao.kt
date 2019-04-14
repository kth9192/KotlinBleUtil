package com.kth.kotlinblemodule.repository

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BleDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(bleModel: BleModel)

    @Query("SELECT * from BleModel")
    fun getAll(): LiveData<List<BleModel>>?

    @Query("DELETE from BleModel")
    fun deleteAll()

}