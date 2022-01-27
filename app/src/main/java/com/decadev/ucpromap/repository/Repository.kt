package com.decadev.ucpromap.repository

import com.decadev.ucpromap.model.ProfileObj
import com.decadev.ucpromap.model.UserDetails
import com.decadev.ucpromap.model.UserResponse
import com.decadev.ucpromap.retrofit.RetrofitApi
import com.decadev.ucpromap.retrofit.RetrofitInstance
import retrofit2.Response

class Repository {

    suspend fun postUser(profile: UserDetails) : Response<UserResponse> {
        return RetrofitInstance.api.postUser(profile)
    }
}