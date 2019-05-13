package com.briot.tmmlmss.implementor.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import android.util.Log
import com.briot.tmmlmss.implementor.repository.local.PrefConstants
import com.briot.tmmlmss.implementor.repository.local.PrefRepository
import com.briot.tmmlmss.implementor.repository.remote.Product
import com.briot.tmmlmss.implementor.repository.remote.PutAwaySubmission
import com.briot.tmmlmss.implementor.repository.remote.RemoteRepository
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reactivex.internal.operators.observable.ObservableDoOnEach
import io.reactivex.schedulers.Schedulers
import java.net.SocketException
import java.net.SocketTimeoutException

class PutAwayProductScanViewModel : ViewModel() {
    val TAG = "PutAwaySubmitViewModel"

    val product: LiveData<Product> = MutableLiveData<Product>()

    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidProduct: Product = Product()

    fun putAwaySubmit(productsbarcode: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        val username = PrefRepository.singleInstance.getValueOrDefault(PrefConstants().USERLOGGEDIN, "")
        val locationbarcode: String = PrefRepository.singleInstance.getValueOrDefault(PrefConstants().PUTAWAYLOCATION, "")
        RemoteRepository.singleInstance.putAwaySubmit(locationbarcode, username, productsbarcode, this::handlePutAwaySubmitResponse, this::handlePutAwaySubmitError)
    }

    private fun handlePutAwaySubmitResponse(item: Product) {
        Log.d(TAG, "successful put away" + item.toString())
        (this.product as MutableLiveData<Product>).value = item
    }

    private fun handlePutAwaySubmitError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.product as MutableLiveData<Product>).value = invalidProduct
        }
    }
}
