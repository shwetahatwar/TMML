package com.briot.tmmlmss.implementor

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import com.briot.tmmlmss.implementor.repository.local.PrefConstants
import com.briot.tmmlmss.implementor.repository.local.PrefRepository
import io.github.pierry.progress.Progress
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.Response
import com.google.gson.GsonBuilder
import com.google.gson.Gson




class ResponseHeaderAuthTokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())

        val localheaders = originalResponse.headers("x-jwt-token")

        val jwtTokenExists = localheaders?.isNotEmpty() ?: false

        if (jwtTokenExists) {
            val jwtToken = localheaders.get(0)
            PrefRepository.singleInstance.setKeyValue("x-jwt-token", jwtToken ?: "")
        }

        return originalResponse
    }

}

class RequestHeaderAuthTokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        val token: String = "JWT " + PrefRepository.singleInstance.getValueOrDefault(PrefConstants().USER_TOKEN, "")
        builder.addHeader("Authorization", token)

        return chain.proceed(builder.build())
    }

}


class RetrofitHelper {
    companion object {
        val BASE_URL = BuildConfig.HOSTNAME;


        private fun getOkHttpClient(): OkHttpClient {
            val okHttpClient: OkHttpClient.Builder = OkHttpClient().newBuilder()
//                    .connectTimeout((30).toLong(), TimeUnit.SECONDS)
                    .readTimeout((90).toLong(), TimeUnit.SECONDS)
                    .writeTimeout((60).toLong(), TimeUnit.SECONDS)

            okHttpClient.interceptors().add(RequestHeaderAuthTokenInterceptor())
            okHttpClient.interceptors().add(ResponseHeaderAuthTokenInterceptor())

            return okHttpClient.build()
        }

        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient())
                .build()
    }
}

class MainActivity : AppCompatActivity() {
    companion object {
        fun showAlert(activity: AppCompatActivity, message: String) {
            AlertDialog.Builder(activity).create().apply {
                setTitle("Alert")
                setMessage(message)
                setButton(AlertDialog.BUTTON_NEUTRAL, "OK", { dialog, _ -> dialog.dismiss() })
                show()
            }
        }

        fun showToast(activity: AppCompatActivity, message: String) {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }

        fun showProgressIndicator(context: Context, message: String): Progress {
            val progress = Progress(context)

            progress.setBackgroundColor(context.resources.getColor(android.R.color.holo_blue_dark))
                    .setMessage(message)
                    .setMessageColor(context.resources.getColor(android.R.color.white))
                    .setProgressColor(context.resources.getColor(android.R.color.white))
                    .show();

            return progress
        }

        fun hideProgress(progress: Progress?) {
            progress?.dismiss()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }

    override fun onSupportNavigateUp()
            = findNavController(findViewById(R.id.nav_host_fragment)).navigateUp()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return true;
    }

}
