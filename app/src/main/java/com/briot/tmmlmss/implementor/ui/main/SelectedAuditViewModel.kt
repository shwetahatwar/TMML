package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import android.util.Log
import com.briot.tmmlmss.implementor.repository.remote.Asset
import com.briot.tmmlmss.implementor.repository.remote.Audit
import com.briot.tmmlmss.implementor.repository.remote.AuditDetails
import com.briot.tmmlmss.implementor.repository.remote.RemoteRepository
import java.net.SocketException
import java.net.SocketTimeoutException

class SelectedAuditViewModel : ViewModel() {

    val TAG = "SelectedAuditViewModel"
    val pendingAudit: LiveData<Audit> =  MutableLiveData<Audit>()

    val pendingAuditAssetsList: LiveData<List<Asset>> = MutableLiveData<List<Asset>>()
    val lastAuditedAssetResult: LiveData<Asset> = MutableLiveData<Asset>()
    val selectedAudit: LiveData<Audit> = MutableLiveData<Audit>()

    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()

    fun pendingAuditList(auditId: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.pendingAuditAssetsList(auditId, this::handleResponse, this::handleError)
    }

    fun submitAssetAudit(auditDetails: AuditDetails) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.submitAuditAssetDetails(auditDetails, this::handleAuditResponse, this::handleError)
    }

    fun submitAuditReport(auditId: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.submitAuditReport(auditId, this::handleSubmitAuditReportResponse, this::handleError)
    }

    private fun handleSubmitAuditReportResponse(item: Audit) {
        Log.d(TAG, "successful" + item.toString())
        (this.selectedAudit as MutableLiveData<Audit>).value = item
    }

    private fun handleAuditResponse(item: Asset) {
        Log.d(TAG, "successful" + item.toString())
        (this.lastAuditedAssetResult as MutableLiveData<Asset>).value = item
    }

    private fun handleResponse(item: List<Asset>) {
        Log.d(TAG, "successful" + item.toString())
        (this.pendingAuditAssetsList as MutableLiveData<List<Asset>>).value = item
    }


    private fun handleError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        //if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        //}
    }
}
