package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.briot.tmmlmss.implementor.R

class DropAtLocation : Fragment() {

    companion object {
        fun newInstance() = DropAtLocation()
    }

    private lateinit var viewModel: DropAtLocationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.drop_at_location_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DropAtLocationViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
