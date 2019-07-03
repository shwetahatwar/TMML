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


class StartPartProcess : Fragment() {

    companion object {
        fun newInstance() = StartPartProcess()
    }

    private lateinit var viewModel: StartPartProcessViewModel
    private var progress: Progress? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.start_part_process_fragment, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(StartPartProcessViewModel::class.java)
        // TODO: Use the ViewModel
        (this.activity as AppCompatActivity).setTitle("Start Part Process")

        startPartMachineBarcodeScan.requestFocus()

        startPartMultiplicationFactor.visibility = View.GONE

        viewModel.startPartProcess.observe(this, Observer<JobProcessSequenceRelation> {
            MainActivity.hideProgress(this.progress)
            this.progress = null
            MainActivity.showToast(this.activity as AppCompatActivity, "Successfully Start Part Process")
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

            if(viewModel.machine == null || viewModel.machine.value == null || viewModel.machine.value?.maintenanceStatus == null || !viewModel.machine.value?.maintenanceStatus!!.equals("Available")) {
                MainActivity.showToast(this.activity as AppCompatActivity, "Machine is not Available Please Select Other Machine")
                startPartMachineBarcodeScan.text?.clear()
                startPartMachineBarcodeScan.requestFocus()
            } else {
                if (viewModel.machine.value?.isAutomacticCount != null && (viewModel.machine.value?.isAutomacticCount!!).toInt() > 0) {
                    startPartMultiplicationFactor.visibility = View.VISIBLE
                } else {
                    startPartMultiplicationFactor.visibility = View.GONE
                }
                MainActivity.showToast(this.activity as AppCompatActivity, "Successful Machine Barcode Scanned")
                startPartJobcardBarcodeScan.requestFocus()
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
                startPartJobcardBarcodeScan.text?.clear()
                startPartJobcardBarcodeScan.requestFocus()
            }else  {
                MainActivity.showToast(this.activity as AppCompatActivity, "Successful Job Card Scanned")
                startPartOperatorBarcodeScan.requestFocus()
            }
        })

        viewModel.jobcardNetworkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null
                MainActivity.showAlert(this.activity as AppCompatActivity, "Server is not reachable, please check if your network connection is working");
            }
        })

        viewModel.employee.observe(this, Observer<Employee> {
            MainActivity.hideProgress(this.progress)
            this.progress = null
            if (it == null) {
                MainActivity.showToast(this.activity as AppCompatActivity, "Operator not found with scanned barcode")
                startPartOperatorBarcodeScan.text?.clear()
                startPartOperatorBarcodeScan.requestFocus()
            } else {
                MainActivity.showToast(this.activity as AppCompatActivity, "Operator Barcode Successful")
            }
        })

        viewModel.employeeNetworkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null
                MainActivity.showAlert(this.activity as AppCompatActivity, "Server is not reachable, please check if your network connection is working");
            }
        })



        startPartMachineBarcodeScan.setOnEditorActionListener { _, i, keyEvent ->
            var handled = false

            if (i == EditorInfo.IME_ACTION_DONE || ((keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || keyEvent.keyCode == KeyEvent.KEYCODE_TAB) && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.loadMachineDetails(startPartMachineBarcodeScan.text.toString())


                handled = true
            }
            handled

        }


        startPartJobcardBarcodeScan.setOnEditorActionListener { _, i, keyEvent ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_DONE || ((keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || keyEvent.keyCode == KeyEvent.KEYCODE_TAB) && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.loadJobcardDetails(startPartJobcardBarcodeScan.text.toString())

                handled = true
            }
            handled
        }

        startPartOperatorBarcodeScan.setOnEditorActionListener { _, i, keyEvent ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_DONE || ((keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || keyEvent.keyCode == KeyEvent.KEYCODE_TAB) && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                val value = this.startPartOperatorBarcodeScan.text.toString()
                viewModel.getEmployeeDetail(value)

                handled = true
            }
            handled
        }

        btnStartPartProcess.setOnClickListener {
            if (viewModel.machine == null || viewModel.machine.value == null || viewModel.machine.value?.barcodeSerial == null) {

                MainActivity.showToast(this.activity as AppCompatActivity, "Please scan valid Machine Barcode")
                startPartMachineBarcodeScan.text?.clear()
                startPartMachineBarcodeScan.requestFocus()

            } else if (viewModel.jobcardDetails == null || viewModel.jobcardDetails.value == null || viewModel.jobcardDetails.value?.barcodeSerial == null) {
                MainActivity.showToast(this.activity as AppCompatActivity, "Please scan valid Jobcard Barcode")
                startPartJobcardBarcodeScan.text?.clear()
                startPartJobcardBarcodeScan.requestFocus()
            }else if (viewModel.employee == null || viewModel.employee.value == null || viewModel.employee.value?.employeeId == null) {
                MainActivity.showToast(this.activity as AppCompatActivity, "Please scan valid Oberator Identifier")
                startPartOperatorBarcodeScan.text?.clear()
                startPartOperatorBarcodeScan.requestFocus()
            } else {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                val operatorIdNumber: Number = viewModel.employee.value!!.id!!
                val mFactor: Number =startPartMultiplicationFactor.text.toString().toInt()
                viewModel.postStartPartProcess(viewModel.machine.value?.id!!.toString(), viewModel.jobcardDetails.value?.id.toString()!!, mFactor, operatorIdNumber)

            }

        }

    }

}


