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

    val processSequence: LiveData<ProcessSequence> = MutableLiveData<ProcessSequence>()
    val processSequenceNetworkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidProcessSequence: ProcessSequence = ProcessSequence()



    fun updateStopPartProcess(processSequence:Number,machineBarcode: String,jobBarcode: String,quantity:Number,status: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.stopPartProcess(processSequence,machineBarcode,jobBarcode,quantity,status, this::handleStopPartResponse, this::handleStopPartError)
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


    fun getProcessSequence(processSequence:Number) {
        (processSequenceNetworkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.getProcessSequence(processSequence, this::handleProcessSequenceResponse, this::handleProcessSequenceError)
    }

    private fun handleProcessSequenceResponse(processSequence: Array<ProcessSequence>) {
        Log.d(TAG, "successful get start part process" + processSequence.toString())
        (this.processSequence as MutableLiveData<ProcessSequence>).value = processSequence.first()
    }

    private fun handleProcessSequenceError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (processSequenceNetworkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.processSequence as MutableLiveData<ProcessSequence>).value = invalidProcessSequence
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