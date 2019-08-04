package com.briot.tmmlmss.implementor.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.briot.tmmlmss.implementor.repository.remote.JobLocationRelation
import com.briot.tmmlmss.implementor.repository.remote.JobLocationRelationDetailed
import com.briot.tmmlmss.implementor.repository.remote.JobcardDetail
import com.briot.tmmlmss.implementor.repository.remote.RemoteRepository
import java.net.SocketException
import java.net.SocketTimeoutException

class PendingItemDashboardViewModel : ViewModel() {
    val TAG = "JobLocationRelation"

    val jobLocations: LiveData<Array<JobLocationRelationDetailed>> = MutableLiveData<Array<JobLocationRelationDetailed>>()

    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()

    val pickStatus: LiveData<Boolean> = MutableLiveData<Boolean>()

    fun loadPendingItems() {
        (networkError as MutableLiveData<Boolean>).value = false
        (pickStatus as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.getPendingJobLocationRelations("", this::handleGetJobLocationRelations, this::handleGetJobLocationRelationsError)
    }

    private fun handleGetJobLocationRelations(jobLocationRelations: Array<JobLocationRelationDetailed>) {
        Log.d(TAG, "successful job location relations" + jobLocationRelations.toString())
        if (jobLocationRelations.size > 0) {
            (this.jobLocations as MutableLiveData<Array<JobLocationRelationDetailed>>).value = jobLocationRelations
        }else {
            (this.jobLocations as MutableLiveData<Array<JobLocationRelationDetailed>>).value = null
        }
    }

    private fun handleGetJobLocationRelationsError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.jobLocations as MutableLiveData<Array<JobLocationRelation>>).value = null
        }
    }

    fun pickItem(jobLocationRelationId: Number, status: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        (pickStatus as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.pickPendingItem(jobLocationRelationId, status, this::handlePickJobLocationRelations, this::handleGPickJobLocationRelationsError)
    }

    private fun handlePickJobLocationRelations(jobLocationRelations: Array<JobLocationRelation>) {
        Log.d(TAG, "successful job location relations" + jobLocationRelations.toString())
        if (jobLocationRelations != null) {
            (this.pickStatus as MutableLiveData<Boolean>).value = true
        }
    }

    private fun handleGPickJobLocationRelationsError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.jobLocations as MutableLiveData<Array<JobLocationRelation>>).value = null
        }
    }
}
