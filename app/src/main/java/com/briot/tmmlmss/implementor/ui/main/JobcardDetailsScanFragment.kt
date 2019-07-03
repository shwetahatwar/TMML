package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import androidx.appcompat.app.AppCompatActivity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.briot.tmmlmss.implementor.MainActivity
import com.briot.tmmlmss.implementor.R
import com.briot.tmmlmss.implementor.repository.remote.JobcardDetail
import com.briot.tmmlmss.implementor.repository.remote.ProductionSchedulePartRelation
import com.pascalwelsch.arrayadapter.ArrayAdapter
import io.github.pierry.progress.Progress
import kotlinx.android.synthetic.main.jobcard_details_scan_fragment.*
import kotlinx.android.synthetic.main.jobcard_item_list_row.view.*
import kotlinx.android.synthetic.main.key_value_info.view.*
import java.sql.Date
import java.util.Date as Date1


class JobcardDetailsScanFragment : Fragment() {

    companion object {
        fun newInstance() = JobcardDetailsScanFragment()
    }

    private lateinit var viewModel: JobcardDetailsScanViewModel
    private var progress: Progress? = null
    private var oldJobcardDetails: JobcardDetail? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.jobcard_details_scan_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(JobcardDetailsScanViewModel::class.java)

        (this.activity as AppCompatActivity).setTitle("Job Card Details")

        jobcardScanText.requestFocus()

        jobcardItemsList.adapter = JobcardDetailsItemsAdapter(this.context!!)
        jobcardItemsList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)
        jobcardResultId.visibility = View.GONE

        viewModel.jobcardDetails.observe(this, Observer<JobcardDetail> {
            MainActivity.hideProgress(this.progress)
            this.progress = null

            jobcardResultId.visibility = View.GONE
            (jobcardItemsList.adapter as JobcardDetailsItemsAdapter).clear()
            if (it != null && it != oldJobcardDetails) {
                (jobcardItemsList.adapter as JobcardDetailsItemsAdapter).add(it)
//                (jobcardItemsList.adapter as JobcardDetailsItemsAdapter).notifyDataSetChanged()

                // dismiss keyboard now
                if (activity != null) {
                    val keyboard = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    keyboard.hideSoftInputFromWindow(activity?.currentFocus?.getWindowToken(), 0)

                    // request for production schedule details to get the production details
                    if (it.productionSchedulePartRelationId != null && it.productionSchedulePartRelationId?.id != null) {
                        this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                        viewModel.loadProductionSchedule(it.productionSchedulePartRelationId!!.id!!)
                    }

                }


            }

            oldJobcardDetails = it

            if (it == null) {
                MainActivity.showToast(this.activity as AppCompatActivity, "Job card not found for scanned Barcode")
                jobcardScanText.text?.clear()
                jobcardScanText.requestFocus()
            }
        })

        viewModel.productionSchedulePartRelation.observe(this, Observer<ProductionSchedulePartRelation> {
            MainActivity.hideProgress(this.progress)
            this.progress = null

            if (it != null) {

                jobcardResultId.visibility = View.GONE

                for (i in 0..2) {

                    val linearLayout = LayoutInflater.from(context).inflate(R.layout.key_value_info, null)

                    when (i) {
                        0 -> {
                            linearLayout.headingTextView.text = "Schedule Status"
                            if (it.scheduleId != null && it.scheduleId?.status != null) {
                                linearLayout.contentTextView.text = it.scheduleId!!.status
                            } else {
                                linearLayout.contentTextView.text = "NA"
                            }
                        }
                        1 -> {
                            linearLayout.headingTextView.text = "Part Number"
                            if (it.partNumberId != null && it.partNumberId?.partNumber != null) {
                                linearLayout.contentTextView.text = it.partNumberId!!.partNumber
                            }
                        }
                        2 -> {
                            linearLayout.headingTextView.text = "Part Description"
                            if (it.partNumberId != null && it.partNumberId?.description != null) {
                                linearLayout.contentTextView.text = it.partNumberId!!.description
                            }
                        }
//                        4 -> {
//                            linearLayout.headingTextView.text = "SMH"
//                            if (it.partNumberId != null && it.partNumberId?.smh != null) {
//                                linearLayout.contentTextView.text = it.partNumberId!!.smh
//                            }
//                        }
                        else -> {

                        }
                    }

                    jobcardResultId.addView(linearLayout)

                }

                jobcardResultId.visibility = View.VISIBLE
                (jobcardItemsList.adapter as JobcardDetailsItemsAdapter).notifyDataSetChanged()
            }
        })

        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null

                MainActivity.showAlert(this.activity as AppCompatActivity, "Server is not reachable, please check if your network connection is working");
            }
        })

        jobcardScanText.setOnEditorActionListener { _, i, keyEvent ->
            var handled = false
            if ((jobcardScanText.text != null && jobcardScanText.text!!.isNotEmpty()) && i == EditorInfo.IME_ACTION_DONE || ((keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || keyEvent.keyCode == KeyEvent.KEYCODE_TAB) && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                jobcardResultId.removeAllViews()
                (jobcardItemsList.adapter as JobcardDetailsItemsAdapter).clear()
                viewModel.loadJobcardDetails(jobcardScanText.text.toString())

                handled = true
            }
            handled
        }

        viewJobcardDetails.setOnClickListener {
            if (jobcardScanText.text != null && jobcardScanText.text!!.isNotEmpty()) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                jobcardResultId.removeAllViews()
                (jobcardItemsList.adapter as JobcardDetailsItemsAdapter).clear()
                viewModel.loadJobcardDetails(jobcardScanText.text.toString())
            }
        }
    }

}

class JobcardDetailsItemsAdapter(val context: Context) : ArrayAdapter<JobcardDetail, JobcardDetailsItemsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val createdAtItemHeadingId: TextView
        val createdAtItemValueId: TextView
        val updatedAtItemHeadingId: TextView
        val updatedAtItemValueId: TextView
        val requestedQuantityItemHeadingId: TextView
        val requestedQuantityItemTextId: TextView
        val actualQuantityItemHeadingId: TextView
        val actualQuantityItemTextId: TextView
        val statusItemHeadingId: TextView
        val statusItemTextId: TextView
        val estimatedDateItemHeadingId: TextView
        val estimatedDateItemTextId: TextView
        val barcodeItemHeadingId: TextView
        val barcodeItemTextId: TextView
        val createdByItemHeadingId: TextView
        val createdByItemTextId: TextView
        val updatedByItemHeadingId: TextView
        val updatedByItemTextId: TextView
        var trolleyIdBarcodeText: TextView
        var trolleyIdTypeIdText: TextView
        var trolleyIdCapacityText: TextView

        init {
            createdAtItemHeadingId = itemView.createdAtItemHeadingId as TextView
            createdAtItemValueId = itemView.createdAtItemTextId as TextView
            updatedAtItemHeadingId = itemView.updatedAtItemHeadingId as TextView
            updatedAtItemValueId = itemView.updatedAtItemTextId as TextView
            requestedQuantityItemHeadingId = itemView.requestedQuantityItemHeadingId as TextView
            requestedQuantityItemTextId = itemView.requestedQuantityItemTextId as TextView
            actualQuantityItemHeadingId = itemView.actualQuantityItemHeadingId as TextView
            actualQuantityItemTextId = itemView.actualQuantityItemTextId as TextView
            statusItemHeadingId = itemView.statusItemHeadingId as TextView
            statusItemTextId = itemView.statusItemTextId as TextView
            estimatedDateItemHeadingId = itemView.estimatedDateItemHeadingId as TextView
            estimatedDateItemTextId = itemView.estimatedDateItemTextId as TextView
            barcodeItemHeadingId = itemView.barcodeItemHeadingId as TextView
            barcodeItemTextId = itemView.barcodeItemTextId as TextView

            createdByItemHeadingId = itemView.createdByItemHeadingId as TextView
            createdByItemTextId = itemView.createdByItemTextId as TextView
            updatedByItemHeadingId = itemView.updatedByItemHeadingId as TextView
            updatedByItemTextId = itemView.updatedByItemTextId as TextView

            trolleyIdBarcodeText = itemView.trolleyIdBarcodeText as TextView
            trolleyIdTypeIdText = itemView.trolleyIdTypeIdText as TextView
            trolleyIdCapacityText = itemView.trolleyIdCapacityText as TextView
        }
    }

    override fun getItemId(item: JobcardDetail): Any {
        return item
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = getItem(position) as JobcardDetail

        holder.createdAtItemHeadingId.setText("Created On")
        if (item.createdAt != null) {
            val createDate = Date((item.createdAt!!).toLong())
            holder.createdAtItemValueId.setText(createDate.toString())
        } else {
            holder.createdAtItemValueId.setText("")
        }

        holder.updatedAtItemHeadingId.setText("Updated On")
        if (item.updatedAt != null) {
            val updateDate = Date((item.updatedAt!!).toLong())
            holder.updatedAtItemValueId.setText(updateDate.toString())
        } else {
            holder.updatedAtItemValueId.setText("")
        }

        holder.requestedQuantityItemHeadingId.setText("Requested Quantity")
        holder.requestedQuantityItemTextId.setText(item.requestedQuantity.toString())
        holder.actualQuantityItemHeadingId.setText("Actual Quantity")
        holder.actualQuantityItemTextId.setText(item.actualQuantity.toString())
        holder.statusItemHeadingId.setText("Status")
        holder.statusItemTextId.setText(item.jobcardStatus)
        holder.estimatedDateItemHeadingId.setText("Estimated Date")
        holder.estimatedDateItemTextId.setText(item.estimatedDate.toString())
        holder.barcodeItemHeadingId.setText("Barcode")
        holder.barcodeItemTextId.setText(item.barcodeSerial)

        holder.createdByItemHeadingId.setText("Created By")
        if (item.createdBy != null) {
            holder.createdByItemTextId.setText(item.createdBy.toString())
        } else {
            holder.createdByItemTextId.setText("NA")
        }

        holder.updatedByItemHeadingId.setText("Updated By")
        if (item.updatedBy != null) {
            holder.updatedByItemTextId.setText(item.updatedBy.toString())
        } else {
            holder.updatedByItemTextId.setText("NA")
        }

        if (item.trolleyId != null) {
            holder.trolleyIdBarcodeText.setText(item.trolleyId!!.barcodeSerial)
        } else {
            holder.trolleyIdBarcodeText.setText("NA")
        }

        if (item.trolleyId != null) {
            holder.trolleyIdTypeIdText.setText(item.trolleyId!!.typeId)
        } else {
            holder.trolleyIdTypeIdText.setText("NA")
        }

        if (item.trolleyId != null) {
            holder.trolleyIdCapacityText.setText(item.trolleyId!!.capacity)
        } else {
            holder.trolleyIdCapacityText.setText("NA")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.jobcard_item_list_row, parent, false)
        return ViewHolder(view)
    }
}
