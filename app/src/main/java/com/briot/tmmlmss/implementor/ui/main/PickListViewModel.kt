////package com.briot.tmmlmss.implementor.ui.main
////
////import androidx.lifecycle.LiveData
////import androidx.lifecycle.MutableLiveData
////import androidx.lifecycle.ViewModel;
////import android.util.Log
////import com.briot.tmmlmss.implementor.repository.local.PrefConstants
////import com.briot.tmmlmss.implementor.repository.local.PrefRepository
////import com.briot.tmmlmss.implementor.repository.remote.pendingJobcard
//////import com.briot.tmmlmss.implementor.repository.remote.Product
//////import com.briot.tmmlmss.implementor.repository.remote.PutAwaySubmission
////import com.briot.tmmlmss.implementor.repository.remote.RemoteRepository
////import io.reactivex.Observable
////import io.reactivex.ObservableSource
////import io.reactivex.android.schedulers.AndroidSchedulers
////import io.reactivex.functions.BiFunction
////import io.reactivex.functions.Function
////import io.reactivex.internal.operators.observable.ObservableDoOnEach
////import io.reactivex.schedulers.Schedulers
////import java.net.SocketException
////import java.net.SocketTimeoutException
////import java.util.ArrayList
////import kotlin.collections.List
////
////class PickListViewModel : ViewModel() {
////    val TAG = "PendingJobcardViewModel"
////
////    val pending: LiveData<List<pendingJobcard>> = MutableLiveData<List<pendingJobcard>>()
////    //val pending: List<PendingPicklist> = LiveData<PendingPicklist>()
////
////    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()
////
////    fun JobcardSubmit() {
////        (networkError as MutableLiveData<Boolean>).value = false
////        //val newpicklistID = PrefRepository.singleInstance.getValueOrDefault(PrefConstants().PICKLISTID, "")
////        RemoteRepository.singleInstance.pendingJobcard(this::handlePicklistSubmitResponse, this::handlePicklistSubmitError)
////    }
////
////    private fun handlePicklistSubmitResponse(item: List<pendingJobcard>) {
////        Log.d(TAG, "successful" + item.toString())
////        (this.pending as MutableLiveData<List<pendingJobcard>>).value = item
////    }
////
////
////    private fun handlePicklistSubmitError(error: Throwable) {
////        Log.d(TAG, error.localizedMessage)
////
////        if (error is SocketException || error is SocketTimeoutException) {
////            (networkError as MutableLiveData<Boolean>).value = true
////        }
////    }
////}

