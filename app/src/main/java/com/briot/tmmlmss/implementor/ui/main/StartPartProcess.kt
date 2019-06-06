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
import com.briot.tmmlmss.implementor.repository.remote.Machine
import com.pascalwelsch.arrayadapter.ArrayAdapter
import io.github.pierry.progress.Progress
import kotlinx.android.synthetic.main.machine_maintenance_fragment.*
import kotlinx.android.synthetic.main.machine_item_list_row.view.*
import android.widget.TextView;
import com.briot.tmmlmss.implementor.repository.remote.MaintenanceTransaction
import com.briot.tmmlmss.implementor.repository.remote.JobcardDetail
import kotlinx.android.synthetic.main.jobcard_details_scan_fragment.*
import kotlinx.android.synthetic.main.jobcard_item_list_row.view.*
import com.briot.tmmlmss.implementor.repository.local.PrefConstants
import com.briot.tmmlmss.implementor.repository.local.PrefRepository
import android.view.inputmethod.InputMethodManager


import com.briot.tmmlmss.implementor.R
import com.briot.tmmlmss.implementor.repository.remote.JobProcessSequenceRelation
import kotlinx.android.synthetic.main.start_part_process_fragment.*

class StartPartProcess : Fragment() {

    companion object {
        fun newInstance() = StartPartProcess()
    }

    private lateinit var viewModel: StartPartProcessViewModel
    private var progress: Progress? = null
    private var oldMachine: Machine? = null
    private var machineStatus: String? = null
    private var oldJobcardDetails: JobcardDetail? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.start_part_process_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(StartPartProcessViewModel::class.java)
        // TODO: Use the ViewModel
        (this.activity as AppCompatActivity).setTitle("Start Part Process")


        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null
                MainActivity.showAlert(this.activity as AppCompatActivity, "Server is not reachable, please check if your internet connection is working");
            }
        })

        startPartMachineBarcodeScan.setOnEditorActionListener { textView, i, keyEvent ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_DONE || (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.loadMachineDetails(startPartMachineBarcodeScan.text.toString().toInt())

                handled = true
            }
            handled
        }

        startPartJobcardBarcodeScan.setOnEditorActionListener { textView, i, keyEvent ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_DONE || (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.loadJobcardDetails(startPartJobcardBarcodeScan.text.toString().toInt())

                handled = true
            }
            handled
        }

        startPartMultiplicationFactor.setOnEditorActionListener { textView, i, keyEvent ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_DONE || (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.loadJobcardDetails(startPartMultiplicationFactor.text.toString().toInt())

                handled = true
            }
            handled
        }

        startPartOperatorBarcodeScan.setOnEditorActionListener { textView, i, keyEvent ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_DONE || (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.loadJobcardDetails(startPartOperatorBarcodeScan.text.toString().toInt())

                handled = true
            }
            handled
        }

        btnStartPartProcess.setOnClickListener {
            if (viewModel.startPartProcess != null && viewModel.startPartProcess.value != null && viewModel.startPartProcess.value?.id != null) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.postStartPartProcess(startPartMachineBarcodeScan.text.toString().toInt(), startPartJobcardBarcodeScan.text.toString().toInt(),startPartMultiplicationFactor.text.toString().toInt(),startPartOperatorBarcodeScan.text.toString().toInt())
            }
        }

    }

}


