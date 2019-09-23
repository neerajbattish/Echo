package com.ndscholar.echo.fragments


import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ndscholar.echo.R



class AboutFragment : Fragment() {

    var myActivity: Activity?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_about, container, false)
        activity?.title="Abou"

        return view
    }


}
