package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.briot.tmmlmss.implementor.MainActivity
import com.pascalwelsch.arrayadapter.ArrayAdapter
import io.github.pierry.progress.Progress
import kotlinx.android.synthetic.main.machine_maintenance_fragment.*
import kotlinx.android.synthetic.main.machine_item_list_row.view.*
import android.widget.TextView;
import kotlinx.android.synthetic.main.jobcard_details_scan_fragment.*
import kotlinx.android.synthetic.main.jobcard_item_list_row.view.*
import com.briot.tmmlmss.implementor.repository.local.PrefConstants
import com.briot.tmmlmss.implementor.repository.local.PrefRepository
import android.view.inputmethod.InputMethodManager
import com.briot.tmmlmss.implementor.repository.remote.Machine
import com.briot.tmmlmss.implementor.R
import kotlinx.android.synthetic.main.start_part_process_fragment.*
import android.widget.EditText
import com.briot.tmmlmss.implementor.repository.remote.*
import kotlinx.android.synthetic.main.stop_part_process_fragment.*


class StopPartProcess : Fragment() {

    companion object {
        fun newInstance() = StopPartProcess()
    }

    private lateinit var viewModel: StopPartProcessViewModel
    private var progress: Progress? = null
    private var stopPartStatus: String? = null
    private var inProgress: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.stop_part_process_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(StopPartProcessViewModel::class.java)
        // TODO: Use the ViewModel
        (this.activity as AppCompatActivity).setTitle("Stop Part Process")

        scheduledQuantityValue.visibility = View.GONE


        stopPartMachineBarcodeScan.requestFocus()

        viewModel.stopPartProcess.observe(this, Observer<JobProcessSequenceRelation> {
            MainActivity.hideProgress(this.progress)
            this.progress = null
            MainActivity.showToast(this.activity as AppCompatActivity, "Successfully Stop Part Process")
            stopPartEnterQuantity.text?.clear()
            stopPartJobcardBarcodeScan.text?.clear()
            stopPartMachineBarcodeScan.text?.clear()
            stopPartProcessRemark.text?.clear()
            scheduledQuantityValue.visibility = View.GONE
            stopPartMachineBarcodeScan.requestFocus()
        })

        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null
                MainActivity.showToast(this.activity as AppCompatActivity, "please check your network connection")
                MainActivity.showAlert(this.activity as AppCompatActivity, "Server is not reachable, please check if your network connection is working");
            }
        })

        viewModel.machine.observe(this, Observer<Machine> {
            MainActivity.hideProgress(this.progress)
            this.progress = null
            if(viewModel.machine == null || viewModel.machine.value == null || viewModel.machine.value?.maintenanceStatus == null || !viewModel.machine.value?.maintenanceStatus!!.equals("Occupied")) {
                MainActivity.showToast(this.activity as AppCompatActivity, "Machine is not Occupied for process. Please Select Other Machine")
                stopPartMachineBarcodeScan.text?.clear()
                stopPartMachineBarcodeScan.requestFocus()
            } else {   //if (it != null)
                MainActivity.showToast(this.activity as AppCompatActivity, "Successful Machine Barcode Scanned")
                stopPartEnterQuantity.text?.clear()
                stopPartProcessSpinner.setSelection(0)
                stopPartJobcardBarcodeScan.requestFocus()
            }
        })

        viewModel.machineNetworkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null
                MainActivity.showAlert(this.activity as AppCompatActivity, "Server is not reachable, please check if your network connection is working");

            }
        })



        viewModel.jobcardDetails.observe(this, Observer<JobcardDetail> {
            MainActivity.hideProgress(this.progress)
            this.progress = null
            if(viewModel.jobcardDetails == null || viewModel.jobcardDetails.value == null) {
                MainActivity.showToast(this.activity as AppCompatActivity, "Jobcard is not Available Please Select Other Jobcard")
                stopPartJobcardBarcodeScan.text?.clear()
                stopPartJobcardBarcodeScan.requestFocus()
            }else{
                MainActivity.showToast(this.activity as AppCompatActivity, "Successful Job Card Scanned")
                val scheduledQuantity: Number = this.viewModel.jobcardDetails.value!!.requestedQuantity.toString().toInt()
                val actualQuantity: Number = this.viewModel.jobcardDetails.value!!.actualQuantity.toString().toInt()

                var result: Int = scheduledQuantity.toInt() - actualQuantity.toInt()
                if (result < 0) {
                    result = 0
                }

                scheduledQuantityValue.text = "Pending Scheduled Quantity: " + result
                scheduledQuantityValue.visibility = View.VISIBLE
//                var pendingQuantity: Number = (viewModel.jobcardDetails.value!!.requestedQuantity.toString().toInt() - viewModel.jobcardDetails.value!!.actualQuantity.toString().toInt())
//                stopPartEnterQuantity.text = ""
                stopPartEnterQuantity.requestFocus()
            }
        })

        viewModel.jobcardNetworkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null
                MainActivity.showAlert(this.activity as AppCompatActivity, "Server is not reachable, please check if your network connection is working");
            }
        })

        viewModel.stopPartProcess.observe(this, Observer<JobProcessSequenceRelation> {
            inProgress = false;
        })


        stopPartMachineBarcodeScan.setOnEditorActionListener { _, i, keyEvent ->
            var handled = false
            if (keyEvent == null) {
                Log.d("StopPartProcess: ", "event is null")
            } else if (i == EditorInfo.IME_ACTION_DONE || ((keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || keyEvent.keyCode == KeyEvent.KEYCODE_TAB) && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.loadMachineDetails(stopPartMachineBarcodeScan.text.toString())


                handled = true
            }
            handled

        }


        stopPartJobcardBarcodeScan.setOnEditorActionListener { _, i, keyEvent ->
            var handled = false
            if (keyEvent == null) {
                Log.d("StopPartProcessScan: ", "event is null")
            } else if (i == EditorInfo.IME_ACTION_DONE || ((keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || keyEvent.keyCode == KeyEvent.KEYCODE_TAB) && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.loadJobcardDetails(stopPartJobcardBarcodeScan.text.toString())

                handled = true
            }
            handled
        }


        stopPartProcessSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                stopPartStatus = (parent.getItemAtPosition(pos)).toString()

            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        })


        btnStopPartProcess.setOnClickListener {

            if (inProgress) {
            } else if (viewModel.machine == null || viewModel.machine.value == null || viewModel.machine.value?.barcodeSerial == null) {

                MainActivity.showToast(this.activity as AppCompatActivity, "Please scan valid Machine Barcode")
                stopPartMachineBarcodeScan.text?.clear()
                stopPartMachineBarcodeScan.requestFocus()

            } else if (viewModel.jobcardDetails == null || viewModel.jobcardDetails.value == null || viewModel.jobcardDetails.value?.barcodeSerial == null) {
                MainActivity.showToast(this.activity as AppCompatActivity, "Please scan valid Jobcard Barcode")
                stopPartJobcardBarcodeScan.text?.clear()
                stopPartJobcardBarcodeScan.requestFocus()
            } else {
                var result = stopPartEnterQuantity.text.toString();
                var quantity: Number = 0
                if (result != null && result.isNotEmpty()) {
                    quantity = result.toInt()
                }
                val scheduledQuantity: Number = this.viewModel.jobcardDetails.value!!.requestedQuantity.toString().toInt()
                val actualQuantity: Number = this.viewModel.jobcardDetails.value!!.actualQuantity.toString().toInt()
                if ((scheduledQuantity.toInt() - actualQuantity.toInt()) < quantity.toInt()) {
                    MainActivity.showToast(this.activity as AppCompatActivity, "Quantity cannot be entered beyond scheduled Quantity")
                } else {
                    this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                    val note: String = stopPartProcessRemark.text.toString()
//                getJobcardId = viewModel.jobcardDetails.value?.id!!
//                getMachineId = viewModel.machine.value?.id!!
                    this.inProgress = true
                    viewModel.updateStopPartProcess(viewModel.machine.value?.id!!.toString(), viewModel.jobcardDetails.value?.id.toString(), quantity, stopPartStatus.toString(),note)

                }
            }

        }

    }

}
