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
import android.widget.*
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
import android.widget.TextView;
import com.briot.tmmlmss.implementor.repository.remote.MaintenanceTransaction


class MachineMaintenanceFragment : Fragment()  {
    //private var valueOfSpinner: String? = null
    private lateinit var viewModel: MachineMaintenanceViewModel
    private var progress: Progress? = null
    private var oldMachine: Machine? = null
    private var machineStatus:String? = null
    var selectedStatus:String? = null

    companion object {
        fun newInstance() = MachineMaintenanceFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.machine_maintenance_fragment, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MachineMaintenanceViewModel::class.java)
        // TODO: Use the ViewModel

        (this.activity as AppCompatActivity).setTitle("Machine Maintenance")

        machineItemsList.adapter = MachineDetailsItemsAdapter(this.context!!)
        machineItemsList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)

        viewModel.machine.observe(this, Observer<Machine> {
            MainActivity.hideProgress(this.progress)
            this.progress = null
            MainActivity.showToast(this.activity as AppCompatActivity, "Machine Details ")

            (machineItemsList.adapter as MachineDetailsItemsAdapter).clear()
            if (it != null && it!= oldMachine) {
                (machineItemsList.adapter as MachineDetailsItemsAdapter).add(it)
                (machineItemsList.adapter as MachineDetailsItemsAdapter).notifyDataSetChanged()
            }

            oldMachine = it
            val items = resources.getStringArray(R.array.machine_state_array) //(R.array.machine_state_array);
            if (oldMachine != null && oldMachine!!.status != null) {
                selectedStatus = oldMachine!!.status!!
                val indexOfSelectedItem = items.indexOf(selectedStatus)
                machineStateSpinner.setSelection(indexOfSelectedItem)

            }

        })

        viewModel.maintenanceTransaction.observe(this, Observer<MaintenanceTransaction> {
            MainActivity.hideProgress(this.progress)
            this.progress = null
            MainActivity.showToast(this.activity as AppCompatActivity, "Updated Machine Status")
        });

        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null
                MainActivity.showToast(this.activity as AppCompatActivity, "Please Scan Machine Machine Barcode")
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


        machineStateSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                machineStatus = (parent.getItemAtPosition(pos)).toString()

                if(selectedStatus.equals("Under Maintenance"))
                {
                    if(machineStatus.equals("Available"))
                    {
                        machinePartReplace.setEnabled(true)
                    }
                    else
                    {
                        machinePartReplace.setEnabled(false)
                    }
                }
                else if(selectedStatus.equals("Available"))
                {
                    if(machineStatus.equals("Available"))
                    {
                        machinePartReplace.setEnabled(false)
                    }
                }
                else
                {
                    machinePartReplace.setEnabled(false)
                }

        }
                override fun onNothingSelected(parent: AdapterView<*>) {}
        })

//        machinePartReplace.setOnEditorActionListener { _, i, keyEvent ->
//            var handled = false
//            if (i == EditorInfo.IME_ACTION_DONE || (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN) ) {
//                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
//                viewModel.updateMachineDetails(viewModel.getMachineDetail!!.id,machinePartReplace.text.toString(),machineRemark.text.toString(),valueOfSpinner.toString())
//
//                handled = true
//            }
//            handled
//        }

        btnUpdateStatus.setOnClickListener {
            if (viewModel.machine != null && viewModel.machine.value != null && viewModel.machine.value?.id != null) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.updateMachineDetails(viewModel.machine.value?.id!!,machinePartReplace.text.toString(), machineRemark.text.toString(), machineStatus.toString())//machineStateSpinner.getSelectedItem().toString()
            }
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
