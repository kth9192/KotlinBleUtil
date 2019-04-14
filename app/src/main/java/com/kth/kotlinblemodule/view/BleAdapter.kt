package com.kth.kotlinblemodule.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kth.kotlinblemodule.repository.BleModel
import com.kth.kotlinblemodule.R
import com.kth.kotlinblemodule.app.MyApp
import com.kth.kotlinblemodule.databinding.ItemBleBinding

class BleAdapter :
    ListAdapter<BleModel, BleAdapter.BleViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BleModel>() {
            override fun areItemsTheSame(oldModel: BleModel, newModel: BleModel): Boolean {
                // User properties may have changed if reloaded from the DB, but ID is fixed
                return oldModel.macAddr == newModel.macAddr
            }

            override fun areContentsTheSame(oldModel: BleModel, newModel: BleModel): Boolean {
                // NOTE: if you use equals, your object must properly override Object#equals()
                // Incorrectly returning false here will result in too many animations.
                return oldModel == newModel
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ble, parent, false)
        return BleViewHolder(view)
    }

    override fun onBindViewHolder(holder: BleViewHolder, position: Int) {
        holder.itemBleBinding?.data = getItem(position)
        holder.itemView.setOnClickListener {
            val myApp = MyApp()
//            myApp.setBleDevice(getItem(position).device)
        }
    }

    class BleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemBleBinding: ItemBleBinding? = DataBindingUtil.bind(itemView)
    }
}