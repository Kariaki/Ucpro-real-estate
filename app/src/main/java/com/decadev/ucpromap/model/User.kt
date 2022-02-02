package com.decadev.ucpromap.model

data class User(
    val __v: Int,
    val _id: String,
    val accessLevel: String,
    val createdAt: String,
    val email: String,
    val profile: Profile,
    val updatedAt: String,
    val verified: Boolean
)