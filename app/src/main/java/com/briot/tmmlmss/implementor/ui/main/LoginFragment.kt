package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.Navigation
import com.briot.tmmlmss.implementor.MainActivity
import com.briot.tmmlmss.implementor.R
import com.briot.tmmlmss.implementor.repository.local.PrefConstants
import com.briot.tmmlmss.implementor.repository.local.PrefRepository
import com.briot.tmmlmss.implementor.repository.remote.User
import io.github.pierry.progress.Progress
import kotlinx.android.synthetic.main.login_fragment.*
import android.view.inputmethod.InputMethodManager
import com.briot.tmmlmss.implementor.repository.remote.PopulatedUser


class LoginFragment : androidx.fragment.app.Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel
    private var progress: Progress? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel

        username.requestFocus()

        viewModel.user.observe(this, Observer<PopulatedUser> {
            MainActivity.hideProgress(this.progress)
            this.progress = null

            if (it != null && it != viewModel.invalidUser && it.token != null) {
                this.activity?.invalidateOptionsMenu()
                PrefRepository.singleInstance.setKeyValue(PrefConstants().USERLOGGEDIN, username.text.toString())
                PrefRepository.singleInstance.setKeyValue(PrefConstants().USER_TOKEN, it.token!!)
                PrefRepository.singleInstance.setKeyValue(PrefConstants().USER_ID, it.id!!.toString())
                PrefRepository.singleInstance.setKeyValue(PrefConstants().USER_NAME, it.username!!)
                if (it.employee != null) {
                    if (it.employee?.name != null) {
                        PrefRepository.singleInstance.setKeyValue(PrefConstants().EMPLOYEE_NAME, it.employee!!.name!!)
                    }
                    if (it.employee?.email != null) {
                        PrefRepository.singleInstance.setKeyValue(PrefConstants().EMPLOYEE_EMAIL, it.employee!!.email!!)
                    }
                    if (it.employee?.mobileNumber != null) {
                        PrefRepository.singleInstance.setKeyValue(PrefConstants().EMPLOYEE_PHONE, it.employee!!.mobileNumber!!.toString())
                    }
                    if (it.employee?.status != null) {
                        PrefRepository.singleInstance.setKeyValue(PrefConstants().EMPLOYEE_STATUS, it.employee!!.status!!.toString())
                    }
                }
                if (it.role != null && it.role?.id != null && it.role?.roleName != null) {
                    PrefRepository.singleInstance.setKeyValue(PrefConstants().ROLE_NAME, it.role!!.roleName!!)
                    PrefRepository.singleInstance.setKeyValue(PrefConstants().ROLE_ID, it.role!!.id!!.toString())
                }
                Navigation.findNavController(login).navigate(R.id.action_loginFragment_to_homeFragment)
                this.context?.let { it1 -> PrefRepository.singleInstance.serializePrefs(it1) }
            } else {
                MainActivity.showToast(this.activity as AppCompatActivity, "An error has occurred, please try again.");
            }
        })

        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null

                var message: String = "Server is not reachable, please check if your network connection is working"
                if (viewModel.errorMessage != null && viewModel.errorMessage.isNotEmpty()) {
                    message = viewModel.errorMessage
                }

                MainActivity.showToast(this.activity as AppCompatActivity, message);
            }
        })


        login.setOnClickListener {
            val keyboard = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(activity?.currentFocus?.getWindowToken(), 0)

            this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
            viewModel.loginUser(username.text.toString(), password.text.toString())
        }

//        username.setOnEditorActionListener { _, i, keyEvent ->
//            var handled = false
//
//            if (i == EditorInfo.IME_ACTION_DONE || (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN)) {
////                password.requestFocus()
//            }
//            handled
//
//        }
//
//        password.setOnEditorActionListener { _, i, keyEvent ->
//            var handled = false
//
//            if (i == EditorInfo.IME_ACTION_DONE || (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN)) {
////                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
////                viewModel.loginUser(username.text.toString(), password.text.toString())
//            }
//            handled
//
//        }

    }

}
