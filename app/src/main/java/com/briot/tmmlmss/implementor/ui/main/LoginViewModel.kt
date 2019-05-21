package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import android.util.Log
import com.briot.tmmlmss.implementor.repository.remote.RemoteRepository
import com.briot.tmmlmss.implementor.repository.remote.SignInResponse
import com.briot.tmmlmss.implementor.repository.remote.User
import java.net.SocketException
import java.net.SocketTimeoutException

class LoginViewModel : ViewModel() {

    val TAG = "LoginViewModel"

    val user: LiveData<User> = MutableLiveData<User>()

    val networkError: LiveData<Boolean> = MutableLiveData<Boolean>()
    val invalidUser: User = User()

    fun loginUser(username: String, password: String) {
        (networkError as MutableLiveData<Boolean>).value = false
        RemoteRepository.singleInstance.loginUser(username, password, this::handleLoginResponse, this::handleLoginError)
    }

    private fun handleLoginResponse(signInResponse: SignInResponse) {
        Log.d(TAG, "successful user" + user.toString())
        (this.user as MutableLiveData<User>).value = signInResponse.user
    }

    private fun handleLoginError(error: Throwable) {
        Log.d(TAG, error.localizedMessage)

        if (error is SocketException || error is SocketTimeoutException) {
            (networkError as MutableLiveData<Boolean>).value = true
        } else {
            (this.user as MutableLiveData<User>).value = invalidUser
        }
    }
}
