package com.kth.kotlinblemodule.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kth.kotlinblemodule.repository.AppDatabase
import com.kth.kotlinblemodule.repository.BleDao
import com.kth.kotlinblemodule.repository.BleModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BleRepository(application: Application) {

    private lateinit var bleDao: BleDao
    private var listLiveData: MutableLiveData<List<BleModel>>? = null

    var executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = AppDatabase.getDatabase(application)

        if (db != null) {
            bleDao = db.bleDao()
        }
    }

    fun getAll(): LiveData<List<BleModel>>? {

        if (listLiveData != null){
            listLiveData = bleDao.getAll() as MutableLiveData<List<BleModel>>
        }

        return listLiveData
    }

    fun insert(bleModel: BleModel) {
        executorService.execute { bleDao.insert(bleModel) }
    }

    fun deleteAll() {
        executorService.execute { bleDao.deleteAll() }
    }

}