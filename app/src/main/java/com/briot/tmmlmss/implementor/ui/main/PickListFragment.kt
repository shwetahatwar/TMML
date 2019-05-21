package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
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
import com.briot.tmmlmss.implementor.R
import com.briot.tmmlmss.implementor.repository.local.PrefConstants
import com.briot.tmmlmss.implementor.repository.local.PrefRepository
import com.briot.tmmlmss.implementor.repository.remote.PendingPicklist
import com.pascalwelsch.arrayadapter.ArrayAdapter
import io.github.pierry.progress.Progress
import kotlinx.android.synthetic.main.pending_picklist_list_row.view.*
import kotlinx.android.synthetic.main.pick_list_fragment.*


class PickListFragment : androidx.fragment.app.Fragment() {

    companion object {
        fun newInstance() = PickListFragment()
    }

    private lateinit var viewModel: PickListViewModel
    private var progress: Progress? = null
    private var oldResponse: List<PendingPicklist>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.pick_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PickListViewModel::class.java)

        pendingPicklists.adapter = PendingAdapter(this.context!!)
        pendingPicklists.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)

        viewModel.pending.observe(this, Observer<List<PendingPicklist>> {
            if (it != null && it != oldResponse) {
                for(i in 0 until it.size){
                    (pendingPicklists.adapter as PendingAdapter).add(it[i])
                }

                (pendingPicklists.adapter as PendingAdapter).notifyDataSetChanged()
            }
            oldResponse = it
        })

        viewModel.picklistSubmit()

        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null

                MainActivity.showAlert(this.activity as AppCompatActivity, "Server is not reachable, please check if your internet connection is working");
            }
        })
        // TODO: Use the ViewModel
    }

}

class PendingAdapter(val context: Context) : ArrayAdapter<PendingPicklist, PendingAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val picklistId: TextView

        init {
            picklistId = itemView.picklistId as TextView
        }
    }

    override fun getItemId(item: PendingPicklist): Any {
        return item
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) as PendingPicklist
        holder.picklistId.setText(item.PicklistID)

        holder.picklistId.setOnClickListener {
            PrefRepository.singleInstance.setKeyValue(PrefConstants().PICKLISTID,item.PicklistID!!)
            Navigation.findNavController(it).navigate(R.id.action_auditListFragment_to_SelectedAuditFragment)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.pending_picklist_list_row, parent, false)
        return ViewHolder(view)
    }
}