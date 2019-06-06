package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Log
import com.briot.tmmlmss.implementor.MainActivity
import com.briot.tmmlmss.implementor.repository.remote.Machine
import com.briot.tmmlmss.implementor.repository.remote.MaintenanceTransaction
import com.briot.tmmlmss.implementor.repository.remote.RemoteRepository
import io.github.pierry.progress.Progress
import java.net.SocketException
import java.net.SocketTimeoutException



class MachineMaintenanceViewModel : ViewModel() {
    val TAG = "MachineScanViewModel"

    val machine: LiveData<Machine> = MutableLiveData<Machine>()
    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidMachine: Machine = Machine()
    //val machineId:Number?=null

    val maintenanceTransaction: LiveData<MaintenanceTransaction> = MutableLiveData<MaintenanceTransaction>()
    val maintenanceTransactionNetworkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidMaintenanceTransaction: MaintenanceTransaction = MaintenanceTransaction()



    fun loadMachineDetails(barcodeSerial: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.getMachineDetail(barcodeSerial, this::handleMachineResponse, this::handleMachineError)
    }

    private fun handleMachineResponse(machineDetail: Array<Machine>) {
        Log.d(TAG, "successful machine Detail" + machineDetail.toString())
        (this.machine as MutableLiveData<Machine>).value = machineDetail.first()

    }

    private fun handleMachineError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.machine as MutableLiveData<Array<Machine>>).value = arrayOf(invalidMachine)
        }
    }

    fun updateMachineDetails(machineId:Number,partReplaced: String,remarks: String,status: String) {
        (maintenanceTransactionNetworkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.updateMachineDetails(machineId,partReplaced,remarks,status, this::handleupdateMachineResponse, this::handleupdateMachineError)

    }

    private fun handleupdateMachineResponse(maintenanceTransaction: Array<MaintenanceTransaction>) {
        Log.d(TAG, "successful Update PartNumber" + maintenanceTransaction.toString())
        (this.maintenanceTransaction as MutableLiveData<MaintenanceTransaction>).value = maintenanceTransaction.first()

    }

    private fun handleupdateMachineError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (maintenanceTransactionNetworkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.maintenanceTransaction as MutableLiveData<Array<MaintenanceTransaction>>).value = arrayOf(invalidMaintenanceTransaction)
        }
    }
}
