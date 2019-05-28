package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import android.util.Log
import com.briot.tmmlmss.implementor.repository.remote.JobcardDetail
import com.briot.tmmlmss.implementor.repository.remote.RemoteRepository
import java.net.SocketException
import java.net.SocketTimeoutException

class JobcardDetailsScanViewModel : ViewModel() {
    val TAG = "JobcardDetailViewModel"

    val jobcardInfo: LiveData<Array<JobcardDetail>> = MutableLiveData<Array<JobcardDetail>>()

    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidJobcardInfo: JobcardDetail = JobcardDetail()

    fun loadJobcardDetails(barcode: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.getjobcardDetails(barcode, this::handleJobcardResponse, this::handleJobcardError)
    }

    private fun handleJobcardResponse(jobcardInformation: Array<JobcardDetail>) {
        Log.d(TAG, "successful jobcard" + jobcardInformation.toString())
        (this.jobcardInfo as MutableLiveData<Array<JobcardDetail>>).value = jobcardInformation

    }

    private fun handleJobcardError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.jobcardInfo as MutableLiveData<Array<JobcardDetail>>).value = arrayOf(invalidJobcardInfo)
        }
    }
}
