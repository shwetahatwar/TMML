package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.Navigation
import com.briot.tmmlmss.implementor.MainActivity
import com.briot.tmmlmss.implementor.R
import com.briot.tmmlmss.implementor.repository.remote.RackLocation
import io.github.pierry.progress.Progress
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.put_away_location_scan_fragment.*
import java.util.concurrent.TimeUnit


class PutAwayLocationScanFragment : androidx.fragment.app.Fragment() {

    companion object {
        fun newInstance() = PutAwayLocationScanFragment()
    }

    private lateinit var viewModel: PutAwayLocationScanViewModel

    private var progress: Progress? = null
    private var oldRackLocation: RackLocation? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.put_away_location_scan_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PutAwayLocationScanViewModel::class.java)
        // TODO: Use the ViewModel

        (this.activity as AppCompatActivity).setTitle("Audit")

        viewModel.rackLocation.observe(this, Observer<RackLocation> {
            MainActivity.hideProgress(this.progress)
            this.progress = null

            if (it != null && it != oldRackLocation) {
                if (it != viewModel.invalidLocation) {
                    Navigation.findNavController(submitLocation).navigate(R.id.action_putAwayLocationScanFragment_to_putAwayProductScanFragment)
                } else {

                }
            }

            oldRackLocation = it
        })

//        Observable.create(ObservableOnSubscribe<String> { subscriber ->
//            locationScanText.addTextChangedListener(object : TextWatcher {
//                override fun afterTextChanged(s: Editable?) = Unit
//
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
//
//                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
//                        = subscriber.onNext(s.toString())
//            })
//        }).debounce(1000, TimeUnit.MILLISECONDS)
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe({ text: String ->
//            if(text.isNotEmpty()) {
//                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
//                viewModel.loadLocationDetails(text)
//            }
//        })

        locationScanText.setOnEditorActionListener { textView, i, keyEvent ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_DONE || (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.loadLocationDetails(locationScanText.text.toString())

                handled = true
            }
            handled
        }


        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null

                MainActivity.showAlert(this.activity as AppCompatActivity, "Server is not reachable, please check if your internet connection is working");
            }
        })

        submitLocation.setOnClickListener {
            this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
            viewModel.loadLocationDetails(locationScanText.text.toString())
        }
    }

}
