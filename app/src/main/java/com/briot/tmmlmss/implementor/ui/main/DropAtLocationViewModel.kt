package com.briot.tmmlmss.implementor.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.briot.tmmlmss.implementor.repository.remote.JobLocationRelation
import com.briot.tmmlmss.implementor.repository.remote.RemoteRepository
import java.net.SocketException
import java.net.SocketTimeoutException

class DropAtLocationViewModel : ViewModel() {

    val TAG = "DropAtLocationViewModel"

    val jobLocationRelations: LiveData<Array<JobLocationRelation>> = MutableLiveData<Array<JobLocationRelation>>()

    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()

    fun dropLocationForPendingItem(jobLocationRelationId: Number, dropLocationBarcode: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.dropLocationForPendingItem(jobLocationRelationId, dropLocationBarcode, this::handleDropJobLocationRelations, this::handleDropJobLocationRelationsError)
    }

    private fun handleDropJobLocationRelations(jobLocationRelations: Array<JobLocationRelation>) {
        Log.d(TAG, "successful job location relations" + jobLocationRelations.toString())
        if (jobLocationRelations.size > 0) {
            (this.jobLocationRelations as MutableLiveData<Array<JobLocationRelation>>).value = jobLocationRelations
        }else {
            (this.jobLocationRelations as MutableLiveData<Array<JobLocationRelation>>).value = null
        }
    }

    private fun handleDropJobLocationRelationsError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.jobLocationRelations as MutableLiveData<Array<JobLocationRelation>>).value = null
        }
    }

}
