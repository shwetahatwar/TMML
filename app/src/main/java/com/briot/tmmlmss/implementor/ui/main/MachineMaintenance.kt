package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.briot.tmmlmss.implementor.R

class MachineMaintenance : Fragment() {

    companion object {
        fun newInstance() = MachineMaintenance()
    }

    private lateinit var viewModel: MachineMaintenanceViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.machine_maintenance_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MachineMaintenanceViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
