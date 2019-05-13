package com.briot.tmmlmss.implementor.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavInflater
import androidx.navigation.Navigation
import com.briot.tmmlmss.implementor.R
import com.briot.tmmlmss.implementor.repository.local.PrefConstants
import com.briot.tmmlmss.implementor.repository.local.PrefRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.android.synthetic.main.main_fragment.*
import java.util.concurrent.TimeUnit

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel

        context?.let { PrefRepository.singleInstance.deserializePrefs(it) }

        var userToken: String = PrefRepository.singleInstance.getValueOrDefault(PrefConstants().USER_TOKEN, "")
        if (userToken.isNotEmpty()) {
            Observable.timer(2000, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe( { Navigation.findNavController(img).navigate(R.id.homeFragment) } );
        } else {
            Observable.timer(2000, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe( { Navigation.findNavController(img).navigate(R.id.action_mainFragment_to_loginFragment) } );
        }


    }

}