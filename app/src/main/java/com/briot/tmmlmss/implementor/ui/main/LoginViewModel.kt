package com.briot.tmmlmss.implementor.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import android.util.Log
import com.briot.tmmlmss.implementor.repository.remote.RemoteRepository
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

    private fun handleLoginResponse(user: User) {
        Log.d(TAG, "successful user" + user.toString())
        (this.user as MutableLiveData<User>).value = user
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
