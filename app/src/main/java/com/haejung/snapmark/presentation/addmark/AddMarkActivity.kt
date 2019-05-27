package com.haejung.snapmark.presentation.addmark

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.haejung.snapmark.R
import com.haejung.snapmark.extend.obtainViewModel
import com.haejung.snapmark.extend.replaceFragmentInActivity
import com.haejung.snapmark.extend.setupActionBar
import com.haejung.snapmark.presentation.Event

class AddMarkActivity : AppCompatActivity() {

    private val addMarkFragment by lazy {
        AddMarkFragment.newInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_mark_activity)

        setupActionBar(R.id.toolbar) {
            setHomeAsUpIndicator(R.drawable.ic_backspace_black_24dp)
            setTitle(R.string.title_add_mark)
            setDisplayHomeAsUpEnabled(true)
        }

        obtainViewModel(AddMarkViewModel::class.java).apply {
            saveMarkEvent.observe(this@AddMarkActivity, Observer<Event<Unit>> {
                finish()
            })
        }

        replaceFragmentInActivity(addMarkFragment, R.id.container_fragment)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

}

