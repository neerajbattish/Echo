package com.ndscholar.echo.fragments


import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.util.UniversalTimeScale.toLong
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.ndscholar.echo.CurrentSongHelper
import com.ndscholar.echo.R
import com.ndscholar.echo.Songs
import com.ndscholar.echo.database.EchoDatabase
import com.ndscholar.echo.fragments.SongPlayingFragment.Staticated.onSongComplete
import com.ndscholar.echo.fragments.SongPlayingFragment.Staticated.playNext
import com.ndscholar.echo.fragments.SongPlayingFragment.Staticated.processInformation
import com.ndscholar.echo.fragments.SongPlayingFragment.Staticated.updateTextViews
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.audioVisualization
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.currentPosition
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.currentSongHelper
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.endTimeText
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.fab
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.favoriteContent
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.fetchSongs
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.glView
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.loopImageButton
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.mediaPlayer
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.myActivity
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.nextImageButton
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.playPauseImageButton
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.previousImageButton
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.seekBar
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.shuffleImageButton
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.songArtistView
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.songTitleView
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.startTimeText
import com.ndscholar.echo.fragments.SongPlayingFragment.Statified.updateSongTime
import java.util.*
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SongPlayingFragment : Fragment() {

    object Statified {
        var myActivity: Activity? = null
        var mediaPlayer: MediaPlayer? = null
        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playPauseImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var shuffleImageButton: ImageButton? = null
        var seekBar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>? = null
        var currentSongHelper: CurrentSongHelper? = null
        var audioVisualization: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null
        var fab: ImageButton? = null
        var favoriteContent: EchoDatabase? = null
        var mSensorManager: SensorManager?=null
        var mSensorListener:SensorEventListener?=null
         var MY_PREFS_NAME="ShakeFeature"

        var updateSongTime= object : Runnable{
            override fun run()
            {
                val getCurrent =mediaPlayer?.currentPosition
                startTimeText?.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                    TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong() as Long))))
                seekBar?.setProgress(getCurrent?.toInt() as Int)
                Handler().postDelayed(this,1000)
            }
        }
    }

    object Staticated {
        var MY_PREFS_SHUFFLE = "Shuffle feature"
        var MY_PREFS_LOOP = "Loop feature"

        fun onSongComplete() {

            if (currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
                currentSongHelper?.isPlayinig = true
            } else {
                if (currentSongHelper?.isLoop as Boolean) {
                    currentSongHelper?.isPlayinig = true
                    var nextSong = fetchSongs?.get(currentPosition)
                    currentSongHelper?.currentPosition = currentPosition
                    currentSongHelper?.songPath = nextSong?.songData
                    currentSongHelper?.songTitle = nextSong?.songTitle
                    currentSongHelper?.songArtist = nextSong?.artist

                    currentSongHelper?.songId = nextSong?.songId as Long

                    updateTextViews(
                        currentSongHelper?.songTitle as String,
                        currentSongHelper?.songArtist as String
                    )
                    mediaPlayer?.reset()

                    try {
                        mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
                        mediaPlayer?.prepare()
                        mediaPlayer?.start()
                        processInformation(mediaPlayer as MediaPlayer)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    playNext("PlayNextNormal")
                    currentSongHelper?.isPlayinig = true
                }
            }

            if(favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean)
            {
                fab?.setBackgroundResource(R.drawable.favorite_on)
            }
            else
            {
                fab?.setBackgroundResource(R.drawable.favorite_off)
            }

        }

        fun updateTextViews(songTitle: String, songArtist: String) {
            var songTitleUpdated=songTitle
            var songArtistUpdated=songArtist
            if(songTitle.equals("<unknown>",true))
            {
                songTitleUpdated="unknown"
            }
            if(songArtist.equals("<unknown>",true))
            {
                songArtistUpdated="unknown"
            }
            songTitleView?.text = songTitle
            songArtistView?.text = songArtist
        }

        fun processInformation(mediaPlayer: MediaPlayer) {
            val finalTime = mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition
            seekBar?.max = finalTime
            startTimeText?.setText(
                String.format(
                    "%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()))
                )
            )

            endTimeText?.setText(
                String.format(
                    "%d: %d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()))
                )
            )

            seekBar?.setProgress(startTime)
            Handler().postDelayed(updateSongTime, 1000)
        }

        fun playNext(check: String) {
            if (check.equals("PlayNextNormal", true)) {
                currentPosition = currentPosition + 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                var randomObject = Random()
                var randomPosition = randomObject.nextInt(fetchSongs?.size?.plus(1) as Int)
                currentPosition = randomPosition

            }
            if(currentPosition== fetchSongs?.size)
            {
                currentPosition=0
            }
            currentSongHelper?.isLoop=false

            var nextSong = fetchSongs?.get(currentPosition)
            currentSongHelper?.songPath = nextSong?.songData
            currentSongHelper?.songTitle = nextSong?.songTitle
            currentSongHelper?.songArtist = nextSong?.artist

            currentSongHelper?.songId = nextSong?.songId as Long
            updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
            mediaPlayer?.reset()


            try {
                mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                processInformation(mediaPlayer as MediaPlayer)

            } catch (e: Exception) {
                e.printStackTrace()
            }
            if(favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean)
            {
                fab?.setBackgroundResource(R.drawable.favorite_on)
            }
            else
            {
                fab?.setBackgroundResource(R.drawable.favorite_off)
            }

        }
    }
var mAccleration: Float=0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationLast : Float= 0f


        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            val view = inflater!!.inflate(R.layout.fragment_song_playing, container, false)
            setHasOptionsMenu(true)
            activity?.title="Now Playing"
            seekBar = view?.findViewById(R.id.seekBar)
            startTimeText = view?.findViewById(R.id.startTime)
            endTimeText = view?.findViewById(R.id.endTime)
            playPauseImageButton = view?.findViewById(R.id.playPauseButton)
            nextImageButton = view?.findViewById(R.id.nextButton)
            previousImageButton = view?.findViewById(R.id.previousButton)
            loopImageButton = view?.findViewById(R.id.loopButton)
            songArtistView = view?.findViewById(R.id.songArtist)
            songTitleView = view?.findViewById(R.id.songTitle)
            shuffleImageButton = view?.findViewById(R.id.shuffleButton)
            glView = view?.findViewById(R.id.visualizer_view)
            fab = view?.findViewById(R.id.favouriteIcon)
            fab?.alpha = 0.8f

            return view

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            audioVisualization = glView as AudioVisualization
        }

        override fun onAttach(context: Context?) {
            super.onAttach(context)
            myActivity = context as Activity
        }

        override fun onAttach(activity: Activity?) {
            super.onAttach(activity)
            myActivity = activity
        }

        override fun onResume() {
            super.onResume()
            audioVisualization?.onResume()
            Statified.mSensorManager?.registerListener(Statified.mSensorListener,
                Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL)
        }

        override fun onPause() {
            audioVisualization?.onPause()
            super.onPause()
            Statified.mSensorManager?.unregisterListener(Statified.mSensorListener)
        }

        override fun onDestroyView() {
            audioVisualization?.release()
            super.onDestroyView()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Statified.mSensorManager=Statified.myActivity?.getSystemService(
            Context.SENSOR_SERVICE) as SensorManager
        mAccleration =0.0f
        mAccelerationCurrent=SensorManager.GRAVITY_EARTH
        mAccelerationLast=SensorManager.GRAVITY_EARTH

        bingShakeListener()

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem?=menu?.findItem(R.id.action_redirect)
        item?.isVisible=true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId)
        {
            R.id.action_redirect ->
            {Statified.myActivity?.onBackPressed()
            return false
            }
        }
        return false
    }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)

            favoriteContent = EchoDatabase(myActivity)
            currentSongHelper = CurrentSongHelper()
            currentSongHelper?.isPlayinig = true
            currentSongHelper?.isLoop = false
            currentSongHelper?.isShuffle = false


            var path: String? = null
            var _songTitle: String? = null
            var _songArtist: String? = null
            var songId: Long = 0

            try {

                path = this.arguments?.getString("path")
                _songTitle = arguments?.getString("songTitle")
                _songArtist = arguments?.getString("SongArtist")
                songId = arguments?.getInt("songId")!!.toLong()
                currentPosition = arguments!!.getInt("position")
                fetchSongs = arguments!!.getParcelableArrayList("songData")

                currentSongHelper?.songPath = path
                currentSongHelper?.songTitle = _songTitle
                currentSongHelper?.songArtist = _songArtist
                currentSongHelper?.songId = songId
                currentSongHelper?.currentPosition = currentPosition
                updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            var fromFavBottonBar = arguments?.get("FavBottomBar") as? String
            if (fromFavBottonBar != null) {
                Statified.mediaPlayer = FavoriteFragment.Statified.mediaPlayer
            } else
            {


                mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                mediaPlayer?.setDataSource(myActivity, Uri.parse(path))
                mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            mediaPlayer?.start()
        }
            processInformation(mediaPlayer as MediaPlayer)

            if (currentSongHelper?.isPlayinig as Boolean) {
                playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }
            mediaPlayer?.setOnCompletionListener { onSongComplete() }
            clickHandler()

            var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(myActivity as Context, 0)
            audioVisualization?.linkTo(visualizationHandler)


            var prefsForShuffle = myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)
            var isShuffleAllowed = prefsForShuffle?.getBoolean("feature", false)
            if (isShuffleAllowed as Boolean) {
                currentSongHelper?.isShuffle = true
                currentSongHelper?.isLoop = false
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            } else {
                currentSongHelper?.isShuffle = false
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            }

            var prefsForLoop = myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)
            var isLoopAllowed = prefsForLoop?.getBoolean("feature", false)
            if (isLoopAllowed as Boolean) {
                currentSongHelper?.isShuffle = false
                currentSongHelper?.isLoop = true
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
            } else {
                currentSongHelper?.isLoop = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }

            if(favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean)
            {
                fab?.setBackgroundResource(R.drawable.favorite_on)
            }
            else
            {
                fab?.setBackgroundResource(R.drawable.favorite_off)
            }
        }


        fun clickHandler() {
            fab?.setOnClickListener({
                if (favoriteContent?.checkifIdExists(
                        currentSongHelper?.songId?.toInt()
                                as Int
                    ) as Boolean
                ) {
                    fab?.setBackgroundResource(R.drawable.favorite_off)
                    favoriteContent?.deleteFavourite(currentSongHelper?.songId?.toInt() as Int)
                    Toast.makeText(myActivity, "Removed from Favorites", Toast.LENGTH_SHORT).show()
                } else {
                    fab?.setBackgroundResource(R.drawable.favorite_on)
                    favoriteContent?.storeAsFavorite(
                        currentSongHelper?.songId?.toInt(),
                        currentSongHelper?.songArtist, currentSongHelper?.songTitle, currentSongHelper?.songPath
                    )
                    Toast.makeText(myActivity, "Added to Favorites", Toast.LENGTH_SHORT).show()
                }
            })



            shuffleImageButton?.setOnClickListener {

                var editorShuffle =
                    myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
                var editorLoop =
                    myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

                if (currentSongHelper?.isShuffle as Boolean) {
                    shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                    currentSongHelper?.isShuffle = false
                    editorShuffle?.putBoolean("feature", false)
                    editorShuffle?.apply()
                } else {
                    currentSongHelper?.isShuffle = true
                    currentSongHelper?.isLoop = false
                    shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                    loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

                    editorShuffle?.putBoolean("feature", true)
                    editorShuffle?.apply()
                    editorLoop?.putBoolean("feature", false)
                    editorLoop?.apply()
                }
            }
            nextImageButton?.setOnClickListener {
                currentSongHelper?.isPlayinig = true
                //playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
                if (currentSongHelper?.isShuffle as Boolean) {
                    playNext("PlayNextLikeNormalShuffle")
                } else {
                    playNext("PlayNextNormal")
                }
            }
            previousImageButton?.setOnClickListener {
                currentSongHelper?.isPlayinig = true
               //
                if (currentSongHelper?.isLoop as Boolean) {
                    loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                }
                playPrevious()
            }
            loopImageButton?.setOnClickListener {

                var editorShuffle =
                    myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
                var editorLoop =
                    myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

                if (currentSongHelper?.isLoop as Boolean) {
                    currentSongHelper?.isLoop = false
                    shuffleImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

                    editorLoop?.putBoolean("feature", false)
                    editorLoop?.apply()
                } else {
                    currentSongHelper?.isShuffle = false
                    currentSongHelper?.isLoop = true
                    shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                    loopImageButton?.setBackgroundResource(R.drawable.loop_icon)

                    editorShuffle?.putBoolean("feature", false)
                    editorShuffle?.apply()
                    editorLoop?.putBoolean("feature", true)
                    editorLoop?.apply()
                }

            }
            playPauseImageButton?.setOnClickListener {

                if (mediaPlayer?.isPlaying as Boolean) {
                    mediaPlayer?.pause()
                    currentSongHelper?.isPlayinig = false
                    playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)

                } else {
                    mediaPlayer?.start()
                    currentSongHelper?.isPlayinig = true
                    playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
                }

            }
        }


        fun playPrevious() {
            currentPosition = currentPosition - 1
            if (currentPosition == -1) {
                currentPosition = 0
            }
            if (currentSongHelper?.isPlayinig as Boolean) {
                playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }
            currentSongHelper?.isLoop = false
            var nextSong = fetchSongs?.get(currentPosition)
            currentSongHelper?.songPath = nextSong?.songData
            currentSongHelper?.songTitle = nextSong?.songTitle
            currentSongHelper?.songArtist = nextSong?.artist
            currentSongHelper?.songId = nextSong?.songId as Long
            mediaPlayer?.reset()
            updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
            try {
                mediaPlayer?.setDataSource(myActivity, Uri.parse(currentSongHelper?.songPath))
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                processInformation(mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if(favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean)
            {
                fab?.setBackgroundResource(R.drawable.favorite_on)
            }
            else
            {
                fab?.setBackgroundResource(R.drawable.favorite_off)
            }


        }

    fun bingShakeListener()
    {
        Statified.mSensorListener= object : SensorEventListener{
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

            override fun onSensorChanged(event: SensorEvent?) {
               val x= event?.values!![0]
                val y= event?.values!![1]
                val z= event?.values!![2]
                mAccelerationLast=mAccelerationCurrent
                mAccelerationCurrent=Math.sqrt(((x*x+y*y+z*z).toDouble())).toFloat()
                val delta=mAccelerationCurrent-mAccelerationLast
                mAccleration=mAccleration * 0.9f + delta

                if(mAccleration>12)
                {
                    val prefs=Statified.myActivity?.getSharedPreferences(
                        Statified.MY_PREFS_NAME,Context.MODE_PRIVATE)
                    val isAllowed =prefs?.getBoolean("feature",false)
                    if(isAllowed as Boolean)
                    {
                        Staticated.playNext("PlayNextNormal")
                    }


                }

            }


        }
    }




}
