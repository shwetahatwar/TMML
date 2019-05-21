package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.briot.tmmlmss.implementor.MainActivity

import com.briot.tmmlmss.implementor.R
import com.briot.tmmlmss.implementor.repository.local.PrefConstants
import com.briot.tmmlmss.implementor.repository.local.PrefRepository
import com.briot.tmmlmss.implementor.repository.remote.Asset
import com.briot.tmmlmss.implementor.repository.remote.Audit
import com.briot.tmmlmss.implementor.repository.remote.AuditDetails
import com.pascalwelsch.arrayadapter.ArrayAdapter
import io.github.pierry.progress.Progress
import kotlinx.android.synthetic.main.asset_details_scan_fragment.*
import kotlinx.android.synthetic.main.asset_item_list_row.view.*
import kotlinx.android.synthetic.main.picklist_product_fragment.*
import kotlinx.android.synthetic.main.selected_audit_fragment.*

class SelectedAuditFragment : androidx.fragment.app.Fragment() {

    companion object {
        fun newInstance() = SelectedAuditFragment()
    }

    private lateinit var viewModel: SelectedAuditViewModel
    private var progress: Progress? = null
    private var oldResponse: MutableList<Asset>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.selected_audit_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SelectedAuditViewModel::class.java)
        (this.activity as AppCompatActivity).setTitle("Selected Audit")

        pendingAuditAssetLists.adapter = SelectedAuditAssetsAdapter(this.context!!)
        pendingAuditAssetLists.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)


        viewModel.pendingAuditAssetsList.observe(this, Observer<List<Asset>> {
            MainActivity.hideProgress(this.progress)
            this.progress = null

            if (it != null && it != oldResponse) {
                for(i in 0 until it.size) {
                    (pendingAuditAssetLists.adapter as SelectedAuditAssetsAdapter).add(it[i])
                }
                (pendingAuditAssetLists.adapter as SelectedAuditAssetsAdapter).notifyDataSetChanged()
            }

            if (it != null) {
                oldResponse = it.toMutableList()
            }
        })

        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null

                MainActivity.showAlert(this.activity as AppCompatActivity, "Server is not reachable, please check if your internet connection is working");
            }
        })

        viewModel.lastAuditedAssetResult.observe(this, Observer<Asset> {
            MainActivity.hideProgress(this.progress)
            this.progress = null
            if (it != null && oldResponse != null && it.status != null) {
                for(i in 0 until oldResponse!!.size) {
                    val item = oldResponse!![i]
                    if (item.barcodeSerial.equals(it.barcodeSerial) && it.status.equals("Found")) {
                        oldResponse!![i].status = it.status
                         break
                    }
                }

                (pendingAuditAssetLists.adapter as SelectedAuditAssetsAdapter).clear()
                for(i in 0 until oldResponse!!.size) {
                    (pendingAuditAssetLists.adapter as SelectedAuditAssetsAdapter).add(oldResponse!![i])
                }
                (pendingAuditAssetLists.adapter as SelectedAuditAssetsAdapter).notifyDataSetChanged()
            }
        })

        viewModel.selectedAudit.observe(this, Observer<Audit> {
            if (it != null) {
                // go back to home screen
                // Navigation
                MainActivity.showAlert(this.activity as AppCompatActivity, "Report submitted for approval.");

            }
        })

        scanLocationButton.setOnClickListener {
            val auditId: String = PrefRepository.singleInstance.getValueOrDefault(PrefConstants().SELECTED_AUDIT_ID,"")
            val subLocationId: String = PrefRepository.singleInstance.getValueOrDefault(PrefConstants().SELECTED_AUDIT_SUBLOCATIONID,"")
            val subLocationBarcode: String = PrefRepository.singleInstance.getValueOrDefault(PrefConstants().SELECTED_AUDIT_SUBLOCATIONBC,"")
            val locationValue = scanLocationTextView.text.toString()
            if ((subLocationId.equals(locationValue) || subLocationBarcode.equals(locationValue)) && !auditId.isEmpty()) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.pendingAuditList(auditId)

                val subLocationName: String = PrefRepository.singleInstance.getValueOrDefault(PrefConstants().SELECTED_AUDIT_SUBLOCATIONNAME,"")

                locationLabelTextView.text = "Asset(s) at Location " + subLocationName

            } else {
                MainActivity.showAlert(this.activity as AppCompatActivity, "Scanned location is not matching for current Audit");
            }
        }

        submitAudit.setOnClickListener {
            this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
            val auditId: String = PrefRepository.singleInstance.getValueOrDefault(PrefConstants().SELECTED_AUDIT_ID,"")
            viewModel.submitAuditReport(auditId)
        }

        /*
        completePicklistProduct.setOnClickListener {
            if (!assetScanText.text.toString().isEmpty()) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")

            }

//            viewModel.loadAssetDetails(assetScanText.text.toString())
        }*/

        scanAssetTextView.setOnEditorActionListener { _, i, keyEvent ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_DONE || (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                val auditDetails: AuditDetails = AuditDetails()
                val auditId: String = PrefRepository.singleInstance.getValueOrDefault(PrefConstants().SELECTED_AUDIT_ID,"")
                val siteId: String = PrefRepository.singleInstance.getValueOrDefault(PrefConstants().SELECTED_AUDIT_SITEID,"")
                val locationId: String = PrefRepository.singleInstance.getValueOrDefault(PrefConstants().SELECTED_AUDIT_LOCATIONID,"")
                val subLocationId: String = PrefRepository.singleInstance.getValueOrDefault(PrefConstants().SELECTED_AUDIT_SUBLOCATIONID,"")
                val subLocationBarcode: String = PrefRepository.singleInstance.getValueOrDefault(PrefConstants().SELECTED_AUDIT_SUBLOCATIONBC,"")
                val locationValue = scanLocationTextView.text.toString()

                auditDetails.auditMasterId = auditId.toInt()
                auditDetails.barcodeSerial = scanAssetTextView.text.toString()
                auditDetails.siteId = siteId.toInt()
                auditDetails.locationId = locationId.toInt()
                auditDetails.subLocationId = subLocationId.toInt()
                auditDetails.status = "Found"
                viewModel.submitAssetAudit(auditDetails)

                handled = true
            }
            handled
        }

    }

}


class SelectedAuditAssetsAdapter(val context: Context) : ArrayAdapter<Asset, SelectedAuditAssetsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val assetItemHeadingId: TextView
        val assetItemValueId: TextView

        init {
            assetItemHeadingId = itemView.assetItemHeadingId as TextView
            assetItemValueId = itemView.assetItemTextId as TextView
        }
    }

    override fun getItemId(item: Asset): Any {
        return item
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) as Asset
        var displayItem: String = """${item.Asset?.barcodeSerial.toString()}"""
        var status: String = """${item.Asset?.assetDescription.toString()} """

        if (item.status.toString().equals("Pending")) {
            holder.assetItemHeadingId.setTextColor(Color.DKGRAY)
            holder.assetItemValueId.setTextColor(Color.DKGRAY)
        } else if (item.status.toString().equals("Found")) {
            holder.assetItemHeadingId.setTextColor(Color.BLUE)
            holder.assetItemValueId.setTextColor(Color.BLUE)
        }

        holder.assetItemHeadingId.setText(displayItem)
        holder.assetItemValueId.setText(status)

//        holder.auditlistId.setOnClickListener {
//            PrefRepository.singleInstance.setKeyValue(PrefConstants().PENDINGAUDITLISTID,item.id.toString())
//            Navigation.findNavController(it).navigate(R.id.action_auditListFragment_to_SelectedAuditFragment)
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.asset_item_list_row, parent, false)
        return ViewHolder(view)
    }
}
