package com.haejung.snapmark.presentation.main

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.haejung.snapmark.R
import com.haejung.snapmark.extend.replaceFragmentInActivity
import com.haejung.snapmark.presentation.mark.MarkFragment
import com.haejung.snapmark.presentation.preset.PresetFragment
import com.haejung.snapmark.presentation.sns.SnsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val markFragment by lazy {
        MarkFragment.newInstance()
    }

    private val presetFragment by lazy {
        PresetFragment.newInstance()
    }

    private val snsFragment by lazy {
        SnsFragment.newInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding()
    }

    private fun binding() {
        bottom_navigation.setOnNavigationItemSelectedListener(
            object : BottomNavigationView.OnNavigationItemSelectedListener {
                override fun onNavigationItemSelected(item: MenuItem): Boolean {
                    when (item.itemId) {
                        R.id.navigation_mark -> replaceFragmentInActivity(markFragment, R.id.container_fragment)
                        R.id.navigation_preset -> replaceFragmentInActivity(presetFragment, R.id.container_fragment)
                        R.id.navigation_sns -> replaceFragmentInActivity(snsFragment, R.id.container_fragment)
                        else -> return false
                    }
                    return true
                }
            })

        replaceFragmentInActivity(markFragment, R.id.container_fragment)
    }
}
