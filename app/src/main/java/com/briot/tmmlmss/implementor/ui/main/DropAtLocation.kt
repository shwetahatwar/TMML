package com.briot.tmmlmss.implementor.ui.main

import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.briot.tmmlmss.implementor.MainActivity

import com.briot.tmmlmss.implementor.R
import com.briot.tmmlmss.implementor.repository.remote.JobLocationRelation
import io.github.pierry.progress.Progress
import kotlinx.android.synthetic.main.pending_item_dashboard_fragment.*
import kotlinx.android.synthetic.main.pending_item_row.view.*
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import com.pascalwelsch.arrayadapter.ArrayAdapter
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.drop_at_location_fragment.*
import kotlinx.android.synthetic.main.jobcard_details_scan_fragment.*

class DropAtLocation : Fragment() {

    companion object {
        fun newInstance() = DropAtLocation()
    }

    private lateinit var viewModel: DropAtLocationViewModel
    private var progress: Progress? = null
    private var oldJobLocationRelations: Array<JobLocationRelation>? = null
    private var selectedJobLocationRelationId: Number = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.drop_at_location_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DropAtLocationViewModel::class.java)

        (this.activity as AppCompatActivity).setTitle("Drop at Location")

        if (this.arguments != null) {
            selectedJobLocationRelationId = this.arguments!!.getInt("jobcardLocationRelationId")
        }

//        droppedLocationRecyclerView.adapter = DropItemsAdapter(this.context!!)
//        droppedLocationRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)

        viewModel.jobLocationRelations.observe(this, Observer<Array<JobLocationRelation>> {
            MainActivity.hideProgress(this.progress)
            this.progress = null

//            (droppedLocationRecyclerView.adapter as DropItemsAdapter).clear()
            if (it != null && it != oldJobLocationRelations) {
                MainActivity.showToast(this.activity as AppCompatActivity, "Dropped at location successfully");
                if (this.activity != null) {
                    Navigation.findNavController(this.activity!!, R.id.dropLocationFragmentId).navigateUp()
                }
//                for (item in it.iterator()) {
//                    (droppedLocationRecyclerView.adapter as DropItemsAdapter).add(item)
//                    (droppedLocationRecyclerView.adapter as DropItemsAdapter).notifyDataSetChanged()
//                }
            }

            if (it == null) {
                var message = "Unknown error has occurred. please retry scanning!"
                if (viewModel.errorMessage.value != null) {
                    message = viewModel.errorMessage.value.toString()
                }

                MainActivity.showToast(this.activity as AppCompatActivity, message);
                droplocationBarcodeTextView.text.clear()
                droplocationBarcodeTextView.requestFocus()
            }

            oldJobLocationRelations = it
        })

        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null

                MainActivity.showToast(this.activity as AppCompatActivity, "Server is not reachable, please check if your network connection is working");
            }
        })

        droplocationBarcodeTextView.setOnEditorActionListener { _, i, keyEvent ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_DONE || ((keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || keyEvent.keyCode == KeyEvent.KEYCODE_TAB) && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.dropLocationForPendingItem(selectedJobLocationRelationId, droplocationBarcodeTextView.text.toString())

                handled = true
            }
            handled
        }

        btnsubmit.setOnClickListener {
            var handled = false
            if (droplocationBarcodeTextView.text != null && droplocationBarcodeTextView.text.isNotEmpty()) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                var jobLocationRelationId = selectedJobLocationRelationId
                viewModel.dropLocationForPendingItem(jobLocationRelationId, droplocationBarcodeTextView.text.toString())

                handled = true
            }
            handled
        }

        droplocationBarcodeTextView.text.clear()
        droplocationBarcodeTextView.requestFocus()
    }

}
/*
class DropItemsAdapter(val context: Context) : ArrayAdapter<JobLocationRelation, DropItemsAdapter.ViewHolder>() {

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

    override fun getItemId(item: JobLocationRelation): Any {
        return item
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = getItem(position) as JobLocationRelation
        if  (item.updatedAt !=  null)  {
            val itemDate = Date((item.createdAt!!).toLong())
            val updatedDate = LocalDateTime.ofEpochSecond((item.updatedAt!!).toLong(), 1000000, ZoneOffset.UTC)

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val formatted = updatedDate.format(formatter)
            holder.updatedOn.text = formatted


        }

        if (item.jobcardId != null && item.jobcardId?.barcodeSerial != null) {
            holder.jobcardId.setText(item.jobcardId?.barcodeSerial)
        }

        if (item.processStatus != null) {
            holder.status.setText(item.processStatus)

            if (item.processStatus.equals("Pending")) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#f3f3f3"))
            } else if (item.processStatus.equals("In Buffer")) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#90C4DA"))
            } else if (item.processStatus.equals("Picked")) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#D2C179"))
            } else if (item.processStatus.equals("Complete")) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#BFE8BA"))
            }
        }

        if (item.sourceLocation != null && item.sourceLocation?.name != null) {
            holder.pickLocation.setText(item.sourceLocation?.name)
        }
        if (item.destinationLocationId != null && item.destinationLocationId?.name != null) {
            holder.dropLocation.setText(item.destinationLocationId?.name)

            holder.suggestedLocations.visibility = View.GONE
            holder.suggestedLocationsLabel.visibility = View.GONE
        } else {
            holder.dropLocation.visibility = View.GONE
            holder.dropLocationLabel.visibility = View.GONE
        }

        if (item.suggestedDropLocations != null) {
            holder.suggestedLocations.visibility = View.VISIBLE
            holder.suggestedLocationsLabel.visibility = View.VISIBLE
            holder.suggestedLocations.setText(item.suggestedDropLocations)
        } else {
            holder.suggestedLocations.visibility = View.GONE
            holder.suggestedLocationsLabel.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.pending_item_row, parent, false)
        return ViewHolder(view) /*.listen{pos, type ->
            val item = items.get(pos)
            val bundle = Bundle()
            Navigation.findNavController(view).navigate(R.id.action_pending_item_dashboard_fragment_to_dropatlocationfragment, bundle)
        }*/
    }
}
*/