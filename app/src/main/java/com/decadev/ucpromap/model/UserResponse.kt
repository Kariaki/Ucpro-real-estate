package com.decadev.ucpromap.model

data class UserResponse(
    val message: String,
    val token: String,
    val user: User
)