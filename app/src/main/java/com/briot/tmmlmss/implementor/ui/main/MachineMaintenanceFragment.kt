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
import android.util.Log
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
import com.briot.tmmlmss.implementor.repository.local.PrefConstants
import com.briot.tmmlmss.implementor.repository.local.PrefRepository
import com.briot.tmmlmss.implementor.repository.remote.MaintenanceTransaction
import kotlinx.android.synthetic.main.machine_item_list_row.view.currentDateHeadingId
import kotlinx.android.synthetic.main.machine_item_list_row.view.currentDateTextId
import kotlinx.android.synthetic.main.machine_item_list_row.view.currentStatusHeadingId
import kotlinx.android.synthetic.main.machine_item_list_row.view.currentStatusItemTextId
import kotlinx.android.synthetic.main.machine_item_list_row.view.machineNameTextId
import kotlinx.android.synthetic.main.machine_maintenance_item_list_row.view.*


class MachineMaintenanceFragment : Fragment() {
    //private var valueOfSpinner: String? = null
    private lateinit var viewModel: MachineMaintenanceViewModel
    private var progress: Progress? = null
    private var machineStatus: String? = null
    var selectedStatus: String? = null

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

        MachineScanText.requestFocus()

        machineItemsList.adapter = MachineDetailsItemsAdapter(this.context!!)
        machineItemsList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)

        val items = resources.getStringArray(R.array.machine_state_array) //(R.array.machine_state_array);

//        val adapter = machineStateSpinner
//        val newAdapter = android.widget.ArrayAdapter<String>(context, R.id.machineStateSpinner, items)
//        machineStateSpinner.adapter = newAdapter

        viewModel.machine.observe(this, Observer<Machine> {
            MainActivity.hideProgress(this.progress)
            this.progress = null
            MainActivity.showToast(this.activity as AppCompatActivity, "Machine Details")

            (machineItemsList.adapter as MachineDetailsItemsAdapter).clear()
            if (it != null) {
                (machineItemsList.adapter as MachineDetailsItemsAdapter).add(it)
                (machineItemsList.adapter as MachineDetailsItemsAdapter).notifyDataSetChanged()
                viewStatus(true)

                if (it!!.maintenanceStatus != null) {
                    selectedStatus = it!!.maintenanceStatus!!
                    val indexOfSelectedItem = items.indexOf(selectedStatus)
                    if (indexOfSelectedItem >= 0) {
                        machineStateSpinner.setSelection(indexOfSelectedItem)
                    }
                }

                if (selectedStatus != null && selectedStatus.equals("Occupied")) {
                    btnUpdateStatus.visibility = View.GONE

                    MainActivity.showToast(this.activity as AppCompatActivity, "Machine is occupied, first STOP the job process on machine.")

                }
            } else {
                MainActivity.showToast(this.activity as AppCompatActivity, "Machine not found for scanned Barcode")
                MachineScanText.text?.clear()
                MachineScanText.requestFocus()
                viewStatus(false)
            }
        })

        viewModel.maintenanceTransaction.observe(this, Observer<MaintenanceTransaction> {
            MainActivity.hideProgress(this.progress)
            this.progress = null
            MainActivity.showToast(this.activity as AppCompatActivity, "Updated Machine Status")

            MachineScanText.text?.clear()
            MachineScanText.requestFocus()
            viewStatus(false)
        });

        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null
                MainActivity.showToast(this.activity as AppCompatActivity, "Please Scan Machine Machine Barcode")
                MainActivity.showAlert(this.activity as AppCompatActivity, "Server is not reachable, please check if your network connection is working");
            }
        })



        btnScanSubmit.setOnClickListener {
            if (MachineScanText.text != null && MachineScanText.text!!.isNotEmpty()) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.loadMachineDetails(MachineScanText.text.toString())
            }
        }

        MachineScanText.setOnEditorActionListener { _, i, keyEvent ->
            var handled = false
            if (keyEvent == null) {
                Log.d("MachinMain: ", "event is null")
            } else if (i == EditorInfo.IME_ACTION_DONE || ((keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || keyEvent.keyCode == KeyEvent.KEYCODE_TAB) && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.loadMachineDetails(MachineScanText.text.toString())
                handled = true
            }
            handled
        }

        machineStateSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                machineStatus = (parent.getItemAtPosition(pos)).toString()

                var machineMaintenanceStatus = viewModel.machine.value?.maintenanceStatus;
                if (machineStatus.equals("Available") && !machineMaintenanceStatus.equals("Available")) {
                    if (viewModel.machine != null && viewModel.machine.value != null && !(viewModel.machine.value!!.maintenanceStatus.equals("Available"))) {
                        machinePartReplace.visibility = View.VISIBLE
                        maintenanceOperatorId.visibility = View.VISIBLE
                        machinePartCost.visibility = View.VISIBLE
                    } else {
                        machinePartReplace.visibility = View.GONE
                        maintenanceOperatorId.visibility = View.GONE
                        machinePartCost.visibility = View.GONE
                    }
                } else {
                    machinePartReplace.visibility = View.GONE
                    maintenanceOperatorId.visibility = View.GONE
                    machinePartCost.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        })

//        maintenanceOperatorId.setOnEditorActionListener { _, i, keyEvent ->
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

            val roleName = PrefRepository.singleInstance.getValueOrDefault(PrefConstants().ROLE_NAME, "")

            val isProductionUser = roleName.toLowerCase().equals("Production".toLowerCase())
            val stateOtherThanBreakDown = !machineStatus.toString().toLowerCase().equals("Break-down".toLowerCase())

            if (stateOtherThanBreakDown && isProductionUser) {
                MainActivity.showAlert(this.activity as AppCompatActivity, "Production users are allowed to report only 'Break-down'")
            } else if (viewModel.machine != null && viewModel.machine.value != null && viewModel.machine.value?.id != null) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                var costOfPartReplaced: Number = 0
                if (machinePartCost.text?.isNotEmpty()!!) {
                    costOfPartReplaced = machinePartCost.text.toString().toInt()
                }
                viewModel.updateMachineDetails(viewModel.machine.value?.id!!, machinePartReplace.text.toString(), machineRemark.text.toString(), machineStatus.toString(), maintenanceOperatorId.text.toString(), costOfPartReplaced)//machineStateSpinner.getSelectedItem().toString()
            }
        }
    }

    fun viewStatus(visible: Boolean) {
        if (visible) {
            machineStateSpinner.setVisibility(View.VISIBLE)
            machineRemark.setVisibility(View.VISIBLE)
            lable1.setVisibility(View.VISIBLE)
            btnUpdateStatus.setVisibility(View.VISIBLE)
            machineItemsList.setVisibility(View.VISIBLE)
            var machineStatus = viewModel.machine.value?.maintenanceStatus?.toLowerCase();
            if (machineStatus.equals("under maintenance") || machineStatus.equals("break-down")) {
                machinePartReplace.setVisibility(View.VISIBLE)
                maintenanceOperatorId.setVisibility(View.VISIBLE)
                machinePartCost.setVisibility(View.VISIBLE)
            } else {
                machinePartReplace.setVisibility(View.GONE)
                maintenanceOperatorId.setVisibility(View.GONE)
                machinePartCost.setVisibility(View.GONE)
            }
        } else {
            machineStateSpinner.setVisibility(View.INVISIBLE)
            maintenanceOperatorId.setVisibility(View.INVISIBLE)
            maintenanceOperatorId.text?.clear()
            machinePartCost.visibility = View.INVISIBLE
            machinePartCost.text?.clear()
            machinePartReplace.setVisibility(View.INVISIBLE)
            machinePartReplace.text?.clear()
            machineRemark.setVisibility(View.INVISIBLE)
            machineRemark.text?.clear()
            lable1.setVisibility(View.INVISIBLE)
            btnUpdateStatus.setVisibility(View.INVISIBLE)
            machineItemsList.setVisibility(View.INVISIBLE)
        }

    }


}

class MachineDetailsItemsAdapter(val context: Context) : ArrayAdapter<Machine, MachineDetailsItemsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val currentStatusHeadingId: TextView
        val currentStatusItemTextId: TextView
        val currentDateHeadingId: TextView
        val currentDateTextId: TextView
        val machineNameId: TextView
        val machineNameTextId: TextView

        init {
            currentStatusHeadingId = itemView.currentStatusHeadingId as TextView
            currentStatusItemTextId = itemView.currentStatusItemTextId as TextView
            currentDateHeadingId = itemView.currentDateHeadingId as TextView
            currentDateTextId = itemView.currentDateTextId as TextView
            machineNameId = itemView.machineNameId as TextView
            machineNameTextId = itemView.machineNameTextId as TextView
        }
    }

    override fun getItemId(item: Machine): Any {
        return item
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) as Machine

        holder.currentStatusHeadingId.setText("Current Status")
        holder.currentStatusItemTextId.setText(item.maintenanceStatus.toString())

        holder.currentDateHeadingId.setText("Cell")
//        val current = LocalDateTime.now()
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        val formatted = current.format(formatter)
        if (item.cellId?.name != null) {
            holder.currentDateTextId.setText(item.cellId?.name)
        } else {
            holder.currentDateTextId.setText("NA")
        }

        holder.machineNameId.setText("Name")
        if (item.machineName != null) {
            holder.machineNameTextId.setText(item.machineName.toString())
        } else {
            holder.machineNameTextId.setText("NA")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.machine_maintenance_item_list_row, parent, false)
        return ViewHolder(view)
    }

}
