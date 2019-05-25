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
    val TAG = "ScanViewModel"

    val JobcardDetail: LiveData<Array<JobcardDetail>> = MutableLiveData<Array<JobcardDetail>>()

    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidJobcardDetail: JobcardDetail = JobcardDetail()

    fun loadJobcardDetails(barcodeSerial: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.getjobcardDetails(barcodeSerial, this::handleJobcardResponse, this::handleJobcardError)
    }

    private fun handleJobcardResponse(jobcardDetail: Array<JobcardDetail>) {
        Log.d(TAG, "successful jobcard" + jobcardDetail.toString())
        (this.JobcardDetail as MutableLiveData<Array<JobcardDetail>>).value = jobcardDetail

    }

    private fun handleJobcardError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.JobcardDetail as MutableLiveData<Array<JobcardDetail>>).value = arrayOf(invalidJobcardDetail)
        }
    }
}
