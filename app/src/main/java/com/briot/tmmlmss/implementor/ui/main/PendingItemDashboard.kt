package com.briot.tmmlmss.implementor.ui.main

import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.format.DateUtils
import android.text.format.DateUtils.*
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.Navigation.*
import androidx.recyclerview.widget.RecyclerView
import com.briot.tmmlmss.implementor.MainActivity

import com.briot.tmmlmss.implementor.R
import com.pascalwelsch.arrayadapter.ArrayAdapter
import com.briot.tmmlmss.implementor.repository.remote.JobLocationRelation
import com.briot.tmmlmss.implementor.repository.remote.JobLocationRelationDetailed
import io.github.pierry.progress.Progress
import kotlinx.android.synthetic.main.jobcard_details_scan_fragment.*
import kotlinx.android.synthetic.main.pending_item_dashboard_fragment.*
import kotlinx.android.synthetic.main.pending_item_row.view.*
//import java.time.LocalDateTime
//import java.time.ZoneOffset
//import java.time.format.DateTimeFormatter
import java.util.*

class PendingItemDashboard : Fragment() {

    companion object {
        fun newInstance() = PendingItemDashboard()
    }

    private lateinit var viewModel: PendingItemDashboardViewModel
    private var progress: Progress? = null
    private var oldJobLocationRelations: Array<JobLocationRelationDetailed>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.pending_item_dashboard_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PendingItemDashboardViewModel::class.java)

        (this.activity as AppCompatActivity).setTitle("Pending Items")

        pendingItemsRecyclerView.adapter = PendingItemsAdapter(this.context!!, this)
        pendingItemsRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)

        viewModel.resultJobLocations.observe(this, Observer<Array<JobLocationRelationDetailed>> {
            MainActivity.hideProgress(this.progress)
            this.progress = null


            (pendingItemsRecyclerView.adapter as PendingItemsAdapter).clear()
            if (it != null) {
                oldJobLocationRelations = it
                if (oldJobLocationRelations != null) {
                    for (item in oldJobLocationRelations!!.iterator()) {
                        (pendingItemsRecyclerView.adapter as PendingItemsAdapter).add(item)
                        (pendingItemsRecyclerView.adapter as PendingItemsAdapter).notifyDataSetChanged()
                    }
                }
            }
            pendingJobcardScanText.text?.clear()
            pendingJobcardScanText.requestFocus()
        })

        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null

                MainActivity.showToast(this.activity as AppCompatActivity, "Server is not reachable, please check if your network connection is working");
            }
        })

        viewModel.pickStatus.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null

                MainActivity.showToast(this.activity as AppCompatActivity, "Item Picked successfully");
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
//                viewModel.loadPendingItems()
                (pendingItemsRecyclerView.adapter as PendingItemsAdapter).clear()
            }
        })

        this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")

        pendingJobcardScanText.setOnEditorActionListener { _, i, keyEvent ->
            var handled = false
            if (keyEvent == null) {
                Log.d("PendingItemDashboard: ", "event is null")
//                Navigation.findNavController(pendingJobcardScanText).navigate(R.id.action_pending_item_dashboard_fragment_to_dropatlocationfragment)
            } else if ((pendingJobcardScanText.text != null && pendingJobcardScanText.text!!.isNotEmpty()) && i == EditorInfo.IME_ACTION_DONE || ((keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || keyEvent.keyCode == KeyEvent.KEYCODE_TAB) && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                (pendingItemsRecyclerView.adapter as PendingItemsAdapter).clear()
                viewModel.loadPendingItems(pendingJobcardScanText.text!!.toString())
//                viewModel.filterPendingItems()
//                MainActivity.hideProgress(this.progress)
//                this.progress = null

                handled = true
            }
            handled
        }

    }

    fun userSelecteditem(jobLocationRelation: JobLocationRelationDetailed) {
        val parentActivity = this
        AlertDialog.Builder(this.activity as AppCompatActivity, R.style.MyDialogTheme).create().apply {
            setTitle("Alert")
            setMessage("Do you want to pick this Item?")
            setButton(AlertDialog.BUTTON_POSITIVE, "Yes", { dialog, _ ->
                if (jobLocationRelation.id != null) {
                    parentActivity.progress = MainActivity.showProgressIndicator(parentActivity.activity as AppCompatActivity, "Please wait")
                    parentActivity.viewModel.pickItem(jobLocationRelation.id!!, "Picked")
                }
                dialog.dismiss()
            })
            setButton(AlertDialog.BUTTON_NEGATIVE, "No", { dialog, _ ->
                dialog.dismiss()
            })
            show()
        }
    }
}

/*fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(getAdapterPosition(), getItemViewType())
    }
    return this
}*/

class PendingItemsAdapter(val context: Context, fragment: PendingItemDashboard?) : ArrayAdapter<JobLocationRelationDetailed, PendingItemsAdapter.ViewHolder>() {

    val viewFragment = fragment

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val cardView: CardView
        val updatedOn: TextView
        val jobcardId: TextView
        val pickLocation: TextView
        val dropLocation: TextView
        val suggestedLocations: TextView
        val status: TextView

        val updatedOnLabel: TextView
        val jobCardLabel: TextView
        val pickLocationLabel: TextView
        val dropLocationLabel: TextView
        val statusLabel: TextView
        val suggestedLocationsLabel: TextView

        init {
            cardView = itemView.pendingItemCard as CardView
            updatedOn = itemView.pendingItemUpdateDateId as TextView
            jobcardId = itemView.pendingItemJobcardId as TextView
            status = itemView.pendingItemStatus as TextView
            dropLocation = itemView.dropLocation as TextView
            pickLocation = itemView.pickLocation as TextView
            suggestedLocations = itemView.suggestedLocation as TextView

            updatedOnLabel = itemView.pendingItemUpdateDateLabel as TextView
            jobCardLabel = itemView.pendingItemJobcardLabel as TextView
            statusLabel = itemView.pendingItemStatusLabel as TextView
            pickLocationLabel = itemView.pickLocationLabel as TextView
            dropLocationLabel = itemView.dropLocationLabel as TextView
            suggestedLocationsLabel = itemView.suggestedLocationLabel as TextView
        }

    }

    override fun getItemId(item: JobLocationRelationDetailed): Any {
        return item
    }

//    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = getItem(position) as JobLocationRelationDetailed
        if  (item.updatedAt !=  null)  {
            val itemDate = Date((item.createdAt!!).toLong())
            val dateFlags = FORMAT_SHOW_DATE + FORMAT_SHOW_TIME + FORMAT_SHOW_YEAR + FORMAT_ABBREV_MONTH
            val updatedDate = DateUtils.formatDateTime(context, ((item.createdAt!!).toLong()), dateFlags)
            holder.updatedOn.text = updatedDate
        }

        if (item.jobcardId != null && item.jobcardId?.barcodeSerial != null) {
            holder.jobcardId.setText(item.jobcardId?.barcodeSerial)
        } else {
            holder.jobCardLabel.visibility = View.GONE
            holder.jobcardId.visibility = View.GONE
        }

        if (item.processStatus != null) {
            holder.status.setText(item.processStatus)

            if (item.processStatus.equals("Pending")) {
                holder.cardView.setCardBackgroundColor(Color.CYAN) //Color.parseColor("#f3f3f3")
            } else if (item.processStatus.equals("In Buffer")) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#C9E1ED"))
            } else if (item.processStatus.equals("Picked")) {
                holder.cardView.setCardBackgroundColor(Color.YELLOW) // Color.parseColor("#D2C179")
            } else if (item.processStatus.equals("Complete")) {
                holder.cardView.setCardBackgroundColor(Color.GREEN) // Color.parseColor("#BFE8BA")
            }
        }

        if (item.sourceLocation != null && item.sourceLocation?.name != null) {
            holder.pickLocation.setText(item.sourceLocation?.name)
        } else {
            holder.pickLocation.visibility = View.GONE
            holder.pickLocationLabel.visibility - View.GONE
            holder.pickLocation.text = "NA"
        }

        if (item.destinationLocationId != null && item.destinationLocationId?.name != null) {
            holder.dropLocation.setText(item.destinationLocationId?.name)

            holder.suggestedLocations.visibility = View.GONE
            holder.suggestedLocationsLabel.visibility = View.GONE
        } else {
            holder.dropLocation.visibility = View.GONE
            holder.dropLocationLabel.visibility = View.GONE
            holder.dropLocation.text = "NA"
        }

        if (item.suggestedDropLocations != null) {
            holder.suggestedLocations.visibility = View.VISIBLE
            holder.suggestedLocationsLabel.visibility = View.VISIBLE
            holder.suggestedLocations.setText(item.suggestedDropLocations)
        } else {
            holder.suggestedLocations.visibility = View.GONE
            holder.suggestedLocationsLabel.visibility = View.GONE
            holder.suggestedLocations.text = "NA"

        }

        holder.cardView.setOnClickListener{
            //            PrefRepository.singleInstance.setKeyValue(PrefConstants().PENDINGAUDITLISTID,item.id.toString())
            val bundle = Bundle()
            if (item.id != null) {
                val itemStatus = item.processStatus?.toLowerCase()
                if (itemStatus.equals("picked")) {
                    bundle.putInt("jobcardLocationRelationId", item.id!!.toInt())
                    Navigation.findNavController(it).navigate(R.id.action_pending_item_dashboard_fragment_to_dropatlocationfragment, bundle)
                } else if (itemStatus.equals("pending") && item.id != null) {
                    viewFragment?.userSelecteditem(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.pending_item_row, parent, false)
        return ViewHolder(view)

                /*.listen{pos, type ->
            val item = items.get(pos)
            val bundle = Bundle()
//            Navigation.findNavController(view).navigate(R.id.action_pending_item_dashboard_fragment_to_dropatlocationfragment, bundle)

            if (item.status.equals("Picked")) {
                // take user to drop location screen
            } else if (item.status.equals("Pending")) {
                // allow user to confirm pick
            }
        }*/
    }
}