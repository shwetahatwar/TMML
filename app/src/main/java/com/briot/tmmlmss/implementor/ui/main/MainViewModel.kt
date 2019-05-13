package com.briot.tmmlmss.implementor.ui.main

import android.arch.lifecycle.ViewModel
import com.briot.tmmlmss.implementor.SQLConnection

class MainViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    var sqlConnection = SQLConnection()

    init {
        // Thread(Runnable { kotlin.run { sqlConnection.connect(); } }).start()
    }
}
