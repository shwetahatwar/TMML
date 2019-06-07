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

    val machine: LiveData<Array<Machine>> = MutableLiveData<Array<Machine>>()
    val machineNetworkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidMachine: Machine = Machine()

    val jobcardDetails: LiveData<Array<JobcardDetail>> = MutableLiveData<Array<JobcardDetail>>()
    val jobcardNetworkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidJobcardDetail: JobcardDetail = JobcardDetail()

    val user: LiveData<Array<User>> = MutableLiveData<Array<User>>()
    val userNetworkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidUser: User = User()



    fun postStartPartProcess(machineId: Number,jobId: Number,multiFaactor:Number,operatorId:Number) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.startPartProcess(machineId,jobId,multiFaactor,operatorId, this::handleStartPartResponse, this::handleStartPartError)
    }

    private fun handleStartPartResponse(jobProcessSequenceRelation: JobProcessSequenceRelation) {
        Log.d(TAG, "successful start part process" + jobProcessSequenceRelation.toString())
        (this.startPartProcess as MutableLiveData<JobProcessSequenceRelation>).value = jobProcessSequenceRelation
    }

    private fun handleStartPartError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.startPartProcess as MutableLiveData<JobProcessSequenceRelation>).value = invalidStartPartProcess
        }
    }



    fun loadMachineDetails(machineId: Number) {
        (machineNetworkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.getMachineId(machineId, this::handleUpdateMachineResponse, this::handleUpdateMachineError)
    }

    private fun handleUpdateMachineResponse(machineDetail: Array<Machine>) {
        Log.d(TAG, "successful machine Id" + machineDetail.toString())
        (this.machine as MutableLiveData<Array<Machine>>).value = machineDetail
    }

    private fun handleUpdateMachineError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (machineNetworkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.machine as MutableLiveData<Array<Machine>>).value = arrayOf(invalidMachine)
        }
    }



    fun loadJobcardDetails(jobId: Number) {
        (jobcardNetworkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.getJobcardId(jobId, this::handleUpdateJobcardResponse, this::handleUpdateJobcardError)
    }

    private fun handleUpdateJobcardResponse(jobcardDetail: Array<JobcardDetail>) {
        Log.d(TAG, "successful jobcard id" + jobcardDetail.toString())
        (this.jobcardDetails as MutableLiveData<Array<JobcardDetail>>).value = jobcardDetail
    }

    private fun handleUpdateJobcardError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (jobcardNetworkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.jobcardDetails as MutableLiveData<Array<JobcardDetail>>).value = arrayOf(invalidJobcardDetail)
        }
    }



    fun getUserDetail(operatorId:Number) {
        (userNetworkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.getUserDetail(operatorId.toString(), this::handleUserDetailResponse, this::handleUserDetailError)
    }

    private fun handleUserDetailResponse(user: Array<User>) {
        Log.d(TAG, "successful user id" + user.toString())
        (this.user as MutableLiveData<Array<User>>).value = user
    }

    private fun handleUserDetailError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (userNetworkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.user as MutableLiveData<Array<User>>).value = arrayOf(invalidUser)
        }
    }



}
