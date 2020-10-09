package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;

import android.util.Log
import com.briot.tmmlmss.implementor.MainActivity
import com.briot.tmmlmss.implementor.repository.remote.*
import com.google.gson.JsonParser
import io.github.pierry.progress.Progress
import retrofit2.HttpException
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
    var errorMessage: String? = ""

    val jobcardDetails: LiveData<JobcardDetail> = MutableLiveData<JobcardDetail>()
    val jobcardNetworkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidJobcardDetail: JobcardDetail = JobcardDetail()

    val employee: LiveData<Employee> = MutableLiveData<Employee>()
    val employeeNetworkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidEmployee: Employee = Employee()



    fun postStartPartProcess(machineBarcode: String,jobBarcode: String,multiFaactor:Number,operatorId:Number) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.startPartProcess(machineBarcode,jobBarcode,multiFaactor,operatorId, this::handleStartPartResponse, this::handleStartPartError)
    }

    private fun handleStartPartResponse(jobProcessSequenceRelation: JobProcessSequenceRelation) {
        Log.d(TAG, "successful start part process" + jobProcessSequenceRelation.toString())
        (this.startPartProcess as MutableLiveData<JobProcessSequenceRelation>).value = jobProcessSequenceRelation
    }

    private fun handleStartPartError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)
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
        } else {
            (this.startPartProcess as MutableLiveData<JobProcessSequenceRelation>).value = invalidStartPartProcess
        }
    }



    fun loadMachineDetails(barcodeSerial: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.getMachineDetail(barcodeSerial, this::handleMachineResponse, this::handleMachineError)
    }

    private fun handleMachineResponse(machineDetail: Array<Machine>) {
        Log.d(TAG, "successful machine Detail" + machineDetail.toString())
        if (machineDetail.size > 0) {
            (this.machine as MutableLiveData<Machine>).value = machineDetail.first()
        } else {
            (this.machine as MutableLiveData<Machine>).value = null
        }

    }

    private fun handleMachineError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.machine as MutableLiveData<Machine>).value = invalidMachine
        }
    }



    fun loadJobcardDetails(barcodeSerial: String) {
        (networkError as MutableLiveData<Boolean>).value = false
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
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.jobcardDetails as MutableLiveData<JobcardDetail>).value = invalidJobcardDetail
        }
    }



    fun getEmployeeDetail(operatorId: String) {
        (employeeNetworkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.getEmployeeDetail(operatorId, this::handleUserDetailResponse, this::handleUserDetailError)
    }

    private fun handleUserDetailResponse(employees: Array<Employee>) {
        Log.d(TAG, "successful user id" + employee.toString())
        if (employees.size > 0) {
            (this.employee as MutableLiveData<Employee>).value = employees.first()
        } else {
            (this.employee as MutableLiveData<Employee>).value = null
        }
    }

    private fun handleUserDetailError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (employeeNetworkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.employee as MutableLiveData<Employee>).value = null
        }
    }

}
