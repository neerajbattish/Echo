package com.ndscholar.echo.adapters

import android.content.Context

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.ndscholar.echo.R
import com.ndscholar.echo.activities.MainActivity
import com.ndscholar.echo.fragments.*

class NavigationDrawerAdapter(_contentList : ArrayList<String>, _getImages: IntArray, _context:Context):RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>()
{
    var contentList:ArrayList<String>?=null
    var getImages: IntArray?=null
    var mContext: Context?=null
    init{
        this.contentList=_contentList
        this.getImages=_getImages
        this.mContext=_context
    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NavigationDrawerAdapter.NavViewHolder {

            var itemView = LayoutInflater.from(p0.context)
                .inflate(R.layout.row_custom_navigationdrawer, p0, false)
            val returnThis = NavViewHolder(itemView)
            return returnThis


      // return
    }



    override fun getItemCount(): Int {
       return (contentList as ArrayList).size
    }

    override fun onBindViewHolder(p0: NavigationDrawerAdapter.NavViewHolder, p1: Int) {
        try {
            p0?.icon_GET?.setBackgroundResource(getImages?.get(p1) as Int)
            p0?.text_GET?.setText(contentList?.get(p1))
            p0?.contentHolder?.setOnClickListener({
                if (p1 == 0) {
                    val mainScreenFragment = MainScreenFragment()
                    (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragments, mainScreenFragment)
                        .commit()
                } else if (p1 == 1) {
                    val favourateFragment = FavoriteFragment()
                    (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragments, favourateFragment)
                        .commit()
                } else if (p1 == 2) {
                    val settingFragments = SettingsFragment()
                    (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragments, settingFragments)
                        .commit()
                } else if(p1==3)  {
                    val aboutUsFragments = AboutUsFragment()
                    (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragments, aboutUsFragments)
                        .commit()

                }
                MainActivity.Statified.drawerLayout?.closeDrawers()
            })
        }
        catch(e: Exception)
        {
            e.printStackTrace()

        }
    }

    class NavViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        var icon_GET: ImageView?=null
        var text_GET: TextView?=null
        var contentHolder: RelativeLayout?=null
        init {

                icon_GET=itemView?.findViewById(R.id.icon_navdrawer)
                text_GET=itemView?.findViewById(R.id.text_navdrawer)
            contentHolder=itemView?.findViewById(R.id.navdrawer_item_content_holder)

        }
    }

}