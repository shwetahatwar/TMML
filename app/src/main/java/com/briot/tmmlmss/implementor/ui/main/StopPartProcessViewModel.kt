package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Log
import com.briot.tmmlmss.implementor.MainActivity
import com.briot.tmmlmss.implementor.repository.remote.*
import com.google.gson.JsonParser
import io.github.pierry.progress.Progress
import retrofit2.HttpException
import java.net.SocketException
import java.net.SocketTimeoutException

class StopPartProcessViewModel : ViewModel() {

    val TAG = "StopPartViewModel"

    val stopPartProcess: LiveData<JobProcessSequenceRelation> = MutableLiveData<JobProcessSequenceRelation>()
    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidStopPartProcess: JobProcessSequenceRelation = JobProcessSequenceRelation()
    var errorMessage: String? = ""

    val machine: LiveData<Machine> = MutableLiveData<Machine>()
    val machineNetworkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidMachine: Machine = Machine()

    val jobcardDetails: LiveData<JobcardDetail> = MutableLiveData<JobcardDetail>()
    val jobcardNetworkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidJobcardDetail: JobcardDetail = JobcardDetail()

    val jobProcessSequenceRelation: LiveData<JobProcessSequenceRelation> = MutableLiveData<JobProcessSequenceRelation>()
    val jobProcessSequenceRelationNetworkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidJobProcessSequenceRelation: JobProcessSequenceRelation = JobProcessSequenceRelation()



    fun updateStopPartProcess(machineBarcode:String,jobBarcode: String,quantity:Number,status: String,note: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.stopPartProcess(machineBarcode,jobBarcode,quantity,status,note, this::handleStopPartResponse, this::handleStopPartError)
    }

    private fun handleStopPartResponse(jobProcessSequenceRelation: JobProcessSequenceRelation) {
        Log.d(TAG, "successful stop part process" + jobProcessSequenceRelation.toString())
        (this.stopPartProcess as MutableLiveData<JobProcessSequenceRelation>).value = jobProcessSequenceRelation
    }

    private fun handleStopPartError(error: Throwable) {
        Log.d(TAG,error.localizedMessage)
        if (error is HttpException) {
            if (error.code() >= 401) {
                var msg = (error as HttpException).response().errorBody()?.string()
                if (msg != null && msg.isNotEmpty()) {
                    errorMessage = msg;
                } else {
                    errorMessage = error.message()
                }
            }
            (networkError as MutableLiveData<Boolean>).value = true
        }

        else if (error is SocketException || error is SocketTimeoutException) {
            errorMessage = "please check your network connection"
            (networkError as MutableLiveData<Boolean>).value = true
            (this.stopPartProcess as MutableLiveData<JobProcessSequenceRelation>).value = null
        } else {
            (this.stopPartProcess as MutableLiveData<JobProcessSequenceRelation>).value = null
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
            errorMessage = "please check your network connection"
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
        if (machineDetail.size > 0) {
            (this.machine as MutableLiveData<Machine>).value = machineDetail.first()
        }else {
            (this.machine as MutableLiveData<Machine>).value = null

        }

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
        if (jobcardDetail.size > 0) {
            (this.jobcardDetails as MutableLiveData<JobcardDetail>).value = jobcardDetail.first()
        }else {
            (this.jobcardDetails as MutableLiveData<JobcardDetail>).value = null

        }
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