package com.briot.tmmlmss.implementor.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import android.util.Log
import com.briot.tmmlmss.implementor.repository.local.PrefConstants
import com.briot.tmmlmss.implementor.repository.local.PrefRepository
import com.briot.tmmlmss.implementor.repository.remote.Product
import com.briot.tmmlmss.implementor.repository.remote.RackLocation
import com.briot.tmmlmss.implementor.repository.remote.RemoteRepository
import java.net.SocketException
import java.net.SocketTimeoutException

class PutAwayLocationScanViewModel : ViewModel() {

    val TAG = "PutAwayLScanViewModel"

    val rackLocation: LiveData<RackLocation> = MutableLiveData<RackLocation>()

    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidLocation: RackLocation = RackLocation()

    fun loadLocationDetails(barcode: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.getLocationDetails(barcode, this::handleRackLocationResponse, this::handleRackLocationError)
    }

    private fun handleRackLocationResponse(rackLocation: RackLocation) {
        Log.d(TAG, "successful putaway Location" + rackLocation.toString())
        PrefRepository.singleInstance.setKeyValue(PrefConstants().PUTAWAYLOCATION, rackLocation.rackID!!)
        (this.rackLocation as MutableLiveData<RackLocation>).value = rackLocation
    }

    private fun handleRackLocationError(error: Throwable) {
        Log.d(TAG, error.localizedMessage ?: "Error occurred")
        PrefRepository.singleInstance.setKeyValue(PrefConstants().PUTAWAYLOCATION, "")

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.rackLocation as MutableLiveData<RackLocation>).value = invalidLocation
        }
    }
}
