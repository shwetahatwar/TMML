package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import com.briot.tmmlmss.implementor.MainActivity
import com.pascalwelsch.arrayadapter.ArrayAdapter
import com.briot.tmmlmss.implementor.R
import com.briot.tmmlmss.implementor.repository.local.PrefConstants
import com.briot.tmmlmss.implementor.repository.local.PrefRepository
import com.briot.tmmlmss.implementor.repository.remote.Audit
import io.github.pierry.progress.Progress
import kotlinx.android.synthetic.main.asset_audit_list_fragment.*
import kotlinx.android.synthetic.main.selected_audit_fragment.*
import kotlinx.android.synthetic.main.pending_auditlist_list_row.view.*

class AssetAuditListFragment : androidx.fragment.app.Fragment() {

    companion object {
        fun newInstance() = AssetAuditListFragment()
    }

    private lateinit var viewModel: AssetAuditListViewModel
    private var progress: Progress? = null
    private var oldResponse: List<Audit>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.asset_audit_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AssetAuditListViewModel::class.java)

        (this.activity as AppCompatActivity).setTitle("Pending Audit List")

        pendingAuditLists.adapter = PendingAuditAdapter(this.context!!)
        pendingAuditLists.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)

        viewModel.pendingAuditList.observe(this, Observer<List<Audit>> {
            MainActivity.hideProgress(this.progress)
            this.progress = null

            if (it != null && it != oldResponse) {
                for(i in 0 until it.size){
                    (pendingAuditLists.adapter as PendingAuditAdapter).add(it[i])
                }

                (pendingAuditLists.adapter as PendingAuditAdapter).notifyDataSetChanged()
            }
            oldResponse = it
        })

        viewModel.pendingAuditList()
        this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")

        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null

                MainActivity.showAlert(this.activity as AppCompatActivity, "Server is not reachable, please check if your internet connection is working");
            }
        })
    }
}

class PendingAuditAdapter(val context: Context) : ArrayAdapter<Audit, PendingAuditAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val auditlistId: TextView

        init {
            auditlistId = itemView.auditlistId as TextView
        }
    }

    override fun getItemId(item: Audit): Any {
        return item
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) as Audit
        var displayItem: String = item.id.toString() + " - " + item.createdAt.toString()
        holder.auditlistId.setText(displayItem)

        holder.auditlistId.setOnClickListener {
            PrefRepository.singleInstance.setKeyValue(PrefConstants().SELECTED_AUDIT_ID,item.id.toString())

            PrefRepository.singleInstance.setKeyValue(PrefConstants().SELECTED_AUDIT_SITEID,item.siteId.toString())
            PrefRepository.singleInstance.setKeyValue(PrefConstants().SELECTED_AUDIT_LOCATIONID,item.locationId.toString())
            PrefRepository.singleInstance.setKeyValue(PrefConstants().SELECTED_AUDIT_SUBLOCATIONID,item.subLocationId.toString())
            PrefRepository.singleInstance.setKeyValue(PrefConstants().SELECTED_AUDIT_SUBLOCATIONBC, item.SubLocation?.barcodeSerial.toString())
            PrefRepository.singleInstance.setKeyValue(PrefConstants().SELECTED_AUDIT_SUBLOCATIONNAME,item.SubLocation?.name.toString())
            Navigation.findNavController(it).navigate(R.id.action_auditListFragment_to_SelectedAuditFragment)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.pending_auditlist_list_row, parent, false)
        return ViewHolder(view)
    }
}
