package com.cheise_proj.teacherapp

import androidx.work.Configuration
import androidx.work.WorkManager
import com.cheise_proj.teacherapp.di.DaggerAppComponent
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class TeacherApp : DaggerApplication() {
    private val appComponent by lazy {
        DaggerAppComponent.builder().application(this).build()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = appComponent

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
        initWorkManager()
        initFCMService()
    }

    private fun initWorkManager() {
        WorkManager.initialize(this, Configuration.Builder().run {
            setWorkerFactory(appComponent.workerFactory())
                .build()
        })
    }

    private fun initFCMService() {
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if (!it.isSuccessful) {
                println("Task Failed")
                return@addOnCompleteListener
            }
            println("result: ${it.result?.token}")
        }
        FirebaseMessaging.getInstance().subscribeToTopic(getGlobalTopic()).addOnCompleteListener {
            if (!it.isSuccessful) {
                println("Task Failed")
                return@addOnCompleteListener
            }
            println("subscribe global topic")
        }
    }

    private fun getGlobalTopic(): String {
        if (BuildConfig.DEBUG) return getString(R.string.fcm_topic_dev_global)
        return getString(R.string.fcm_topic_global)
    }
}