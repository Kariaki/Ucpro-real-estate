package com.decadev.ucpromap.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.decadev.ucpromap.model.UserDetails
import com.decadev.ucpromap.model.UserResponse
import com.decadev.ucpromap.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: Repository) : ViewModel() {

    private var _userResponse: MutableLiveData<Response<UserResponse>> = MutableLiveData()
    val userResponse: LiveData<Response<UserResponse>> get() = _userResponse

    fun pushUserDetails(userInfo: UserDetails) {
        viewModelScope.launch {
            val response = repository.postUser(userInfo)
            _userResponse.postValue(response)
        }
    }

}