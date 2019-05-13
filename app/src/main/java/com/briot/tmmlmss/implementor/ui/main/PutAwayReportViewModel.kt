package com.briot.tmmlmss.implementor.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import android.util.Log
import com.briot.tmmlmss.implementor.repository.local.PrefConstants
import com.briot.tmmlmss.implementor.repository.local.PrefRepository
import com.briot.tmmlmss.implementor.repository.remote.Product
import com.briot.tmmlmss.implementor.repository.remote.RemoteRepository
import java.net.SocketException
import java.net.SocketTimeoutException

class PutAwayReportViewModel : ViewModel() {
    val TAG = "PutAwayReportViewModel"

    val products: LiveData<List<Product>> = MutableLiveData<List<Product>>()

    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidProductList: List<Product> = ArrayList<Product>()

    fun putAwaySubmit(productsbarcode: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        val username = PrefRepository.singleInstance.getValueOrDefault(PrefConstants().USERLOGGEDIN, "")
        RemoteRepository.singleInstance.putAwayReport(username, this::handlePutAwayReportResponse, this::handlePutAwayReportError)
    }

    private fun handlePutAwayReportResponse(items: List<Product>) {
        Log.d(TAG, "successful put away report with total of items - " + items.size)
        (this.products as MutableLiveData<List<Product>>).value = items
    }

    private fun handlePutAwayReportError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.products as MutableLiveData<List<Product>>).value = invalidProductList
        }
    }
}
