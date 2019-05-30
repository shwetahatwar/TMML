package com.briot.tmmlmss.implementor.repository.remote

import android.os.Parcel
import android.os.Parcelable
import io.reactivex.Observable
import retrofit2.http.*
import java.util.*


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
    var id: Int = -1
    var token: String? = null
}

class JobcardDetail{
    var createdAt: Number? = null
    var updatedAt: Number? = null
    var id: Int = -1
    var requestedQuantity: Number? = null
    var actualQuantity: Number? = null
    var status: String? = null
    var estimatedDate:Number? = null
    var barcodeSerial: String? = null
    var productionSchedulePartRelationId: ProductionSchedulePartRelation? = null
    var trolleyId: Trolley? = null
    var createdBy: User? = null
    var updatedBy: User? = null

    var jobcardDetail: JobcardDetail? = null
//    var trolley: Trolley? = null
//    var productionSchedulePartRelation: ProductionSchedulePartRelation? = null
    var Machine: Machine? = null
    var partNumber: PartNumber? = null
    var processSequenceMaster: ProcessSequenceMaster? = null
    var costCenter: CostCenter? = null
    var cell: Cell? =null
    var maintenanceTransactionTable: MaintenanceTransactionTable?= null
    var materialType: MaterialType?=null
    var rawMaterial: RawMaterial?=null
    var machineGroup: MachineGroup?= null
    var machineType: MachineType?=null
    var processSequenceMachineRelation: ProcessSequenceMachineRelation?=null
    var trolleyType:TrolleyType?=null
    var location:Location?=null
    var productionSchedule:ProductionSchedule?=null
    var jobProcessSequenceRelation:JobProcessSequenceRelation?=null
    var jobToJobRerouting:JobToJobRerouting?=null

}

class  PartNumber {
    var createdOn: Number? = null
    var updatedOn: Number? = null
    var id: Int = -1
    var description: String? = null
    var manPower: Number? = null
    var smh: Number? = null
    var rawMaterialId: RawMaterial?=null
    var createdBy: User? = null
    var updatedBy: User? = null
}

class ProcessSequenceMaster{
    var id: Int = -1
    var partId: String? = null
    var sequenceNumber: Number? = null
    var loadingTime: Number? = null
    var processTime: Number? = null
    var unloadingTime: Number? = null
    var machineGroupId: MachineGroup?= null
    var isGroup: Boolean? = null
}

class Machine{
    var id: Int = -1
    var machineTypeId: MachineType?=null
    var machineGroupId: MachineGroup?= null
    var costCenterId: CostCenter? = null
    var capacity: Number? = null
    var cellId: Cell? =null
    var machineWeight: Number? = null
    var status: String? = null
    var createdBy: User? = null
    var createdOn: Number? = null
    var updatedBy : User? = null
    var updatedOn: Number? = null
    var frequenceyInDays: Number? = null
    var lastMaintenanceOn: Number? = null
    var lastMaintenanceBy: User? = null
    var barcodeSerial: String? = null
}

class CostCenter{
    var id: Int = -1
    var name: String? = null
    var createdBy: User? = null
    var createdOn: Number? = null
}

class Cell{
    var id: Int = -1
    var name: String? = null
    var createdBy: User? = null
    var createdOn: Number? = null
}

class MaintenanceTransactionTable{
    var id: Int = -1
    var machineId: Machine? = null
    var maintenanceOn: Number? = null
    var maintenanceBy: User? = null
    var remarks: String? = null
}

class MaterialType{
    var id: Int = -1
    var name: String? = null
}

class RawMaterial{
    var id: Int = -1
    var name: String? = null
    var description: String? = null
    var materialTypeId: MaterialType?=null
}

class MachineGroup
{
    var id: Int = -1
    var name: String? = null
    var createdBy: User? = null
    var createdOn: Number? = null
}

class MachineType{
    var id: Int = -1
    var name: String? = null
    var createdBy: User? = null
    var createdOn: Number? = null
}

class ProcessSequenceMachineRelation{
    var id: Int = -1
    var processSequenceId: ProcessSequenceMachineRelation?=null
    var machineId: Machine? = null
}

class Trolley{
    var id: Int = -1
    var capacity: String? = null
    var typeId : String? = null
    var materialTypeId: String? = null
    var barcodeSerial: String? = null
    var status: String? = null
    var createdBy: User? = null
    var createdOn : Number? = null
}

class TrolleyType{
    var id: Int = -1
    var name: String? = null
}

class Location{
    var id: Int = -1
    var name: String? = null
    var barcodeSerial: String? = null
    var createdBy: User? = null
    var createdOn: Number? = null
}

class ProductionSchedule{
    var id: Int = -1
    var createdOn: Number? = null
    var createdBy: User? = null
    var estimatedCompletionDate: Number? = null
    var actualCompletionDate: Number? = null
    var status: String? = null
}


class ProductionSchedulePartRelation{
    var id: Int = -1
    var scheduleId: ProductionSchedule?=null
    var partNumberId: PartNumber? = null
    var requestedQuantity: Number? = null
    var status: String? = null
    var createdOn: Number? = null
    var createdBy: User? = null
    var estimatedCompletionDate: Number? = null
}

class JobProcessSequenceRelation{
    var id: Int = -1
    var jobId: JobcardDetail? = null
    var processSequenceId : ProcessSequenceMaster? = null
    var machineId : Machine? = null
    var locationId: Location?=null
    var quantity: Number? = null
    var note: String? = null
    var status: String? = null
    var createdBy : User? = null
    var createdOn: Number? = null
}

class JobToJobRerouting{
    var id: Int = -1
    var fromJobId: JobcardDetail? = null
    var fromProcessSequenceId: ProcessSequenceMaster? = null
    var toJobId : JobcardDetail? = null
    var toProcessSequenceId: ProcessSequenceMaster? = null
    var quantity: Number? = null
}



interface ApiInterface {
    @POST("login")
    fun login(@Body signInRequest: SignInRequest): Observable<SignInResponse>

    @GET("jobcard")
    fun JobcardDetail(@Query("barcodeSerial") barcodeSerial: String): Observable<Array<JobcardDetail>>

    @GET("machine")
    fun Machine(@Query("barcodeSerial") barcodeSerial: String): Observable<Array<Machine>>

    @GET("trolley")
    fun trolley(@Query("barcodeSerial") barcode: String): Observable<Trolley>

    @GET("productionSchedule")
    fun productionSchedulePartRelation(@Query("barcodeSerial") barcode: String): Observable<ProductionSchedulePartRelation>

}