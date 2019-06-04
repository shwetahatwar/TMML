package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.briot.tmmlmss.implementor.R

class PendingItemDashboard : Fragment() {

    companion object {
        fun newInstance() = PendingItemDashboard()
    }

    private lateinit var viewModel: PendingItemDashboardViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.pending_item_dashboard_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PendingItemDashboardViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
