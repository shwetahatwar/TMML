package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import android.util.Log
import com.briot.tmmlmss.implementor.repository.remote.Asset
import com.briot.tmmlmss.implementor.repository.remote.RemoteRepository
import java.net.SocketException
import java.net.SocketTimeoutException

class AssetDetailsScanViewModel : ViewModel() {
    val TAG = "AssetScanViewModel"

    val asset: LiveData<Asset> = MutableLiveData<Asset>()

    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidAsset: Asset = Asset()

    fun loadAssetDetails(barcode: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.getAssetDetails(barcode, this::handleAssetResponse, this::handleAssetError)
    }

    private fun handleAssetResponse(asset: Asset) {
        Log.d(TAG, "successful asset" + asset.toString())
        (this.asset as MutableLiveData<Asset>).value = asset
    }

    private fun handleAssetError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.asset as MutableLiveData<Asset>).value = invalidAsset
        }
    }
}
