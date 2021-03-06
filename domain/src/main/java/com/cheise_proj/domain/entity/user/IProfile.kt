package com.cheise_proj.domain.entity.user

interface IProfile {
    val refNo: String
    val name: String
    val gender: String
    val adminDate: String
    val faculty: String
    val level: String
    val username: String
    val contact: String
    var imageUrl: String?
    val section: String
    val semester: String
    val guardian: String
    val dob: String
}