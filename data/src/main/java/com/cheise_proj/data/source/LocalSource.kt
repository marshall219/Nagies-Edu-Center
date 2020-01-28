package com.cheise_proj.data.source

import com.cheise_proj.data.model.user.ProfileData
import com.cheise_proj.data.model.user.UserData
import io.reactivex.Completable
import io.reactivex.Single

interface LocalSource {
    fun saveUser(userData: UserData)
    fun getUser(username: String, password: String): Single<UserData>
    fun saveProfile(profileData: ProfileData)
    fun getProfile(identifier:String):Single<ProfileData>
}