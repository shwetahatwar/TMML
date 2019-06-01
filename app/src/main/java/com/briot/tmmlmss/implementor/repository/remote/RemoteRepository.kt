package com.briot.tmmlmss.implementor.repository.remote

import com.briot.tmmlmss.implementor.RetrofitHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.reflect.KFunction1

class RemoteRepository {
    companion object {
        val singleInstance = RemoteRepository();
    }

    fun loginUser(username: String, password: String, handleResponse: (SignInResponse) -> Unit, handleError: (Throwable) -> Unit) {
        var signInRequest: SignInRequest = SignInRequest();
        signInRequest.username = username;
        signInRequest.password = password;
        RetrofitHelper.retrofit.create(ApiInterface::class.java)
                .login(signInRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResponse, handleError)
    }

    fun getjobcardDetails(barcodeSerial: String, handleResponse: (Array<JobcardDetail>) -> Unit, handleError: (Throwable) -> Unit) {
        RetrofitHelper.retrofit.create(ApiInterface::class.java)
                .JobcardDetail(barcodeSerial)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResponse, handleError)
    }

    fun machineDetail(barcodeSerial: String, handleResponse: (Array<Machine>) -> Unit, handleError: (Throwable) -> Unit) {
        RetrofitHelper.retrofit.create(ApiInterface::class.java)
                .Machine(barcodeSerial)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResponse, handleError)
    }

    fun updateMachineDetails(machineId:Machine,partReplaced: String,remarks: String,machineStatus: String, handleResponse: (MaintenanceTransactionTable) -> Unit, handleError: (Throwable) -> Unit) {
        var maintenanceTransactionTable: MaintenanceTransactionTable = MaintenanceTransactionTable();
        maintenanceTransactionTable.partReplaced = partReplaced
        maintenanceTransactionTable.remarks = remarks
        maintenanceTransactionTable.machineStatus = machineStatus
     //   maintenanceTransactionTable.machineId=machineId
        RetrofitHelper.retrofit.create(ApiInterface::class.java)
                .UpdateMachineDetails(maintenanceTransactionTable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResponse, handleError)
    }

}