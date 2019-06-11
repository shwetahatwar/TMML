//package com.briot.tmmlmss.implementor.ui.main
//
//import androidx.lifecycle.ViewModel;
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import android.util.Log
//import com.briot.tmmlmss.implementor.MainActivity
//import com.briot.tmmlmss.implementor.repository.remote.*
//import io.github.pierry.progress.Progress
//import java.net.SocketException
//import java.net.SocketTimeoutException
//
//class StopPartProcessViewModel : ViewModel() {
//
//    val TAG = "StopPartViewModel"
//
//    val stopPartProcess: LiveData<JobProcessSequenceRelation> = MutableLiveData<JobProcessSequenceRelation>()
//    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()
//    val invalidStopPartProcess: JobProcessSequenceRelation = JobProcessSequenceRelation()
//
//    val machine: LiveData<Machine> = MutableLiveData<Machine>()
//    val machineNetworkError: LiveData<Boolean> = MutableLiveData<Boolean>()
//    val invalidMachine: Machine = Machine()
//
//    val jobcardDetails: LiveData<JobcardDetail> = MutableLiveData<JobcardDetail>()
//    val jobcardNetworkError: LiveData<Boolean> = MutableLiveData<Boolean>()
//    val invalidJobcardDetail: JobcardDetail = JobcardDetail()
//
//
//    fun updateStopPartProcess(machineId:Number,jobId: Number,quantity:Number,status: String,note: String) {
//        (networkError as MutableLiveData<Boolean>).value = false
//        RemoteRepository.singleInstance.stopPartProcess(machineId,jobId,quantity,status,note, this::handleStopPartResponse, this::handleStopPartError)
//    }
//
//    private fun handleStopPartResponse(jobProcessSequenceRelation: Array<JobProcessSequenceRelation>) {
//        Log.d(TAG, "successful stop part process" + jobProcessSequenceRelation.toString())
//        (this.stopPartProcess as MutableLiveData<Array<JobProcessSequenceRelation>>).value = jobProcessSequenceRelation
//    }
//
//    private fun handleStopPartError(error: Throwable) {
//        Log.d(TAG, error.localizedMessage)
//
//        if (error is SocketException || error is SocketTimeoutException) {
//            (networkError as MutableLiveData<Boolean>).value = true
//        } else {
//            (this.stopPartProcess as MutableLiveData<Array<JobProcessSequenceRelation>>).value = arrayOf(invalidStopPartProcess)
//        }
//    }
//
//
//
//    fun loadMachineDetails(machineId: Number) {
//        (machineNetworkError as MutableLiveData<Boolean>).value = false
//        RemoteRepository.singleInstance.getMachineId(machineId, this::handleUpdateMachineResponse, this::handleUpdateMachineError)
//    }
//
//    private fun handleUpdateMachineResponse(machineDetail: Array<Machine>) {
//        Log.d(TAG, "successful machine Id" + machineDetail.toString())
//        (this.machine as MutableLiveData<Machine>).value = machineDetail.first()
//    }
//
//    private fun handleUpdateMachineError(error: Throwable) {
//        Log.d(TAG, error.localizedMessage)
//
//        if (error is SocketException || error is SocketTimeoutException) {
//            (machineNetworkError as MutableLiveData<Boolean>).value = true
//        } else {
//            (this.machine as MutableLiveData<Array<Machine>>).value = arrayOf(invalidMachine)
//        }
//    }
//
//
//
//    fun loadJobcardDetails(jobId: Number) {
//        (jobcardNetworkError as MutableLiveData<Boolean>).value = false
//        RemoteRepository.singleInstance.getJobcardId(jobId, this::handleUpdateJobcardResponse, this::handleUpdateJobcardError)
//    }
//
//    private fun handleUpdateJobcardResponse(jobcardDetail: Array<JobcardDetail>) {
//        Log.d(TAG, "successful jobcard id" + jobcardDetail.toString())
//        (this.jobcardDetails as MutableLiveData<Array<JobcardDetail>>).value = jobcardDetail
//    }
//
//    private fun handleUpdateJobcardError(error: Throwable) {
//        Log.d(TAG, error.localizedMessage)
//
//        if (error is SocketException || error is SocketTimeoutException) {
//            (jobcardNetworkError as MutableLiveData<Boolean>).value = true
//        } else {
//            (this.jobcardDetails as MutableLiveData<Array<JobcardDetail>>).value = arrayOf(invalidJobcardDetail)
//        }
//    }
//}