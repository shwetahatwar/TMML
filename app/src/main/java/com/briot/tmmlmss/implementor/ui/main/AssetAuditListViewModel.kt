package com.briot.tmmlmss.implementor.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import android.util.Log
import com.briot.tmmlmss.implementor.repository.remote.Audit
import com.briot.tmmlmss.implementor.repository.remote.RemoteRepository
import java.net.SocketException
import java.net.SocketTimeoutException

class AssetAuditListViewModel : ViewModel() {
    val TAG = "AuditListViewModel"

    val pendingAuditList: LiveData<List<Audit>> = MutableLiveData<List<Audit>>()

    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()

    fun pendingAuditList() {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.pendingAuditList(this::handleAuditListResponse, this::handleAuditListError)
    }

    private fun handleAuditListResponse(item: List<Audit>) {
        Log.d(TAG, "successful" + item.toString())
        (this.pendingAuditList as MutableLiveData<List<Audit>>).value = item
    }


    private fun handleAuditListError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

//        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
//        }
    }

}
