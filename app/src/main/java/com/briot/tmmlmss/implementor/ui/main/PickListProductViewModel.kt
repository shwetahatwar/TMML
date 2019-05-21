package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import com.briot.tmmlmss.implementor.repository.local.PrefConstants
import com.briot.tmmlmss.implementor.repository.local.PrefRepository
import com.briot.tmmlmss.implementor.repository.remote.PendingPicklist
import com.briot.tmmlmss.implementor.repository.remote.Product
import com.briot.tmmlmss.implementor.repository.remote.RemoteRepository
import java.net.SocketException
import java.net.SocketTimeoutException

class PickListProductViewModel : ViewModel(){
    val TAG = "PickListProductViewMod"

    val picklist: LiveData<List<PendingPicklist>> = MutableLiveData<List<PendingPicklist>>()

    val product: LiveData<Product> = MutableLiveData<Product>()

    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()

    fun picklistProductScan(PicklistID: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.picklist(PicklistID, this::handlePickListResponse, this::handlePickListError)
    }

    private fun handlePickListResponse(picklist: List<PendingPicklist>) {
        Log.d(TAG, "successful product" + picklist.toString())
        (this.picklist as MutableLiveData<List<PendingPicklist>>).value = picklist
    }

    private fun handlePickListError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        }
    }

    fun pickListProductSubmit(productbarcode: String, picklistId: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.picklistProductSubmit(productbarcode, picklistId, this::handlePicklistSubmitResponse, this::handlePicklistSubmitError)
    }

    private fun handlePicklistSubmitResponse(item: Product) {
        Log.d(TAG, "successful picklist product" + item.toString())
//        (this.product as MutableLiveData<Product>).value = item
    }

    private fun handlePicklistSubmitError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
//            (this.product as MutableLiveData<Product>).value = invalidProduct
        }
    }

    fun completePicklistProduct(picklistId: String){
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.completePicklistProduct(picklistId, this::handlecompletePicklistProductResponse, this::handlecompletePicklistProductError)
    }

    private fun handlecompletePicklistProductResponse(item: PendingPicklist) {
        Log.d(TAG, "successful picklist product" + item.toString())
//        (this.product as MutableLiveData<Product>).value = item
    }

    private fun handlecompletePicklistProductError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
//            (this.product as MutableLiveData<Product>).value = invalidProduct
        }
    }
}