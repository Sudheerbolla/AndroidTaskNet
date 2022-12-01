package com.imageloader.activities

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayoutMediator
import com.imageloader.R
import com.imageloader.adapters.ImageModelAdapter
import com.imageloader.databinding.ActivityMainBinding
import com.imageloader.repository.ImagesRepository
import com.imageloader.utils.StaticUtils
import com.imageloader.utils.wsutils.RetrofitService
import com.imageloader.viewmodels.ImagesViewModel
import com.imageloader.viewmodels.MyViewModelFactory

class HomeActivity : AppCompatActivity() {
    private val TAG = "HomeActivity"
    private lateinit var binding: ActivityMainBinding

    lateinit var viewModel: ImagesViewModel

    private val retrofitService = RetrofitService.getInstance()
    lateinit var adapter: ImageModelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        initComponents()
    }

    private fun initComponents() {
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(ImagesRepository(retrofitService))).get(
                ImagesViewModel::class.java
            )
        binding.viewModel = viewModel
        setUpViewPager()
        setViewModelObservers()
    }

    private fun setUpViewPager() {
        adapter = ImageModelAdapter(viewModel)
        binding.viewPager.orientation = ORIENTATION_HORIZONTAL
        binding.viewPager.adapter = adapter
        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                adapter.getImageModelList().let {
                    if (it.isNotEmpty() && TextUtils.isEmpty(it[position].imageUrl)) viewModel.getImageDetails(
                        adapter.getImageModelList()[position].identifier
                    )
                }
            }
        })
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
        }.attach()
    }

    private fun setViewModelObservers() {
        viewModel.selectedImageGroup.observe(this) {
            updateCurrentImageGroupView(it)
        }
        viewModel.currentImageDetails.observe(this) {
            adapter.updateCurrentImage(binding.viewPager.currentItem, it)
        }
        viewModel.apiError.observe(this) { error ->
            StaticUtils.showIndefiniteToast(
                binding.root,
                getString(R.string.error_getting_data_from_api),
                getString(R.string.retry)
            ) {
                when (error) {
                    0 -> viewModel.getAllImageGroups()
                    1 -> viewModel.getImageDetails(
                        adapter.getImageModelList()[binding.viewPager.currentItem].identifier
                    )
                }
            }
        }
    }

    private fun updateCurrentImageGroupView(groupImageIdentifiers: List<String>) {
        try {
            if (groupImageIdentifiers.isNotEmpty()) {
                adapter.updateList(StaticUtils.getFakeData(groupImageIdentifiers))
                binding.viewPager.setCurrentItem(0, true)
                adapter.getImageModelList().let {
                    if (it.isNotEmpty() && TextUtils.isEmpty(it[0].imageUrl)) viewModel.getImageDetails(
                        adapter.getImageModelList()[0].identifier
                    )
                }
            }
        } catch (e: Exception) {
        }
    }

}