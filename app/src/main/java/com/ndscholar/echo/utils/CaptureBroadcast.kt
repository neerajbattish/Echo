package com.ndscholar.echo.utils

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import com.ndscholar.echo.R
import com.ndscholar.echo.activities.MainActivity
import com.ndscholar.echo.fragments.SongPlayingFragment

class CaptureBroadcast :BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p1?.action==Intent.ACTION_NEW_OUTGOING_CALL)
        {
            try {
                MainActivity.Statified.notificationManager?.cancel(1978)
                if(SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean)
                {
                    SongPlayingFragment.Statified.mediaPlayer?.pause()
                    SongPlayingFragment.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                }
            }
            catch (e:Exception)
            {
                e.printStackTrace()
            }

        }else
        {
            val tm: TelephonyManager=p0?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when(tm?.callState)
            {
                TelephonyManager.CALL_STATE_RINGING ->
                {
                    try {
                        MainActivity.Statified.notificationManager?.cancel(1978)
                        if(SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean)
                        {
                            SongPlayingFragment.Statified.mediaPlayer?.pause()
                            SongPlayingFragment.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                        }
                    } catch (e:Exception)
                    {
                        e.printStackTrace()
                    }
                }
                else ->{

                }
            }

        }
    }
}