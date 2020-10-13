package com.briot.tmmlmss.implementor.ui.main

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.briot.tmmlmss.implementor.R
import com.briot.tmmlmss.implementor.repository.remote.RoleAccessRelation
import kotlinx.android.synthetic.main.home_fragment.*
import androidx.lifecycle.Observer
import com.briot.tmmlmss.implementor.BuildConfig
import com.briot.tmmlmss.implementor.repository.local.PrefConstants
import com.briot.tmmlmss.implementor.repository.local.PrefRepository


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

        this.viewModel.roleAccessRelations.observe(this, Observer<Array<RoleAccessRelation>> {
            if (it != null) {
                val roleName = PrefRepository.singleInstance.getValueOrDefault(PrefConstants().ROLE_NAME, "")
                val roleId = PrefRepository.singleInstance.getValueOrDefault(PrefConstants().ROLE_ID, "0").toInt()
                if (roleName.toLowerCase().equals("admin")) {
                    viewStatus(true)
                } else {
                    for (item in it) {

                        if (item.roleId?.id?.toInt() == roleId) {
                            if (item.accessId?.uri?.toLowerCase().equals("/JobProcessSequenceRelation/create".toLowerCase())) {
                                startPartProcess.visibility = View.VISIBLE
                            } else if (item.accessId?.uri?.toLowerCase().equals("/jobProcessSequenceRelation/update".toLowerCase())) {
                                stopPartProcess.visibility =  View.VISIBLE
                            } else if (item.accessId?.uri?.toLowerCase().equals("/joblocationrelation".toLowerCase())) {
                                pendingItemsDashboard.visibility = View.VISIBLE
                                receiveAtStore.visibility = View.VISIBLE
                            } else if (item.accessId?.uri?.toLowerCase().equals("/MaintenanceTransaction/update".toLowerCase())) {
                                machineMaintenance.visibility = View.VISIBLE
                            }
                        }
                    }
                }

            }

        })

        this.viewModel.loadRoleAccess()
        versiontext.text = "app version " + BuildConfig.VERSION_NAME;

        // hide all options initially,  enable it as per role only
        viewStatus(false)


        jobCardDetails.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_jobcardDetailsScanFragment) }
        startPartProcess.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_startpartprocessfragment) }
        stopPartProcess.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_stoppartprocessfragment) }
        pendingItemsDashboard.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_pendingItemDashboard) }
        machineMaintenance.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_machinemaintenancefragment) }
        machineWiseJobCardDetails.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_machineWiseJobcardDetailsFragment) }
        receiveAtStore.setOnClickListener { Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_receiveAtStoreFragment) }
    }

    fun viewStatus(show: Boolean) {
        if (show) {
//            jobCardDetails.visibility = View.VISIBLE
            machineWiseJobCardDetails.visibility = View.VISIBLE
            machineMaintenance.visibility = View.VISIBLE
            startPartProcess.visibility = View.VISIBLE
            stopPartProcess.visibility = View.VISIBLE
            pendingItemsDashboard.visibility = View.VISIBLE
            receiveAtStore.visibility = View.VISIBLE
        } else {
//            jobCardDetails.visibility = View.GONE
            machineWiseJobCardDetails.visibility = View.VISIBLE
            machineMaintenance.visibility = View.GONE
            startPartProcess.visibility = View.GONE
            stopPartProcess.visibility = View.GONE
            pendingItemsDashboard.visibility = View.GONE
            receiveAtStore.visibility = View.GONE
        }

    }

}
