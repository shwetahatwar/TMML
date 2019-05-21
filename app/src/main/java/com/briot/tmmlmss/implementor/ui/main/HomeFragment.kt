package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.briot.tmmlmss.implementor.R
import kotlinx.android.synthetic.main.home_fragment.*


class HomeFragment : androidx.fragment.app.Fragment() {

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

        (this.activity as AppCompatActivity).setTitle("Home")

        productDetails.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_assetDetailsScanFragment) }
        auditAssets.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_assetAuditListFragment) }
        pickList.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_pickListFragment) }
        putAwayReport.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_putAwayReportFragment) }
    }

}
