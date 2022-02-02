package com.decadev.ucpromap.retrofit

import com.decadev.ucpromap.model.UserDetails
import com.decadev.ucpromap.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitApi {

    @POST("/api/v2/google-auth")
    suspend fun postUser(
        @Body  post: UserDetails
    ) : Response<UserResponse>
}