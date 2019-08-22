package com.example.restaurant

import androidx.annotation.CallSuper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.mockito.MockitoAnnotations


open class BaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val trampolineSchedulerRule = TrampolineSchedulerRule()

    @Before
    @CallSuper
    open fun setup() {
        MockitoAnnotations.initMocks(this)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @After
    @CallSuper
    fun after() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }
}

class TrampolineSchedulerRule : TestRule {

    private val scheduler by lazy { Schedulers.trampoline() }

    override fun apply(base: Statement?, description: Description?): Statement =
        object : Statement() {
            override fun evaluate() {
                try {
                    RxJavaPlugins.setComputationSchedulerHandler { scheduler }
                    RxJavaPlugins.setIoSchedulerHandler { scheduler }
                    RxJavaPlugins.setNewThreadSchedulerHandler { scheduler }
                    RxJavaPlugins.setSingleSchedulerHandler { scheduler }
                    base?.evaluate()
                } finally {
                    RxJavaPlugins.reset()
                    RxAndroidPlugins.reset()
                }
            }
        }
}