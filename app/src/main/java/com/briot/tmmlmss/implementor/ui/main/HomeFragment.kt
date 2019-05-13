package com.briot.tmmlmss.implementor.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.briot.tmmlmss.implementor.R
import kotlinx.android.synthetic.main.home_fragment.*


class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        (this.activity as AppCompatActivity).setTitle("Briot AMS")

        productDetails.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_assetDetailsScanFragment) }
        auditAssets.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_assetAuditListFragment) }
        pickList.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_pickListFragment) }
        putAwayReport.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_putAwayReportFragment) }
    }

}
