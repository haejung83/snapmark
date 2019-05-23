package com.haejung.snapmark.extend

import android.content.Intent
import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.haejung.snapmark.presentation.ViewModelFactory

// Referenced from the Android-Architecture/TodoApp

fun <T> AppCompatActivity.startActivity(clazz: Class<T>) {
    val intent = Intent(applicationContext, clazz)
    startActivity(intent)
}

fun <T> AppCompatActivity.moveToActivity(clazz: Class<T>) {
    startActivity(clazz)
    finish()
}

fun AppCompatActivity.replaceFragmentInActivity(fragment: Fragment, @IdRes frameId: Int) {
    supportFragmentManager.transact {
        replace(frameId, fragment)
    }
}

fun AppCompatActivity.addFragmentToActivity(fragment: Fragment, tag: String) {
    supportFragmentManager.transact {
        add(fragment, tag)
    }
}

fun AppCompatActivity.setupActionBar(@IdRes toobarId: Int, action: ActionBar.() -> Unit) {
    setSupportActionBar(findViewById(toobarId))
    supportActionBar?.run {
        action()
    }
}

fun <T : ViewModel> AppCompatActivity.obtainViewModel(viewModelClass: Class<T>) =
    ViewModelProviders.of(this, ViewModelFactory.getInstance(applicationContext)).get(viewModelClass)

private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) =
    beginTransaction().apply { action() }.commit()
