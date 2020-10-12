package com.briot.tmmlmss.implementor.repository.remote

import com.briot.tmmlmss.implementor.RetrofitHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

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

    fun getJobcardDetails(barcodeSerial: String, handleResponse: (Array<JobcardDetail>) -> Unit, handleError: (Throwable) -> Unit) {
        RetrofitHelper.retrofit.create(ApiInterface::class.java)
                .getJobcardDetail(barcodeSerial)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResponse, handleError)
    }

    fun getProductionSchedulePartRelationDetails(id: Number, handleResponse: (Array<ProductionSchedulePartRelation>) -> Unit, handleError: (Throwable) -> Unit) {
        RetrofitHelper.retrofit.create(ApiInterface::class.java)
                .getProductionSchedulePartRelation(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResponse, handleError)
    }


    fun getMachineDetail(barcodeSerial: String, handleResponse: (Array<Machine>) -> Unit, handleError: (Throwable) -> Unit) {
        RetrofitHelper.retrofit.create(ApiInterface::class.java)
                .getMachine(barcodeSerial)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResponse, handleError)
    }


    fun updateMachineDetails(machineId:Number,partReplaced: String,remarks: String,machineStatus: String, employeeBarcode: String, costOfPartReplaced: Number, handleResponse: (Array<MaintenanceTransaction>) -> Unit, handleError: (Throwable) -> Unit) {
        var maintenanceTransactionRequest: MaintenanceTransactionRequest = MaintenanceTransactionRequest();
        maintenanceTransactionRequest.partReplaced = partReplaced
        maintenanceTransactionRequest.remarks = remarks
        maintenanceTransactionRequest.maintenanceStatus = machineStatus
        maintenanceTransactionRequest.machineId=machineId
        if (employeeBarcode.isNotEmpty()) {
            maintenanceTransactionRequest.employeeBarcode = employeeBarcode;
        }
        maintenanceTransactionRequest.costOfPartReplaced = costOfPartReplaced
        RetrofitHelper.retrofit.create(ApiInterface::class.java)
                .updateMachineDetails(maintenanceTransactionRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResponse, handleError)
    }


    fun startPartProcess(machineBarcode:String,jobBarcode: String,multiFaactor:Number,operatorId:Number, handleResponse: (JobProcessSequenceRelation) -> Unit, handleError: (Throwable) -> Unit) {
        var jobProcessSequenceRelation: JobProcessSequenceRelation = JobProcessSequenceRelation();
        jobProcessSequenceRelation.jobId=jobBarcode
        jobProcessSequenceRelation.operatorId=operatorId
        jobProcessSequenceRelation.machineId=machineBarcode
        RetrofitHelper.retrofit.create(ApiInterface::class.java)
                .postJobProcessSequenceRelation(jobProcessSequenceRelation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResponse, handleError)
    }

//    fun getUserDetail(username: String, handleResponse: (Array<User>) -> Unit, handleError: (Throwable) -> Unit) {
//        RetrofitHelper.retrofit.create(ApiInterface::class.java)
//                .getUser(username)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(handleResponse, handleError)
//    }


//    fun getMachineId(id: Number, handleResponse: (Array<Machine>) -> Unit, handleError: (Throwable) -> Unit) {
//        RetrofitHelper.retrofit.create(ApiInterface::class.java)
//                .getMachineId(id)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(handleResponse, handleError)
//    }
//
//    fun getJobcardId(id: Number, handleResponse: (Array<JobcardDetail>) -> Unit, handleError: (Throwable) -> Unit) {
//        RetrofitHelper.retrofit.create(ApiInterface::class.java)
//                .getJobcardId(id)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(handleResponse, handleError)
//    }

    fun getEmployeeDetail(employeeId: String, handleResponse: (Array<Employee>) -> Unit, handleError: (Throwable) -> Unit) {
    RetrofitHelper.retrofit.create(ApiInterface::class.java)
            .getEmployee(employeeId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(handleResponse, handleError)
}

    fun stopPartProcess(machineBarcode:String,jobBarcode: String,quantity:Number,status: String,note:String, handleResponse: (JobProcessSequenceRelation) -> Unit, handleError: (Throwable) -> Unit) {
        var jobProcessSequenceRelation: JobProcessSequenceRelation = JobProcessSequenceRelation();
//        jobProcessSequenceRelation.id=id
        jobProcessSequenceRelation.machineId=machineBarcode
        jobProcessSequenceRelation.jobId=jobBarcode
        jobProcessSequenceRelation.quantity=quantity
        jobProcessSequenceRelation.processStatus=status
        jobProcessSequenceRelation.note=note
        RetrofitHelper.retrofit.create(ApiInterface::class.java)
                .updateJobProcessSequenceRelation(jobProcessSequenceRelation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResponse, handleError)
    }

    fun getJobProcessSequenceRelation(id: Number, handleResponse: (Array<JobProcessSequenceRelation>) -> Unit, handleError: (Throwable) -> Unit) {
        RetrofitHelper.retrofit.create(ApiInterface::class.java)
                .getJobProcessSequenceRelation(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResponse, handleError)
    }

    fun getPendingJobLocationRelations(barcodeSerial: String, handleResponse: (Array<JobLocationRelationDetailed>) -> Unit, handleError: (Throwable) -> Unit) {
//        var whereStatement = "{" + "\"processStatus\"" + ":{" + "\"!=\"" + ":" + "\"Complete\"" + "}}"
        RetrofitHelper.retrofit.create(ApiInterface::class.java)
                .getJobLocationRelations(barcodeSerial)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResponse, handleError)
    }

    fun pickPendingItem(jobLocationRelationId: Number, status: String, handleResponse: (Array<JobLocationRelation>) -> Unit, handleError: (Throwable) -> Unit) {
        var jobLocationRelation = JobLocationRelationRequest()
        jobLocationRelation.jobLocationRelationId = jobLocationRelationId
        RetrofitHelper.retrofit.create(ApiInterface::class.java)
                .pickJobLocationRelation(jobLocationRelation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResponse, handleError)
    }

    fun dropLocationForPendingItem(jobLocationRelationId: Number, locationBarcodeSerial: String, handleResponse: (Array<JobLocationRelation>) -> Unit, handleError: (Throwable) -> Unit) {
        var locationUpdateRequest = JobLocationRelationRequest()
        locationUpdateRequest.jobLocationRelationId = jobLocationRelationId
        locationUpdateRequest.barcodeSerial = locationBarcodeSerial
        RetrofitHelper.retrofit.create(ApiInterface::class.java)
                .dropJobLocationRelation(locationUpdateRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResponse, handleError)
    }

    fun getRoleAccessRelation(handleResponse: (Array<RoleAccessRelation>) -> Unit, handleError: (Throwable) -> Unit) {
        RetrofitHelper.retrofit.create(ApiInterface::class.java)
                .getRoleAccessRelation(1000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResponse, handleError)
    }

    fun getAllProcessesForJobCard(barcodeSerial: String, handleResponse: (JobProcesses) -> Unit, handleError: (Throwable) -> Unit) {
        var request = AllJobcardProcessSequences()
        request.barcodeSerial = barcodeSerial
        RetrofitHelper.retrofit.create(ApiInterface::class.java)
                .getAllProcessJobCard(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResponse, handleError)
    }

    fun getJobCardByMachine(machineBarcodeSerial: String, handleResponse: (Array<String>) -> Unit, handleError: (Throwable) -> Unit) {
        var request = MachineBarcodeRequest()
        request.machineBarcodeSerial = machineBarcodeSerial
        RetrofitHelper.retrofit.create(ApiInterface::class.java)
                .getJobCardByMachine(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResponse, handleError)
    }

    fun get313Records(jobCard: String, handleResponse: (Array<Sap313Record>) -> Unit, handleError: (Throwable) -> Unit) {
        var request = Jobcard313Request()
        request.jobCard = jobCard
        RetrofitHelper.retrofit.create(ApiInterface::class.java)
                .get313Records(request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResponse, handleError)
    }

    fun postSAP315(record: Sap313Record, handleResponse: (Sap315Record) -> Unit, handleError: (Throwable) -> Unit) {
        RetrofitHelper.retrofit.create(ApiInterface::class.java)
                .postSAP315(record)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleResponse, handleError)
    }
}
