package com.haejung.snapmark.presentation.intro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.haejung.snapmark.R
import com.haejung.snapmark.extend.moveToActivity
import com.haejung.snapmark.presentation.main.MainActivity
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class IntroActivity : AppCompatActivity() {

    private val disposable by lazy {
        CompositeDisposable()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
    }

    override fun onResume() {
        super.onResume()
        disposable.add(Completable.complete()
            .delay(1, TimeUnit.SECONDS)
            .subscribe{
                moveToActivity(MainActivity::class.java)
            }
        )
    }

    override fun onPause() {
        super.onPause()
        disposable.clear()
    }
}
