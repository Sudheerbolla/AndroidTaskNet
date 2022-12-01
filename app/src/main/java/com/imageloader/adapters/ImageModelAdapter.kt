package com.imageloader.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.imageloader.BR
import com.imageloader.R
import com.imageloader.databinding.AdapterImageGroupBinding
import com.imageloader.models.ImageModel
import com.imageloader.viewmodels.ImagesViewModel

class ImageModelAdapter() : RecyclerView.Adapter<MainViewHolder>() {

    lateinit var imageGroups: ArrayList<ImageModel>
    lateinit var viewModel: ImagesViewModel

    constructor(viewModel: ImagesViewModel) : this() {
        this.viewModel = viewModel
        imageGroups = ArrayList()
    }

    fun updateList(imageGroups: ArrayList<ImageModel>?) {
        this.imageGroups = imageGroups!!
        notifyDataSetChanged()
    }

    fun getImageModelList(): ArrayList<ImageModel> {
        return imageGroups
    }

    fun updateCurrentImage(position: Int, imageModel: ImageModel) {
        this.imageGroups[position] = imageModel
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.adapter_image_group, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(viewModel, imageGroups.get(position))
    }

    override fun getItemCount(): Int {
        return imageGroups.size
    }
}

class MainViewHolder(private val binding: AdapterImageGroupBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(viewModel: ImagesViewModel, imageModel: ImageModel) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.setVariable(BR.imageModel, imageModel)
        binding.executePendingBindings()
    }

}