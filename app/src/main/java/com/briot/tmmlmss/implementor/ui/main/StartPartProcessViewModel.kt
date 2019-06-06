package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;

import android.util.Log
import com.briot.tmmlmss.implementor.MainActivity
import com.briot.tmmlmss.implementor.repository.remote.*
import io.github.pierry.progress.Progress
import java.net.SocketException
import java.net.SocketTimeoutException

class StartPartProcessViewModel : ViewModel() {

    val TAG = "StartPartViewModel"

    val startPartProcess: LiveData<JobProcessSequenceRelation> = MutableLiveData<JobProcessSequenceRelation>()
    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidStartPartProcess: JobProcessSequenceRelation = JobProcessSequenceRelation()

    val machine: LiveData<Machine> = MutableLiveData<Machine>()
    val machineNetworkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidMachine: Machine = Machine()

    val jobcardDetails: LiveData<Array<JobcardDetail>> = MutableLiveData<Array<JobcardDetail>>()
    val jobcardNetworkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidJobcardDetail: JobcardDetail = JobcardDetail()


    fun postStartPartProcess(machineId:Number) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.startPartProcess(machineId, this::handleStartPartResponse, this::handleStartPartError)
    }
    private fun handleStartPartResponse(jobProcessSequenceRelation: Array<JobProcessSequenceRelation>) {
        Log.d(TAG, "successful start part process" + jobProcessSequenceRelation.toString())
        (this.startPartProcess as MutableLiveData<JobProcessSequenceRelation>).value = jobProcessSequenceRelation.first()
    }
    private fun handleStartPartError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.startPartProcess as MutableLiveData<Array<JobProcessSequenceRelation>>).value = arrayOf(invalidStartPartProcess)
        }
    }



    fun loadMachineDetails(machineId:Number) {
        (machineNetworkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.getMachineDetail(machineId.toString(), this::handleUpdateMachineResponse, this::handleUpdateMachineError)
    }
    private fun handleUpdateMachineResponse(machineDetail: Array<Machine>) {
        Log.d(TAG, "successful machine Detail" + machineDetail.toString())
        (this.machine as MutableLiveData<Machine>).value = machineDetail.first()
    }
    private fun handleUpdateMachineError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (machineNetworkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.machine as MutableLiveData<Array<Machine>>).value = arrayOf(invalidMachine)
        }
    }




//    fun loadJobcardDetails(jobId: Number) {
//        (jobcardNetworkError as MutableLiveData<Boolean>).value = false
//        RemoteRepository.singleInstance.getJobcardDetails(jobId.toString(), this::handleUpdateJobcardResponse, this::handleUpdateJobcardError)
//    }
//    private fun handleUpdateJobcardResponse(jobcardDetail: Array<JobcardDetail>) {
//        Log.d(TAG, "successful jobcard id" + jobcardDetail.toString())
//        (this.jobcardDetails as MutableLiveData<Array<JobcardDetail>>).value = jobcardDetail
//    }
//    private fun handleUpdateJobcardError(error: Throwable) {
//        Log.d(TAG, error.localizedMessage)
//
//        if (error is SocketException || error is SocketTimeoutException) {
//            (jobcardNetworkError as MutableLiveData<Boolean>).value = true
//        } else {
//            (this.jobcardDetails as MutableLiveData<Array<JobcardDetail>>).value = arrayOf(invalidJobcardDetail)
//        }
//    }

}
