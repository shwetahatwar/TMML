package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.briot.tmmlmss.implementor.R
import com.briot.tmmlmss.implementor.repository.remote.Sap313Record
import androidx.lifecycle.Observer
import io.github.pierry.progress.Progress
import com.briot.tmmlmss.implementor.MainActivity
import com.briot.tmmlmss.implementor.repository.remote.Sap315Record
import kotlinx.android.synthetic.main.receive_at_store_fragment.*
import kotlinx.android.synthetic.main.receive_at_store_fragment.jobcardScanText
import android.widget.AdapterView;
import android.widget.Toast



class ReceiveAtStoreFragment : Fragment() {

    companion object {
        fun newInstance() = ReceiveAtStoreFragment()
    }

    private lateinit var viewModel: ReceiveAtStoreViewModel
    private var progress: Progress? = null
    private var oldSap313Records: Array<Sap313Record>? = null
    private var oldSap315Record: Sap315Record? = null
    private var selectedSap313Record: Sap313Record? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.receive_at_store_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ReceiveAtStoreViewModel::class.java)

        (this.activity as AppCompatActivity).setTitle("Receive at Store")


        viewModel.sap313Records.observe(this, Observer<Array<Sap313Record>> {
            MainActivity.hideProgress(this.progress)
            this.progress = null

            if (it != null && it != oldSap313Records) {
                record313dataLayout.visibility = View.INVISIBLE
                if (it.size == 1) {
                    display313RecordsSpinner(it)
                } else if (it.size > 0) {
                    display313RecordsSpinner(it)
                } else {
                    var message = "No record found for this Jobcard!"
                    if (viewModel.errorMessage.value != null) {
                        message = viewModel.errorMessage.value.toString()
                    }

                    MainActivity.showToast(this.activity as AppCompatActivity, message);
                }
            } else if (it == null) {
                var message = "Unknown error has occurred. please retry scan!"
                if (viewModel.errorMessage.value != null) {
                    message = viewModel.errorMessage.value.toString()
                }

                MainActivity.showToast(this.activity as AppCompatActivity, message);
            }

            jobcardScanText.text?.clear()
            jobcardScanText.requestFocus()
            oldSap313Records = it
        })



        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null
                var message: String = "Server is not reachable, please check if your network connection is working"
                if (viewModel.errorMessage != null && viewModel.errorMessage.value != null) {
                    message = viewModel.errorMessage!!.value.toString()
                }

                MainActivity.showToast(this.activity as AppCompatActivity, message);
                jobcardScanText.text?.clear()
//                viewModel.defaultValue()
            }
            if (!jobcardScanText.isFocused) {
                jobcardScanText.requestFocus()
            }
        })

        jobcardScanText.setOnEditorActionListener { _, i, keyEvent ->
            var handled = false
            if (keyEvent == null) {
                Log.d("jobcard scan: ", "event is null")
            } else if (jobcardScanText != null && jobcardScanText.text != null && i == EditorInfo.IME_ACTION_DONE || ((keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || keyEvent.keyCode == KeyEvent.KEYCODE_TAB) && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.get313Records(jobcardScanText.text.toString())
                handled = true
            }
            handled
        }

        btnScanSubmit.setOnClickListener {
            var handled = false
            if (jobcardScanText != null && jobcardScanText.text != null) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.get313Records(jobcardScanText.text.toString())
                handled = true
            }
            handled
        }

        btnSubmitSAPRecord.setOnClickListener {
            var handled = false
            if (selectedSap313Record != null) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.post315Record(selectedSap313Record!!)

                handled = true
            }
            handled
        }

        selectQuantitySpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (viewModel.sap313Records.value != null) {
                    var record = viewModel.sap313Records.value!!.elementAt(position)
                    selectedSap313Record = record
                    display313Record(record)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        })


        viewModel.sap315Record.observe(this, Observer<Sap315Record> {
            MainActivity.hideProgress(this.progress)
            this.progress = null

            if (it != null) {
                if (it.quantity313 == it.quantity315 && it.documentNumber315 != null) {
                    MainActivity.showToast(this.activity as AppCompatActivity, "SAP 315 transaction done successfully!");
                    record313dataLayout.visibility = View.INVISIBLE
                } else {
                    var message = "Unknown error has occurred. please retry scan!"
                    if (viewModel.errorMessage.value != null) {
                        message = viewModel.errorMessage.value.toString()
                    }

                    MainActivity.showToast(this.activity as AppCompatActivity, message);
                }
            } else if (it == null) {
                var message = "Unknown error has occurred. please retry scan!"
                if (viewModel.errorMessage.value != null) {
                    message = viewModel.errorMessage.value.toString()
                }

                MainActivity.showToast(this.activity as AppCompatActivity, message);
            }

            jobcardScanText.text?.clear()
            jobcardScanText.requestFocus()
//            oldSap315Record = it
        })
    }

    fun display313RecordsSpinner(records: Array<Sap313Record>) {
        if (records != null && records.size > 0) {
            var result: ArrayList<String> = ArrayList()
            for (item in records) {
                result.add("Quanity: " + item.quantity.toString())

            }
            val arrayAdapter = ArrayAdapter(this.context, android.R.layout.simple_spinner_item, result.toArray())
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            selectQuantitySpinner.setAdapter(arrayAdapter)

            if (records.size > 1) {
                selectQuantitySpinner.performClick()
            }

        }

        record313dataLayout.visibility = View.INVISIBLE
    }

    fun display313Record(record: Sap313Record) {
        if (record != null) {
            jobcard313Id.text = record.jobCard
            material313Id.text = record.material
            quantity313Id.text = record.quantity.toString()
            docNumber313Id.text =record.documentNumber
            uniqueNumber313id.text = record.uniqueNumber
            selectedSap313Record = record
        }

        record313dataLayout.visibility = View.VISIBLE
    }
}
