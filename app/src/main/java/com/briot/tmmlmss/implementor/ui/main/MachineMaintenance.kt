package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.briot.tmmlmss.implementor.MainActivity
import com.briot.tmmlmss.implementor.R
import com.briot.tmmlmss.implementor.repository.remote.Machine
import com.pascalwelsch.arrayadapter.ArrayAdapter
import io.github.pierry.progress.Progress
import kotlinx.android.synthetic.main.machine_maintenance_fragment.*
import kotlinx.android.synthetic.main.machine_item_list_row.view.*
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.widget.Spinner


class MachineMaintenance : Fragment()  {


    companion object {
        fun newInstance() = MachineMaintenance()

    }

    private lateinit var viewModel: MachineMaintenanceViewModel
    private var progress: Progress? = null
    private var oldMachineDetail: Array<Machine>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.machine_maintenance_fragment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val arraySpinner = resources.getStringArray(R.array.machine_state_array) // = arrayOf("Select","Available","Occupied","Waiting For Maintenance","Under Maintenance","Machine break down")
//        val machineSpinner = view.findViewById(R.id.machineStateSpinner) as Spinner
//        machineSpinner.adapter = ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, arraySpinner)
//        machineSpinner.adapter.setDr setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        machineSpinner.adapter = adapter

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MachineMaintenanceViewModel::class.java)
        // TODO: Use the ViewModel

//        Navigation.findNavController(btnsubmit).navigate(R.id.action_loginFragment_to_homeFragment)

        (this.activity as AppCompatActivity).setTitle("Machine Maintenance")

        machineItemsList.adapter = MachineDetailsItemsAdapter(this.context!!)
        machineItemsList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)


        viewModel.machineDetail.observe(this, Observer<Array<Machine>> {
            MainActivity.hideProgress(this.progress)
            this.progress = null

            (machineItemsList.adapter as MachineDetailsItemsAdapter).clear()
            if (it != null && it!= oldMachineDetail) {

                for(i in 0 until it.size) {
                    (machineItemsList.adapter as MachineDetailsItemsAdapter).add(it[i])
                }
                (machineItemsList.adapter as MachineDetailsItemsAdapter).notifyDataSetChanged()
            }

            oldMachineDetail = it
        })

        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null

                MainActivity.showAlert(this.activity as AppCompatActivity, "Server is not reachable, please check if your internet connection is working");
            }
        })

        btnsubmit.setOnClickListener {
            this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
            viewModel.loadMachineDetails(MachineScanText.text.toString())
        }

        MachineScanText.setOnEditorActionListener { _, i, keyEvent ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_DONE || (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.loadMachineDetails(MachineScanText.text.toString())

                handled = true
            }
            handled
        }
    }

}

class MachineDetailsItemsAdapter(val context: Context) : ArrayAdapter<Machine, MachineDetailsItemsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val currentStatusHeadingId: TextView
        val currentStatusItemTextId: TextView
        val currentDateHeadingId: TextView
        val currentDateTextId: TextView
        val currentUserHeadingId: TextView
        val currentUserTextId: TextView

        init {
            currentStatusHeadingId = itemView.currentStatusHeadingId as TextView
            currentStatusItemTextId = itemView.currentStatusItemTextId as TextView
            currentDateHeadingId = itemView.currentDateHeadingId as TextView
            currentDateTextId = itemView.currentDateTextId as TextView
            currentUserHeadingId = itemView.currentUserHeadingId as TextView
            currentUserTextId = itemView.currentUserTextId as TextView
        }
    }

    override fun getItemId(item: Machine): Any {
        return item
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = getItem(position) as Machine

        holder.currentStatusHeadingId.setText("Current Status")
        holder.currentStatusItemTextId.setText(item.status.toString())

        holder.currentDateHeadingId.setText("Currnet Date")
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = current.format(formatter)
        holder.currentDateTextId.setText(formatted.toString())

        holder.currentUserHeadingId.setText("Current User")
        if (item.createdBy != null) {
            holder.currentUserTextId.setText(item.createdBy.toString())
        } else {
            holder.currentUserTextId.setText("NA")
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.machine_item_list_row, parent, false)
        return ViewHolder(view)

    }

}