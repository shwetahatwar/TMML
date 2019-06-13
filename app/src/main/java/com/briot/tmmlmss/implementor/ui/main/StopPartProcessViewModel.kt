package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Log
import com.briot.tmmlmss.implementor.MainActivity
import com.briot.tmmlmss.implementor.repository.remote.*
import io.github.pierry.progress.Progress
import java.net.SocketException
import java.net.SocketTimeoutException

class StopPartProcessViewModel : ViewModel() {

    val TAG = "StopPartViewModel"

    val stopPartProcess: LiveData<JobProcessSequenceRelation> = MutableLiveData<JobProcessSequenceRelation>()
    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidStopPartProcess: JobProcessSequenceRelation = JobProcessSequenceRelation()

    val machine: LiveData<Machine> = MutableLiveData<Machine>()
    val machineNetworkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidMachine: Machine = Machine()

    val jobcardDetails: LiveData<JobcardDetail> = MutableLiveData<JobcardDetail>()
    val jobcardNetworkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidJobcardDetail: JobcardDetail = JobcardDetail()

    val jobProcessSequenceRelation: LiveData<JobProcessSequenceRelation> = MutableLiveData<JobProcessSequenceRelation>()
    val jobProcessSequenceRelationNetworkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidJobProcessSequenceRelation: JobProcessSequenceRelation = JobProcessSequenceRelation()



    fun updateStopPartProcess(id:Number,machineBarcode:String,jobBarcode: String,quantity:Number,status: String,note: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.stopPartProcess(id,machineBarcode,jobBarcode,quantity,status,note, this::handleStopPartResponse, this::handleStopPartError)
    }

    private fun handleStopPartResponse(jobProcessSequenceRelation: JobProcessSequenceRelation) {
        Log.d(TAG, "successful stop part process" + jobProcessSequenceRelation.toString())
        (this.stopPartProcess as MutableLiveData<JobProcessSequenceRelation>).value = jobProcessSequenceRelation
    }

    private fun handleStopPartError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.stopPartProcess as MutableLiveData<JobProcessSequenceRelation>).value = invalidStopPartProcess
        }
    }

    fun loadJobProcessSequenceRelation(id:Number) {
        (jobProcessSequenceRelationNetworkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.getJobProcessSequenceRelation(id, this::handleJobProcessSequenceRelationResponse, this::handleJobProcessSequenceRelationError)
    }

    private fun handleJobProcessSequenceRelationResponse(jobProcessSequenceRelation: Array<JobProcessSequenceRelation>) {
        Log.d(TAG, "successful JobProcessSequenceRelation Detail" + jobProcessSequenceRelation.toString())
        (this.jobProcessSequenceRelation as MutableLiveData<JobProcessSequenceRelation>).value = jobProcessSequenceRelation.first()

    }

    private fun handleJobProcessSequenceRelationError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (jobProcessSequenceRelationNetworkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.jobProcessSequenceRelation as MutableLiveData<JobProcessSequenceRelation>).value = invalidJobProcessSequenceRelation
        }
    }



    fun loadMachineDetails(barcodeSerial: String) {
        (machineNetworkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.getMachineDetail(barcodeSerial, this::handleMachineResponse, this::handleMachineError)
    }

    private fun handleMachineResponse(machineDetail: Array<Machine>) {
        Log.d(TAG, "successful machine Detail" + machineDetail.toString())
        (this.machine as MutableLiveData<Machine>).value = machineDetail.first()

    }

    private fun handleMachineError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (machineNetworkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.machine as MutableLiveData<Machine>).value = invalidMachine
        }
    }



    fun loadJobcardDetails(barcodeSerial: String) {
        (jobcardNetworkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.getJobcardDetails(barcodeSerial, this::handleJobcardResponse, this::handleJobcardError)
    }

    private fun handleJobcardResponse(jobcardDetail: Array<JobcardDetail>) {
        Log.d(TAG, "successful jobcard" + jobcardDetail.toString())
        (this.jobcardDetails as MutableLiveData<JobcardDetail>).value = jobcardDetail.first()
    }

    private fun handleJobcardError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (jobcardNetworkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.jobcardDetails as MutableLiveData<JobcardDetail>).value = invalidJobcardDetail
        }
    }
}