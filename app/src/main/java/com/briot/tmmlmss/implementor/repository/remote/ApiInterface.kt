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
    var productionSchedulePartRelationId: Int = -1
    var trolleyId: trolley? = null
    var createdBy: User? = null
    var updatedBy: User? = null

    var JobcardDetail: JobcardDetail? = null
//    var trolley: trolley? = null
//    var productionSchedulePartRelation: productionSchedulePartRelation? = null
    var Machine: Machine? = null
    var PartNumber: PartNumber? = null
    var ProcessSequenceMaster: ProcessSequenceMaster? = null
    var CostCenter: CostCenter? = null
    var Cell: Cell? =null
    var MaintenanceTransactionTable: MaintenanceTransactionTable?= null
    var MaterialType: MaterialType?=null
    var RawMaterial: RawMaterial?=null
    var MachineGroup: MachineGroup?= null
    var MachineType: MachineType?=null
    var ProcessSequenceMachineRelation: ProcessSequenceMachineRelation?=null
    var trolleyType:trolleyType?=null
    var Location:Location?=null
    var ProductionSchedule:ProductionSchedule?=null
    var JobProcessSequenceRelation:JobProcessSequenceRelation?=null
    var JobToJobRerouting:JobToJobRerouting?=null

}

class  PartNumber {
    var createdOn: String? = null
    var updatedOn: String? = null
    var id: String? = null
    var description: String? = null
    var manPower: String? = null
    var SMH: String? = null
    var rawMaterialId: String? = null
    var createdBy: String? = null
    var updatedBy: String? = null
}

class ProcessSequenceMaster{
    var id: String? = null
    var partId: String? = null
    var sequenceNumber: String? = null
    var loadingTime: String? = null
    var processTime: String? = null
    var varunloadingTime: String? = null
    var machineGroupId: String? = null
    var isGroup: String? = null
}

class Machine{
    var id: String? = null
    var machineTypeId: String? = null
    var machineGroupId: String? = null
    var costCenterId: String? = null
    var capacity: String? = null
    var cellId: String? = null
    var machineWeight: String? = null
    var status: String? = null
    var createdBy: String? = null
    var createdOn: String? = null
    var updatedBy : String? = null
    var updatedOn: String? = null
    var frequenceyInDays: String? = null
    var lastMaintenanceOn: String? = null
    var lastMaintenanceBy: String? = null
}

class CostCenter{
    var id: String? = null
    var name: String? = null
    var createdBy: String? = null
    var createdOn: String? = null
}

class Cell{
    var id: String? = null
    var name: String? = null
    var createdBy: String? = null
    var createdOn: String? = null
}

class MaintenanceTransactionTable{
    var id: String? = null
    var machineId: String? = null
    var maintenanceOn: String? = null
    var maintenanceBy: String? = null
    var remarks: String? = null
}

class MaterialType{
    var id: String? = null
    var name: String? = null
}

class RawMaterial{
    var id: String? = null
    var name: String? = null
    var description: String? = null
    var material_type_Id: String? = null
}

class MachineGroup
{
    var id: String? = null
    var name: String? = null
    var createdBy: String? = null
    var createdOn: String? = null
}

class MachineType{
    var id: String? = null
    var name: String? = null
    var createdBy: String? = null
    var createdOn: String? = null
}

class ProcessSequenceMachineRelation{
    var id: String? = null
    var processSequenceId: String? = null
    var machineId: String? = null
}

class trolley{
    var id: String? = null
    var capacity: String? = null
    var typeId : String? = null
    var materialTypeId: String? = null
    var barcodeSerial: String? = null
    var status: String? = null
    var createdBy: String? = null
    var createdOn : String? = null
}

class trolleyType{
    var id: String? = null
    var name: String? = null
}

class Location{
    var id: String? = null
    var name: String? = null
    var barcodeSerial: String? = null
    var createdBy: String? = null
    var createdOn: String? = null
}

class ProductionSchedule{
    var id: String? = null
    var createdOn: String? = null
    var createdBy: String? = null
    var estimatedCompletionDate: String? = null
    var actualCompletionDate: String? = null
    var status: String? = null
}


class productionSchedulePartRelation{
    var id: String? = null
    var scheduleId: String? = null
    var partNumberId: String? = null
    var requestedquantity: String? = null
    var status: String? = null
    var createdOn: String? = null
    var createdBy: String? = null
    var estimatedCompletionDate: String? = null
}

class JobProcessSequenceRelation{
    var id: String? = null
    var jobId: String? = null
    var processSequenceId : String? = null
    var machineId : String? = null
    var locationId: String? = null
    var quantity: String? = null
    var note: String? = null
    var status: String? = null
    var createdBy : String? = null
    var createdOn: String? = null
}

class JobToJobRerouting{
    var id: String? = null
    var fromJobId: String? = null
    var fromProcessSequenceId: String? = null
    var toJobId : String? = null
    var toProcessSequenceId: String? = null
    var quantity: String? = null
}



interface ApiInterface {
    @POST("login")
    fun login(@Body signInRequest: SignInRequest): Observable<SignInResponse>

    @GET("jobcard")
    fun JobcardDetail(@Query("barcodeSerial") barcodeSerial: String): Observable<Array<JobcardDetail>>

    @GET("trolley")
    fun trolley(@Query("barcode") barcode: String): Observable<trolley>

    @GET("productionSchedule")
    fun productionSchedulePartRelation(@Query("barcode") barcode: String): Observable<productionSchedulePartRelation>

}