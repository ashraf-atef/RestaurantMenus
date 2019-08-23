package com.example.restaurant.common.presentationLayer

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection

//TODO: Make base aactivity of state
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        if (getContentResource() != -1) {
            setContentView(getContentResource())
        }
        init(savedInstanceState)
    }

    @LayoutRes
    abstract fun getContentResource(): Int

    abstract fun init(state: Bundle?)
}