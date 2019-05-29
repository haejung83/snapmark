package com.haejung.snapmark.presentation.snap

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.haejung.snapmark.R
import com.haejung.snapmark.extend.replaceFragmentInActivity


class SnapActivity : AppCompatActivity() {

    private val snapFragment by lazy {
        SnapFragment.newInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.snap_activity)

        replaceFragmentInActivity(snapFragment.apply {
            // Extract extra from Intent
            intent?.extras?.let { arguments = it }
        }, R.id.container)
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
