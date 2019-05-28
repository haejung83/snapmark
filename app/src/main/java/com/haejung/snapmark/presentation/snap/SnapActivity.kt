package com.haejung.snapmark.presentation.snap

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.haejung.snapmark.R
import com.haejung.snapmark.extend.replaceFragmentInActivity
import timber.log.Timber


class SnapActivity : AppCompatActivity() {

    private val snapFragment by lazy {
        SnapFragment.newInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.snap_activity)

        // Extract extra from Intent
        intent?.extras?.let {
            Timber.d("Mark ID: ${it.getInt(EXTRA_WITH_MARK, EXTRA_DEFAULT_VALUE)}")
            Timber.d("Preset ID: ${it.getInt(EXTRA_WITH_PRESET, EXTRA_DEFAULT_VALUE)}")
        }

        replaceFragmentInActivity(snapFragment, R.id.container)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    companion object {
        private const val EXTRA_DEFAULT_VALUE = -1
        private const val EXTRA_WITH_MARK = "extra_with_mark"
        private const val EXTRA_WITH_PRESET = "extra_with_preset"

        fun createBundleWithMark(markId: Int) =
            Bundle().apply {
                putInt(EXTRA_WITH_MARK, markId)
            }

        fun createBundleWithPreset(presetId: Int) =
            Bundle().apply {
                putInt(EXTRA_WITH_PRESET, presetId)
            }
    }

}
