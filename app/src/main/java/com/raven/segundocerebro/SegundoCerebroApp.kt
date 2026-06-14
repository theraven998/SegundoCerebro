package com.raven.segundocerebro

import android.app.Application
import com.raven.segundocerebro.data.AppDatabase
import com.raven.segundocerebro.data.Repository

class SegundoCerebroApp : Application() {
    val repository: Repository by lazy {
        val db = AppDatabase.get(this)
        Repository(db.noteDao(), db.containerDao())
    }
}
