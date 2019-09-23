package com.ndscholar.echo.adapters

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.ndscholar.echo.R
import com.ndscholar.echo.Songs
import com.ndscholar.echo.activities.MainActivity
import com.ndscholar.echo.fragments.MainScreenFragment
import com.ndscholar.echo.fragments.SongPlayingFragment
import kotlinx.android.synthetic.main.row_custom_mainscreen_adapter.view.*

class MainScreenAdapter(_songDetails: ArrayList<Songs>, _context: Context) :
    RecyclerView.Adapter<MainScreenAdapter.MyViewHolder>() {
    var songDetails: ArrayList<Songs>? = null
    var mContext: Context? = null

    init {
        this.songDetails = _songDetails
        this.mContext = _context
    }

    override fun getItemCount(): Int {
        if (songDetails == null) {
            return 0
        } else {
            return (songDetails as ArrayList<Songs>).size
        }
    }

    override fun onBindViewHolder(p0: MainScreenAdapter.MyViewHolder, p1: Int) {
        val songObject = songDetails?.get(p1)
        p0.trackTitle?.text = songObject?.songTitle
        p0.trackArtist?.text = songObject?.artist
        p0.contentHolder?.setOnClickListener({
            val songplayingFragment = SongPlayingFragment()
            var args= Bundle()
            args.putString("songArtist", songObject?.artist)
            args.putString("path", songObject?.songData)
            args.putString("songTitle", songObject?.songTitle)
            args.putInt("songId",songObject?.songId?.toInt() as Int)
            args.putInt("songPosition",p1)
            args.putParcelableArrayList("songData", songDetails)
            songplayingFragment.arguments=args
            (mContext as FragmentActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.details_fragments, songplayingFragment)
                .addToBackStack("SongPlayingFragment")
                .commit()
        })
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MainScreenAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(p0?.context).inflate(R.layout.row_custom_mainscreen_adapter, p0, false)
        return MyViewHolder(itemView)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var contentHolder: RelativeLayout? = null

        init {
            trackTitle = view.findViewById(R.id.trackTitle) as TextView
            trackArtist = view.findViewById(R.id.trackArtist) as TextView
            contentHolder = view.findViewById(R.id.contentRow) as RelativeLayout
        }
    }
}