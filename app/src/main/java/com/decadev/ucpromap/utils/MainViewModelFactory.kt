package com.decadev.ucpromap.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.decadev.ucpromap.repository.Repository
import com.decadev.ucpromap.viewModel.MainViewModel

class MainViewModelFactory(val newsRespository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(newsRespository) as T
    }
}