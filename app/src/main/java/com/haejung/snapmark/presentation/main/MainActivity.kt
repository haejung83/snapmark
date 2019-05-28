package com.haejung.snapmark.presentation.main

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.haejung.snapmark.R
import com.haejung.snapmark.extend.addFragmentToActivity
import com.haejung.snapmark.extend.hideFragmentInActivity
import com.haejung.snapmark.extend.setupActionBar
import com.haejung.snapmark.extend.showFragmentInActivity
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

    private var activatedFragment: Fragment? = null

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
                        R.id.navigation_mark -> replaceFragmentAndActionBarStatus(
                            markFragment,
                            getString(R.string.title_nav_mark),
                            R.drawable.ic_watermark_black_24dp
                        )
                        R.id.navigation_preset -> replaceFragmentAndActionBarStatus(
                            presetFragment,
                            getString(R.string.title_nav_mark_preset),
                            R.drawable.ic_preset_black_24dp
                        )
                        R.id.navigation_sns -> replaceFragmentAndActionBarStatus(
                            snsFragment,
                            getString(R.string.title_nav_sns),
                            R.drawable.ic_sns_black_24dp
                        )
                        else -> return false
                    }
                    return true
                }
            })
    }

    private fun setupViewFragment() {
        replaceFragmentAndActionBarStatus(
            markFragment,
            getString(R.string.title_nav_mark),
            R.drawable.ic_watermark_black_24dp
        )
    }

    private fun replaceFragmentAndActionBarStatus(
        targetFragment: Fragment,
        actionBarTitle: String,
        @DrawableRes actionBarIcon: Int
    ) {
        activatedFragment?.let { hideFragmentInActivity(it) }

        if (targetFragment.isAdded) {
            showFragmentInActivity(targetFragment)
        } else {
            addFragmentToActivity(targetFragment, R.id.container_fragment, actionBarTitle)
        }

        activatedFragment = targetFragment

        supportActionBar?.let {
            it.title = actionBarTitle
            it.setHomeAsUpIndicator(actionBarIcon)
        }
    }


}
