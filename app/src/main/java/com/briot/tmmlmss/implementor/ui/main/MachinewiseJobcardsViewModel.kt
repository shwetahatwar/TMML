package com.briot.tmmlmss.implementor.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.briot.tmmlmss.implementor.repository.remote.JobLocationRelation
import com.briot.tmmlmss.implementor.repository.remote.RemoteRepository
import retrofit2.HttpException
import java.net.SocketException
import java.net.SocketTimeoutException

class MachinewiseJobcardsViewModel : ViewModel() {

    val TAG = "MachineWiseJobcard"

    val jobcards: LiveData<Array<String>> = MutableLiveData<Array<String>>()
    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    var errorMessage: LiveData<String> = MutableLiveData<String>()

    fun loadTestValues() {
        (this.jobcards as MutableLiveData<Array<String>>).value = arrayOf("Jobcard1", "Jobcard2", "jobcard3", "jobcard4", "Jobcard5")
    }


    fun getJobcardsOnMachine(barcodeSerial: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.getJobCardByMachine(barcodeSerial, this::handleMachineWiseJobcardList, this::handleMachineWiseJobcardError)
    }

    private fun handleMachineWiseJobcardList(jobcards: Array<String>) {
        Log.d(TAG, "successful job location relations" + jobcards.toString())
        if (jobcards.size > 0) {
            (this.jobcards as MutableLiveData<Array<String>>).value = jobcards
        }else {
            (this.jobcards as MutableLiveData<Array<String>>).value = null
        }
    }

    private fun handleMachineWiseJobcardError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
//            (this.jobcards as MutableLiveData<Array<String>>).value = null

            if (error is HttpException) {
                if (error.code() == 404) {
                    (errorMessage as MutableLiveData<String>).value = "Machine not available"
                } else if (error.code() == 404) {
                    (errorMessage as MutableLiveData<String>).value = error.message()
                }

            }
//            (this.jobcards as MutableLiveData<Array<String>>).value = null
        }
    }

}
