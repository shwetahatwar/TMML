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
import com.briot.tmmlmss.implementor.repository.remote.JobLocationRelation
import io.github.pierry.progress.Progress
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.drop_at_location_fragment.*

class DropAtLocation : Fragment() {

    companion object {
        fun newInstance() = DropAtLocation()
    }

    private lateinit var viewModel: DropAtLocationViewModel
    private var progress: Progress? = null
    private var oldJobLocationRelations: Array<JobLocationRelation>? = null
    private var selectedJobLocationRelationId: Number = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.drop_at_location_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DropAtLocationViewModel::class.java)

        (this.activity as AppCompatActivity).setTitle("Drop at Location")

        if (this.arguments != null) {
            selectedJobLocationRelationId = this.arguments!!.getInt("jobcardLocationRelationId")
        }

        viewModel.jobLocationRelations.observe(this, Observer<Array<JobLocationRelation>> {
            MainActivity.hideProgress(this.progress)
            this.progress = null

            if (it != null && it != oldJobLocationRelations) {
                MainActivity.showToast(this.activity as AppCompatActivity, "Dropped at location successfully!");
                if (this.activity != null) {
                    Navigation.findNavController(this.activity!!, R.id.dropLocationFragmentId).navigateUp()
                }
            } else if (it == null) {
                var message = "Unknown error has occurred. please retry scanning!"
                if (viewModel.errorMessage.value != null) {
                    message = viewModel.errorMessage.value.toString()
                }

                MainActivity.showToast(this.activity as AppCompatActivity, message);
            }

//            droplocationBarcodeTextView.text?.clear()
//            droplocationBarcodeTextView.requestFocus()
//            oldJobLocationRelations = it
        })

        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null

                MainActivity.showToast(this.activity as AppCompatActivity, "Server is not reachable, please check if your network connection is working");
                droplocationBarcodeTextView.text?.clear()
            }
            if (!droplocationBarcodeTextView.isFocused) {
                droplocationBarcodeTextView.requestFocus()
            }
        })

        droplocationBarcodeTextView.setOnEditorActionListener { _, i, keyEvent ->
            var handled = false
            if (keyEvent == null) {
                Log.d("DropAtLocation: ", "event is null")
            } else if (i == EditorInfo.IME_ACTION_DONE || ((keyEvent.keyCode == KeyEvent.KEYCODE_ENTER || keyEvent.keyCode == KeyEvent.KEYCODE_TAB) && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                if (droplocationBarcodeTextView.text != null && droplocationBarcodeTextView.text!!.isNotEmpty()) {
                    this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                    viewModel.dropLocationForPendingItem(selectedJobLocationRelationId, droplocationBarcodeTextView.text!!.toString())
                } else {
                    droplocationBarcodeTextView.text?.clear()
                    droplocationBarcodeTextView.requestFocus()
                }


                handled = true
            }
            handled
        }

        btnScanSubmit.setOnClickListener {
            var handled = false
            if (droplocationBarcodeTextView?.text != null && droplocationBarcodeTextView.text!!.isNotEmpty()) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                var jobLocationRelationId = selectedJobLocationRelationId
                viewModel.dropLocationForPendingItem(jobLocationRelationId, droplocationBarcodeTextView.text!!.toString())

                handled = true
            }
            handled
        }

//        viewModel.dummySetupForFieldFocus()
    }

}
