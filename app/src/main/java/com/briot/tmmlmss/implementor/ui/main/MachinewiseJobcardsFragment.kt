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
import androidx.appcompat.app.AppCompatActivity
import com.briot.tmmlmss.implementor.MainActivity
import com.briot.tmmlmss.implementor.R
import io.github.pierry.progress.Progress
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.machinewise_jobcards_fragment.*
import kotlinx.android.synthetic.main.receive_at_store_fragment.btnScanSubmit
import android.widget.ArrayAdapter


class MachinewiseJobcardsFragment : Fragment() {

    companion object {
        fun newInstance() = MachinewiseJobcardsFragment()
    }

    private lateinit var viewModel: MachinewiseJobcardsViewModel
    private var progress: Progress? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.machinewise_jobcards_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MachinewiseJobcardsViewModel::class.java)

        (this.activity as AppCompatActivity).setTitle("Jobcards at Machine")

        viewModel.jobcards.observe(this, Observer<Array<String>> {
            MainActivity.hideProgress(this.progress)
            this.progress = null

            if (it != null) {
                jobcardList.visibility = View.INVISIBLE
                var adapter = ArrayAdapter<String>(this.context,
                        android.R.layout.simple_list_item_1,
                        it)
                jobcardList.adapter =  adapter
                jobcardList.visibility = View.VISIBLE
//                adapter.notifyDataSetChanged();

            } else if (it == null) {
                var message = "Unknown error has occurred. please retry scan!"
                if (viewModel.errorMessage.value != null) {
                    message = viewModel.errorMessage.value.toString()
                }

                MainActivity.showToast(this.activity as AppCompatActivity, message);
            }

            machineJobcardScanText.text?.clear()
            machineJobcardScanText.requestFocus()
        })



        viewModel.networkError.observe(this, Observer<Boolean> {
            jobcardList.visibility = View.INVISIBLE
//            viewModel.loadTestValues()
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null
                var message: String = "Server is not reachable, please check if your network connection is working"
                if (viewModel.errorMessage != null && viewModel.errorMessage.value != null) {
                    message = viewModel.errorMessage!!.value.toString()
                }

                MainActivity.showToast(this.activity as AppCompatActivity, message);
                machineJobcardScanText.text?.clear()
            }
            if (!machineJobcardScanText.isFocused) {
                machineJobcardScanText.requestFocus()
            }
        })

        machineJobcardScanText.setOnEditorActionListener { _, i, keyEvent ->
            var handled = false
            if (keyEvent == null) {
                Log.d("machine barcode scan: ", "event is null")
            } else if (machineJobcardScanText != null && machineJobcardScanText.text != null && i == EditorInfo.IME_ACTION_DONE || ((keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || keyEvent.keyCode == KeyEvent.KEYCODE_TAB) && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.getJobcardsOnMachine(machineJobcardScanText.text.toString())
                handled = true
            }
            handled
        }

        btnScanSubmit.setOnClickListener {
            var handled = false
            if (machineJobcardScanText != null && machineJobcardScanText.text != null) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.getJobcardsOnMachine(machineJobcardScanText.text.toString())
                handled = true
            }
            handled
        }

        machineJobcardScanText.text?.clear()
        machineJobcardScanText.requestFocus()
    }

}
