package com.co2team.covidbuster.ui.history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.co2team.covidbuster.R

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, HistoryFragment.newInstance())
                    .commitNow()
        }
    }
}