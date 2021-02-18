package com.co2team.covidbuster.ui.history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.co2team.covidbuster.R
import com.co2team.covidbuster.ui.roomlist.EXTRA_ROOM_ID
import com.co2team.covidbuster.ui.roomlist.EXTRA_ROOM_NAME


class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_activity)

        val intent = intent
        val roomId = intent.getIntExtra(EXTRA_ROOM_ID, -1)
        val roomName = intent.getStringExtra(EXTRA_ROOM_NAME)

        title = roomName

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, HistoryFragment.newInstance(roomId))
                    .commitNow()
        }
    }
}