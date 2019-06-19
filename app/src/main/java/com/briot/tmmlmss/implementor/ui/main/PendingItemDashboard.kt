package com.briot.tmmlmss.implementor.ui.main

import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
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
import io.github.pierry.progress.Progress
import kotlinx.android.synthetic.main.pending_item_dashboard_fragment.*
import kotlinx.android.synthetic.main.pending_item_row.view.*
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class PendingItemDashboard : Fragment() {

    companion object {
        fun newInstance() = PendingItemDashboard()
    }

    private lateinit var viewModel: PendingItemDashboardViewModel
    private var progress: Progress? = null
    private var oldJobLocationRelations: Array<JobLocationRelation>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.pending_item_dashboard_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PendingItemDashboardViewModel::class.java)

        (this.activity as AppCompatActivity).setTitle("Pending Items")

        pendingItemsRecyclerView.adapter = PendingItemsAdapter(this.context!!)
        pendingItemsRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)

        viewModel.jobLocations.observe(this, Observer<Array<JobLocationRelation>> {
            MainActivity.hideProgress(this.progress)
            this.progress = null

            (pendingItemsRecyclerView.adapter as PendingItemsAdapter).clear()
            if (it != null && it != oldJobLocationRelations) {
                oldJobLocationRelations = it
            }

            if (oldJobLocationRelations != null) {
                for (item in it.iterator()) {
                    (pendingItemsRecyclerView.adapter as PendingItemsAdapter).add(item)
                    (pendingItemsRecyclerView.adapter as PendingItemsAdapter).notifyDataSetChanged()
                }
            }

        })

        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null

                MainActivity.showToast(this.activity as AppCompatActivity, "Server is not reachable, please check if your network connection is working");
            }
        })

        if (viewModel.jobLocations.value.isNullOrEmpty()) {
            this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
            viewModel.loadPendingItems()
        }
    }

}

/*fun <T : RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(getAdapterPosition(), getItemViewType())
    }
    return this
}*/

class PendingItemsAdapter(val context: Context) : ArrayAdapter<JobLocationRelation, PendingItemsAdapter.ViewHolder>() {

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

        if (item.status != null) {
            holder.status.setText(item.status)

            if (item.status.equals("Pending")) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#f3f3f3"))
            } else if (item.status.equals("In Buffer")) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#C9E1ED"))
            } else if (item.status.equals("Picked")) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#D2C179"))
            } else if (item.status.equals("Complete")) {
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

        if (item.multiplyMachines != null) {
            holder.suggestedLocations.visibility = View.VISIBLE
            holder.suggestedLocationsLabel.visibility = View.VISIBLE
            holder.suggestedLocations.setText(item.multiplyMachines)
        } else {
            holder.suggestedLocations.visibility = View.GONE
            holder.suggestedLocationsLabel.visibility = View.GONE
        }

        holder.cardView.setOnClickListener{
            //            PrefRepository.singleInstance.setKeyValue(PrefConstants().PENDINGAUDITLISTID,item.id.toString())
            val bundle = Bundle()
            if (item.id != null) {
                bundle.putInt("jobcardLocationRelationId", item.id!!.toInt())
                Navigation.findNavController(it).navigate(R.id.action_pending_item_dashboard_fragment_to_dropatlocationfragment, bundle)
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