package com.briot.tmmlmss.implementor.repository.remote

import io.reactivex.Observable
import retrofit2.http.*


class SignInRequest {
    var username: String? = null
    var password: String? = null
}

class SignInResponse {
    var message: String? = null
    var user: User? = null
}

class User {
    var username: String? = null
    var id: Number? = null
    var token: String? = null
}

class JobcardDetail {
    var id: Number? = null
    var createdAt: Number? = null
    var updatedAt: Number? = null
    var requestedQuantity: Number? = null
    var actualQuantity: Number? = null
    var status: String? = null
    var estimatedDate: Number? = null
    var barcodeSerial: String? = null
    var productionSchedulePartRelationNestedId: ProductionSchedulePartRelationNested? = null
    var trolleyId: Trolley? = null
    var createdBy: User? = null
    var updatedBy: User? = null
}

class PartNumber {
    var createdOn: Number? = null
    var updatedOn: Number? = null
    var id: Number? = null
    var description: String? = null
    var manPower: Number? = null
    var smh: Number? = null
    var rawMaterialId: RawMaterial? = null
    var createdBy: User? = null
    var updatedBy: User? = null
}

class ProcessSequence {
    var id: Number? = null
    var partId: String? = null
    var sequenceNumber: Number? = null
    var loadingTime: Number? = null
    var processTime: Number? = null
    var unloadingTime: Number? = null
    var machineGroupId: MachineGroup? = null
    var isGroup: Boolean? = null
    var cycleTime: Number? = null
}

class Machine {
    var id: Number? = null
    var machineTypeId: MachineType? = null
    var machineGroupId: MachineGroup? = null
    var costCenterId: CostCenter? = null
    var capacity: Number? = null
    var cellId: Cell? = null
    var machineWeight: Number? = null
    var status: String? = null
    var createdBy: User? = null
    var createdOn: Number? = null
    var updatedBy: User? = null
    var updatedOn: Number? = null
    var frequenceyInDays: Number? = null
    var lastMaintenanceOn: Number? = null
    var lastMaintenanceBy: User? = null
    var nextMaintenanceOn: Number? = null
    var barcodeSerial: String? = null
    var machineName: String? = null
    var maintenanceBy: User? = null
}

class CostCenter {
    var id: Number? = null
    var name: String? = null
    var createdBy: User? = null
    var updatedBy: User? = null
    var createdAt: Number? = null
    var updatedAt: Number? = null
    var status: String? = null
}

class Cell {
    var id: Number? = null
    var name: String? = null
    var createdBy: User? = null
    var updatedBy: User? = null
    var createdAt: Number? = null
    var updatedAt: Number? = null
    var status: String? = null
}

class MaintenanceTransactionRequest {
    var createdAt: Number? = null
    var updatedAt: Number? = null
    var id: Number? = null
    var maintenanceOn: Number? = null
    var remarks: String? = null
    var partReplaced: String? = null
    var status: String? = null
    var machineId: Number? = null
    var maintenanceBy: User? = null
}

class MaintenanceTransaction {
    var createdAt: Number? = null
    var updatedAt: Number? = null
    var id: Number? = null
    var maintenanceOn: Number? = null
    var remarks: String? = null
    var partReplaced: String? = null
    var machineStatus: String? = null
    var machineId: Number? = null
    var maintenanceBy: User? = null
}

class MaterialType {
    var id: Number? = null
    var name: String? = null
}

class RawMaterial {
    var id: Number? = null
    var name: String? = null
    var description: String? = null
    var materialTypeId: MaterialType? = null
}

class MachineGroup {
    var id: Number? = null
    var name: String? = null
    var createdBy: User? = null
    var updatedBy: User? = null
    var createdAt: Number? = null
    var updatedAt: Number? = null
    var status: String? = null
}

class MachineType {
    var id: Number? = null
    var name: String? = null
    var createdBy: User? = null
    var updatedBy: User? = null
    var createdAt: Number? = null
    var updatedAt: Number? = null
    var status: String? = null
}

class ProcessSequenceMachineRelation {
    var id: Number? = null
    var processSequenceId: ProcessSequenceMachineRelation? = null
    var machineId: Machine? = null
}

class Trolley {
    var id: Number? = null
    var capacity: String? = null
    var typeId: String? = null
    var materialTypeId: String? = null
    var barcodeSerial: String? = null
    var status: String? = null
    var createdBy: User? = null
    var createdOn: Number? = null
}

class TrolleyType {
    var id: Number? = null
    var name: String? = null
}

class Location {
    var id: Number? = null
    var name: String? = null
    var barcodeSerial: String? = null
    var createdBy: User? = null
    var createdOn: Number? = null
}

class ProductionSchedule {
    var id: Number? = null
    var createdOn: Number? = null
    var createdBy: User? = null
    var estimatedCompletionDate: Number? = null
    var actualCompletionDate: Number? = null
    var status: String? = null
}


class ProductionSchedulePartRelationNested {
    var id: Number? = null
    var scheduleId: Number? = null
    var partNumberId: Number? = null
    var requestedQuantity: Number? = null
    var status: String? = null
    var createdAt: Number? = null
    var createdBy: Number? = null
    var estimatedCompletionDate: Number? = null
}

class ProductionSchedulePartRelation {
    var id: Number? = null
    var scheduleId: ProductionSchedule? = null
    var partNumberId: PartNumber? = null
    var requestedQuantity: Number? = null
    var status: String? = null
    var createdAt: Number? = null
    var createdBy: User? = null
    var estimatedCompletionDate: Number? = null
}

class JobProcessSequenceRelation {
    var id: Number? = null
    var jobId: String? = null
    var processSequenceId: Number? = null                            //ProcessSequenceMaster? = null
    var machineId: String? = null                                    //Machine? = null
    var locationId: Number? = null                                     //Location?=null
    var quantity: Number? = null
    var note: String? = null
    var status: String? = null
    var createdBy: User? = null
    var createdAt: Number? = null
    var updatedAt: Number? = null
    var startTime: Number? = null
    var endTime: Number? = null
    var duration: Number? = null
    var operatorId: Number? = null
}

class JobToJobRerouting {
    var id: Number? = null
    var fromJobId: JobcardDetail? = null
    var fromProcessSequenceId: ProcessSequence? = null
    var toJobId: JobcardDetail? = null
    var toProcessSequenceId: ProcessSequence? = null
    var quantity: Number? = null
}

class  Department {
    var name:String? = null
    var status:Number? = null
    var createdBy:User? = null
    var updatedBy:User? = null
}

class Employee{
    var createdAt:Number? = null
    var updatedAt:Number? = null
    var id: Number? = null
    var employeeId:String? = null
    var name:String? = null
    var email:String? = null
    var mobileNumber:Number? = null
    var status:Number? = null
    var notifyForMachineMaintenance:Number = 0
    var createdBy:User? = null
    var updatedBy:User? = null
    var department:Department? = null
}


interface ApiInterface {
    @POST("login")
    fun login(@Body signInRequest: SignInRequest): Observable<SignInResponse>

    @GET("jobcard")
    fun getJobcardDetail(@Query("barcodeSerial") barcodeSerial: String): Observable<Array<JobcardDetail>>

    @GET("machine")
    fun getMachine(@Query("barcodeSerial") barcodeSerial: String): Observable<Array<Machine>>

    @GET("trolley")
    fun trolley(@Query("barcodeSerial") barcodeSerial: String): Observable<Trolley>

    @GET("productionSchedule")
    fun productionSchedulePartRelation(@Query("barcodeSerial") barcodeSerial: String): Observable<ProductionSchedulePartRelation>

    @PUT("maintenancetransaction/update")
    fun updateMachineDetails(@Body maintenanceTransaction: MaintenanceTransactionRequest): Observable<Array<MaintenanceTransaction>>

    @POST("jobProcessSequenceRelation/create")
    fun postJobProcessSequenceRelation(@Body jobProcessSequenceRelation: JobProcessSequenceRelation): Observable<JobProcessSequenceRelation>

//    @GET("user")
//    fun getUser(@Query("username") username: String): Observable<Array<User>>

//    @GET("machine")
//    fun getMachineId(@Query("id") id: Number): Observable<Array<Machine>>
//
//    @GET("jobcard")
//    fun getJobcardId(@Query("id") id: Number): Observable<Array<JobcardDetail>>

    @GET("employee")
    fun getEmployee(@Query("employeeId") employeeId: String): Observable<Array<Employee>>

    @PUT("jobProcessSequenceRelation/update")
    fun updateJobProcessSequenceRelation(@Body jobProcessSequenceRelation: JobProcessSequenceRelation): Observable<JobProcessSequenceRelation>

    @GET("processSequence")
    fun getProcessSequence(@Query("id") id: Number): Observable<Array<ProcessSequence>>

}
