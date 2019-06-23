package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import android.util.Log
import com.briot.tmmlmss.implementor.repository.remote.JobcardDetail
import com.briot.tmmlmss.implementor.repository.remote.ProductionSchedule
import com.briot.tmmlmss.implementor.repository.remote.ProductionSchedulePartRelation
import com.briot.tmmlmss.implementor.repository.remote.RemoteRepository
import java.net.SocketException
import java.net.SocketTimeoutException

class JobcardDetailsScanViewModel : ViewModel() {
    val TAG = "JobcardScanViewModel"

    val jobcardDetails: LiveData<JobcardDetail> = MutableLiveData<JobcardDetail>()
    val productionSchedulePartRelation: LiveData<ProductionSchedulePartRelation> = MutableLiveData<ProductionSchedulePartRelation>()

    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidJobcardDetail: JobcardDetail = JobcardDetail()

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
            (this.jobcardDetails as MutableLiveData<JobcardDetail>).value = null
        }
    }

    fun loadProductionSchedule(id: Number) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.getProductionSchedulePartRelationDetails(id, this::handleGetProductionScheduleResponse, this::handleGetProductionScheduleError)
    }

    private fun handleGetProductionScheduleResponse(productionSchedulePartRelations: Array<ProductionSchedulePartRelation>) {
        Log.d(TAG, "successful production schedule part relations" + productionSchedulePartRelations.toString())
        if (productionSchedulePartRelations != null && productionSchedulePartRelations.size > 0) {
            (this.productionSchedulePartRelation as MutableLiveData<ProductionSchedulePartRelation>).value = productionSchedulePartRelations.first()
        }else {
            (this.productionSchedulePartRelation as MutableLiveData<ProductionSchedulePartRelation>).value = null

        }
    }

    private fun handleGetProductionScheduleError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.productionSchedulePartRelation as MutableLiveData<ProductionSchedulePartRelation>).value = null
        }
    }

}
