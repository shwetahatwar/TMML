package com.briot.tmmlmss.implementor.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.briot.tmmlmss.implementor.repository.remote.*
import retrofit2.HttpException
import java.net.SocketException
import java.net.SocketTimeoutException

    class ReceiveAtStoreViewModel : ViewModel() {
    val TAG = "ReceiveAtStore"

    val sap313Records: LiveData<Array<Sap313Record>> = MutableLiveData<Array<Sap313Record>>()
    val sap315Record: LiveData<Sap315Record> = MutableLiveData<Sap315Record>()

    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    var errorMessage: LiveData<String> = MutableLiveData<String>()
    var record: Sap313Record = Sap313Record()

    fun defaultValue() {
        var record: Sap313Record = Sap313Record()
        record.id = 2980
        record.plant = "7002"
        record.date = "23-09-19"
        record.material = "923100300004"
        record.jobCard = "JO1569245042220001"
        record.uniqueNumber = "AA002182"
        record.quantity = 30
        record.documentNumber = "1191928897"
        record.documentYear = 2019
        record.remarks = "Success"
        record.createdAt = 1569251046532
        record.updatedAt = 1569251070454

        var record2: Sap313Record = Sap313Record()
        record2.id = 2981
        record2.plant = "7002"
        record2.date = "23-09-20"
        record2.material = "923100300005"
        record2.jobCard = "JO1569245042220002"
        record2.uniqueNumber = "AA002183"
        record2.quantity = 43
        record2.documentNumber = "1191928898"
        record2.documentYear = 2019
        record2.remarks = "Success"
        record2.createdAt = 1569251046532
        record2.updatedAt = 1569251070454

        (this.sap313Records as MutableLiveData<Array<Sap313Record>>).value = arrayOf<Sap313Record>(record, record2)
    }

    fun get313Records(barcodeSerial: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.get313Records(barcodeSerial, this::handleGet313Records, this::handleGet313RecordsError)
    }

    fun post315Record(record: Sap313Record) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.postSAP315(record, this::handle315Records, this::handle315RecordsError)
    }

    private fun handleGet313Records(sap313Records: Array<Sap313Record>) {
        Log.d(TAG, "successful job location relations" + sap313Records.toString())
        if (sap313Records.size > 0) {
            (this.sap313Records as MutableLiveData<Array<Sap313Record>>).value = sap313Records
        }else {
            (this.sap313Records as MutableLiveData<Array<Sap313Record>>).value = null
        }
    }

    private fun handleGet313RecordsError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.sap313Records as MutableLiveData<Array<Sap313Record>>).value = null
            (this.sap315Record as MutableLiveData<Array<Sap313Record>>).value = null

            if (error is HttpException) {
                if (error.code() == 404) {
                    (errorMessage as MutableLiveData<String>).value = "Machine not available"
                } else if (error.code() == 404) {
                    (errorMessage as MutableLiveData<String>).value = error.message()
                }

            }
            (this.sap313Records as MutableLiveData<Array<JobLocationRelation>>).value = null
        }
    }

    private fun handle315Records(sap315Record: Sap315Record) {
        Log.d(TAG, "successful job location relations" + sap315Record.toString())
        if (sap315Record != null) {
            (this.sap315Record as MutableLiveData<Sap315Record>).value = sap315Record
        }else {
            (this.sap315Record as MutableLiveData<Array<Sap315Record>>).value = null
        }
    }


    private fun handle315RecordsError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.sap315Record as MutableLiveData<Sap313Record>).value = null

            if (error is HttpException) {
                if (error.code() == 404) {
                    (errorMessage as MutableLiveData<String>).value = "Machine not available"
                } else if (error.code() == 404) {
                    (errorMessage as MutableLiveData<String>).value = error.message()
                }

            }
            (this.sap315Record as MutableLiveData<JobLocationRelation>).value = null
        }
    }

}
