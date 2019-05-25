package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.briot.tmmlmss.implementor.MainActivity
import com.briot.tmmlmss.implementor.R
import com.briot.tmmlmss.implementor.repository.remote.JobcardDetail
import com.pascalwelsch.arrayadapter.ArrayAdapter
import io.github.pierry.progress.Progress
import kotlinx.android.synthetic.main.asset_details_scan_fragment.*
import kotlinx.android.synthetic.main.asset_item_list_row.view.*


class AssetDetailsScanFragment : androidx.fragment.app.Fragment() {

    companion object {
        fun newInstance() = AssetDetailsScanFragment()
    }

    private lateinit var viewModel: JobcardDetailsScanViewModel
    private var progress: Progress? = null
    private var oldJobcardDetail: JobcardDetail? = null
    private var JobcardDetailItems: Int = 12

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.asset_details_scan_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(JobcardDetailsScanViewModel::class.java)

        (this.activity as AppCompatActivity).setTitle("Job Card Details")

        JobcardItemsList.adapter = JobcardDetailsItemsAdapter(this.context!!)
        JobcardItemsList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)


        viewModel.JobcardDetail.observe(this, Observer<Array<JobcardDetail>> {
            MainActivity.hideProgress(this.progress)
            this.progress = null


                for(i in 0 until JobcardDetailItems) {
                    if (it != null && it[i] != oldJobcardDetail) {
                    (JobcardItemsList.adapter as JobcardDetailsItemsAdapter).add(it[i])
                }
                (JobcardItemsList.adapter as JobcardDetailsItemsAdapter).notifyDataSetChanged()
                oldJobcardDetail = it[i]
            }


        })

        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null

                MainActivity.showAlert(this.activity as AppCompatActivity, "Server is not reachable, please check if your internet connection is working");
            }
        })

        viewJobcardDetails.setOnClickListener {
            this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
            viewModel.loadJobcardDetails(JobcardScanText.text.toString())
        }

        JobcardScanText.setOnEditorActionListener { _, i, keyEvent ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_DONE || (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.loadJobcardDetails(JobcardScanText.text.toString())

                handled = true
            }
            handled
        }
    }

}

class JobcardDetailsItemsAdapter(val context: Context) : ArrayAdapter<JobcardDetail, JobcardDetailsItemsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val JobcardItemHeadingId: TextView
        val JobcardItemValueId: TextView

        init {
            JobcardItemHeadingId = itemView.JobcardItemHeadingId as TextView
            JobcardItemValueId = itemView.JobcardItemTextId as TextView
        }
    }

    override fun getItemId(item: JobcardDetail): Any {
        return item
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = getItem(position) as JobcardDetail

        when (position) {
            0 -> {
                holder.JobcardItemHeadingId.setText("createdAt")
                holder.JobcardItemValueId.setText(item.createdAt.toString())
            }
            1 -> {
                holder.JobcardItemHeadingId.setText("updatedAt")
                holder.JobcardItemValueId.setText(item.updatedAt.toString())
            }
            2 -> {
                holder.JobcardItemHeadingId.setText("ID")
                holder.JobcardItemValueId.setText(item.id)
            }
            3 -> {
                holder.JobcardItemHeadingId.setText("requestedQuantity")
                holder.JobcardItemValueId.setText(item.requestedQuantity.toString())
            }
            4 -> {
                holder.JobcardItemHeadingId.setText("actualQuantity")
                holder.JobcardItemValueId.setText(item.actualQuantity.toString())
            }
            5 -> {
                holder.JobcardItemHeadingId.setText("status")
                holder.JobcardItemValueId.setText(item.status)
            }
            6 -> {
                holder.JobcardItemHeadingId.setText("estimatedDate")
                holder.JobcardItemValueId.setText(item.estimatedDate.toString())
            }
            7 -> {
                holder.JobcardItemHeadingId.setText("Barcode")
                holder.JobcardItemValueId.setText(item.barcodeSerial)
            }
            8 -> {
                holder.JobcardItemHeadingId.setText("productionSchedulePartRelationId")
                holder.JobcardItemValueId.setText(item.productionSchedulePartRelationId)
            }
            9 -> {
                holder.JobcardItemHeadingId.setText("trolleyId")
                holder.JobcardItemValueId.setText(item.trolleyId)
            }
            10 -> {
                holder.JobcardItemHeadingId.setText("createdBy")
                holder.JobcardItemValueId.setText(item.createdBy.toString())
            }
            11 -> {
                holder.JobcardItemHeadingId.setText("updatedBy")
                holder.JobcardItemValueId.setText(item.updatedBy.toString())
            }
            else -> {
                print("s does not encode x")
                holder.JobcardItemHeadingId.setText("")
                holder.JobcardItemValueId.setText("")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.asset_item_list_row, parent, false)
        return ViewHolder(view)
    }
}
