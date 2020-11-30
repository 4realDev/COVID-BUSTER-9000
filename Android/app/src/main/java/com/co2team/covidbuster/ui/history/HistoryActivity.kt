package com.co2team.covidbuster.ui.history

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.co2team.covidbuster.R
import com.co2team.covidbuster.ui.currentroom.CurrentRoomFragment
import com.co2team.covidbuster.ui.roomlist.EXTRA_ROOMID
import com.co2team.covidbuster.ui.roomlist.RoomListFragment


class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_activity)

        val intent = intent
        val roomId = intent.getIntExtra(EXTRA_ROOMID, -1) //if it's a string you stored.

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, HistoryFragment.newInstance())
                    .commitNow()
        }
    }
}