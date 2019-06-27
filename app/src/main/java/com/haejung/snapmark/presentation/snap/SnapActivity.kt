package com.haejung.snapmark.presentation.snap

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.haejung.snapmark.R
import com.haejung.snapmark.extend.replaceFragmentInActivity
import com.haejung.snapmark.extend.setupActionBar


class SnapActivity : AppCompatActivity() {

    private val snapFragment by lazy {
        SnapFragment.newInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.snap_activity)

        setupActionBar(R.id.toolbar) {
            setHomeAsUpIndicator(R.drawable.ic_backspace_black_24dp)
            setDisplayHomeAsUpEnabled(true)
        }
        setupViewFragment()
    }

    private fun setupViewFragment() {
        replaceFragmentInActivity(
            snapFragment.apply {
                // Extract extra from Intent
                intent?.extras?.let { arguments = it }
            },
            R.id.container_fragment
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    companion object {
        internal const val EXTRA_TYPE = "extra_type"
        internal const val EXTRA_TYPE_MARK = "extra_type_mark"
        internal const val EXTRA_TYPE_PRESET = "extra_type_preset"
        internal const val EXTRA_ID = "extra_id"

        fun createBundleWithMark(markId: Int) =
            Bundle().apply {
                putString(EXTRA_TYPE, EXTRA_TYPE_MARK)
                putInt(EXTRA_ID, markId)
            }

        fun createBundleWithPreset(presetId: Int) =
            Bundle().apply {
                putString(EXTRA_TYPE, EXTRA_TYPE_PRESET)
                putInt(EXTRA_ID, presetId)
            }
    }

}
