package com.kth.kotlinblemodule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class BleViewModel(application: Application) : AndroidViewModel(application) {

    private var viewRepository : BleRepository = BleRepository(application)

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