package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import android.util.Log
import com.briot.tmmlmss.implementor.repository.remote.PopulatedUser
import com.briot.tmmlmss.implementor.repository.remote.RemoteRepository
import com.briot.tmmlmss.implementor.repository.remote.SignInResponse
import com.briot.tmmlmss.implementor.repository.remote.User
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.net.SocketException
import java.net.SocketTimeoutException

class LoginViewModel : ViewModel() {

    val TAG = "LoginViewModel"

    val user: LiveData<PopulatedUser> = MutableLiveData<PopulatedUser>()
    var errorMessage: String = ""

    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidUser: PopulatedUser = PopulatedUser()

    fun loginUser(username: String, password: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.loginUser(username, password, this::handleLoginResponse, this::handleLoginError)
    }

    private fun handleLoginResponse(signInResponse: SignInResponse) {
        Log.d(TAG, "successful user" + user.toString())
        (this.user as MutableLiveData<PopulatedUser>).value = signInResponse.user
    }

    private fun handleLoginError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is HttpException) {
            if (error.code() >= 401) {
                var msg = (error as HttpException).response().errorBody()?.string()
                if (msg != null && msg.isNotEmpty()) {
                    errorMessage = msg;
                } else {
                    errorMessage = error.message()
                }
            }
            (networkError as MutableLiveData<Boolean>).value = true
        } else if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.user as MutableLiveData<PopulatedUser>).value = invalidUser
        }
    }
}
