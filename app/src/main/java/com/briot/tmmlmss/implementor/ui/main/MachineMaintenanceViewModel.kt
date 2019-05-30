package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Log
import com.briot.tmmlmss.implementor.repository.remote.Machine
import com.briot.tmmlmss.implementor.repository.remote.RemoteRepository
import java.net.SocketException
import java.net.SocketTimeoutException


class MachineMaintenanceViewModel : ViewModel() {
    val TAG = "MachineScanViewModel"

    val machineDetail: LiveData<Array<Machine>> = MutableLiveData<Array<Machine>>()

    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidMachineDetail: Machine = Machine()

    fun loadMachineDetails(barcodeSerial: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.machineDetail(barcodeSerial, this::handleMachineResponse, this::handleMachineError)
    }

    private fun handleMachineResponse(machineDetail: Array<Machine>) {
        Log.d(TAG, "successful machine Detail" + machineDetail.toString())
        (this.machineDetail as MutableLiveData<Array<Machine>>).value = machineDetail

    }

    private fun handleMachineError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.machineDetail as MutableLiveData<Array<Machine>>).value = arrayOf(invalidMachineDetail)
        }
    }
}
