package com.kth.kotlinblemodule.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.kth.kotlinblemodule.repository.BleModel
import com.kth.kotlinblemodule.repository.BleRepository

class BleViewModel(application: Application) : AndroidViewModel(application) {

    private var viewRepository : BleRepository =
        BleRepository(application)

    fun getList() : LiveData<List<BleModel>>? {

        return viewRepository.getAll()
    }

    fun insert(bleModel: BleModel){
        viewRepository.insert(bleModel)
    }

    fun deleteAll(){
        viewRepository.deleteAll()
    }
}