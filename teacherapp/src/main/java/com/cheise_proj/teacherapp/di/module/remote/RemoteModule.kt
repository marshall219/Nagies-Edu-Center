package com.cheise_proj.teacherapp.di.module.remote

import com.cheise_proj.common_module.DEV_INFORDAS_BASE_URL
import com.cheise_proj.common_module.INFORDAS_BASE_URL
import com.cheise_proj.data.model.files.FilesData
import com.cheise_proj.data.model.message.ComplaintData
import com.cheise_proj.data.model.message.MessageData
import com.cheise_proj.data.model.people.PeopleData
import com.cheise_proj.data.model.user.ProfileData
import com.cheise_proj.data.model.user.UserData
import com.cheise_proj.data.source.RemoteSource
import com.cheise_proj.remote_source.RemoteSourceImpl
import com.cheise_proj.remote_source.api.ApiService
import com.cheise_proj.remote_source.mapper.RemoteMapper
import com.cheise_proj.remote_source.mapper.files.FilesDtoDataMapper
import com.cheise_proj.remote_source.mapper.message.ComplaintDtoDataMapper
import com.cheise_proj.remote_source.mapper.message.MessageDtoDataMapper
import com.cheise_proj.remote_source.mapper.people.PeopleDtoDataMapper
import com.cheise_proj.remote_source.mapper.user.ProfileDtoDataMapper
import com.cheise_proj.remote_source.mapper.user.UserDtoDataMapper
import com.cheise_proj.remote_source.model.dto.files.IFilesDto
import com.cheise_proj.remote_source.model.dto.message.ComplaintDto
import com.cheise_proj.remote_source.model.dto.message.MessageDto
import com.cheise_proj.remote_source.model.dto.people.IPeopleDto
import com.cheise_proj.remote_source.model.dto.user.IProfileDto
import com.cheise_proj.remote_source.model.dto.user.UserDto
import com.cheise_proj.teacherapp.BuildConfig
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [RemoteModule.Binders::class])
class RemoteModule {
    @Module
    interface Binders {

        //region FILES
        @Binds
        fun bindFilesDtoDataMapper(filesDtoDataMapper: FilesDtoDataMapper): RemoteMapper<IFilesDto, FilesData>
        //endregion

        //region PEOPLE
        @Binds
        fun bindPeopleDtoMapper(peopleDtoDataMapper: PeopleDtoDataMapper): RemoteMapper<IPeopleDto, PeopleData>
        //endregion

        //region MESSAGES
        @Binds
        fun bindComplaintDtoDataMapper(complaintDtoDataMapper: ComplaintDtoDataMapper): RemoteMapper<ComplaintDto, ComplaintData>

        @Binds
        fun bindMessageDtoDataMapper(messageDtoDataMapper: MessageDtoDataMapper): RemoteMapper<MessageDto, MessageData>
        //endregion

        //region USERS
        @Binds
        fun bindUserDtoDataMapper(userDtoDataMapper: UserDtoDataMapper): RemoteMapper<UserDto, UserData>

        @Binds
        fun bindRemoteSource(remoteSourceImpl: RemoteSourceImpl): RemoteSource

        @Binds
        fun bindProfileDtoDataMapper(profileDtoDataMapper: ProfileDtoDataMapper):
                RemoteMapper<IProfileDto, ProfileData>

        //endregion
    }

    @Suppress("SpellCheckingInspection")
    @Provides
    fun provideOkttpClient(tokenService: TokenService): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(tokenService)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .baseUrl(getBaseUrl())
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    private fun getBaseUrl(): String {
        if (BuildConfig.DEBUG) return DEV_INFORDAS_BASE_URL
        return INFORDAS_BASE_URL
    }
}