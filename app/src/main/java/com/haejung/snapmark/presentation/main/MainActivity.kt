package com.haejung.snapmark.presentation.main

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.haejung.snapmark.R
import com.haejung.snapmark.extend.replaceFragmentInActivity
import com.haejung.snapmark.extend.setupActionBar
import com.haejung.snapmark.presentation.mark.MarkFragment
import com.haejung.snapmark.presentation.preset.PresetFragment
import com.haejung.snapmark.presentation.sns.SnsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val markFragment by lazy(LazyThreadSafetyMode.NONE) {
        MarkFragment.newInstance()
    }

    private val presetFragment by lazy(LazyThreadSafetyMode.NONE) {
        PresetFragment.newInstance()
    }

    private val snsFragment by lazy(LazyThreadSafetyMode.NONE) {
        SnsFragment.newInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBar(R.id.toolbar) {
            setHomeAsUpIndicator(R.drawable.ic_watermark_black_24dp)
            setDisplayHomeAsUpEnabled(true)
        }

        setupBottomNavigation()
        setupViewFragment()
    }

    private fun setupBottomNavigation() {
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
    }

    private fun setupViewFragment() {
        replaceFragmentInActivity(markFragment, R.id.container_fragment)
    }

}
