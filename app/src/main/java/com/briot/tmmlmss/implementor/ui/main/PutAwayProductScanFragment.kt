package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.briot.tmmlmss.implementor.R
import kotlinx.android.synthetic.main.put_away_product_scan_fragment.*
import android.widget.TextView
import com.briot.tmmlmss.implementor.MainActivity
import com.briot.tmmlmss.implementor.repository.local.PrefConstants
import com.briot.tmmlmss.implementor.repository.local.PrefRepository
import com.briot.tmmlmss.implementor.repository.remote.Product
import com.pascalwelsch.arrayadapter.ArrayAdapter
import io.github.pierry.progress.Progress
import kotlinx.android.synthetic.main.barcode_list_row.view.*


class PutAwayProductScanFragment : androidx.fragment.app.Fragment() {

    companion object {
        fun newInstance() = PutAwayProductScanFragment()
    }

    private lateinit var viewModel: PutAwayProductScanViewModel
    private var progress: Progress? = null
    private var oldResponse: Product? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.put_away_product_scan_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PutAwayProductScanViewModel::class.java)
        val locationbarcode: String = PrefRepository.singleInstance.getValueOrDefault(PrefConstants().PUTAWAYLOCATION, "")

        (this.activity as AppCompatActivity).setTitle("Audit")
        putAwayLocationScanText.text = locationbarcode

        productsList.adapter = BarcodeAdapter(this.context!!)
        productsList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)

//        Observable.create(ObservableOnSubscribe<String> { subscriber ->
//            putAwayProductScanText.addTextChangedListener(object : TextWatcher {
//                override fun afterTextChanged(s: Editable?) = Unit
//
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
//
//                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
//                        = subscriber.onNext(s.toString())
//            })
//        }).debounce(1000, TimeUnit.MILLISECONDS)
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe({ text: String ->
//            if(text.isNotEmpty()) {
//                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
//                viewModel.putAwaySubmit(text)
//            }
//        })

        putAwayProductScanText.setOnEditorActionListener { textView, i, keyEvent ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_DONE || (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.putAwaySubmit(putAwayProductScanText.text.toString())

                handled = true
            }
            handled
        }


        viewModel.product.observe(this, Observer<Product> {
            MainActivity.hideProgress(this.progress)
            this.progress = null

            if (it != null && it != oldResponse) {
                if (it != viewModel.invalidProduct) {
                    MainActivity.showToast(this.activity as AppCompatActivity, "Product has been put away");

                    (productsList.adapter as BarcodeAdapter).add(it)
                    (productsList.adapter as BarcodeAdapter).notifyDataSetChanged()

                    putAwayProductScanText.text?.clear()
                } else {
                    MainActivity.showAlert(this.activity as AppCompatActivity, "Product not found");
                }
            }

            oldResponse = it
        })

        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null

                MainActivity.showAlert(this.activity as AppCompatActivity, "Server is not reachable, please check if your internet connection is working");
            }
        })
    }

}

class BarcodeAdapter(val context: Context) : ArrayAdapter<Product, BarcodeAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val serialNumberView: TextView
        val locationView: TextView
        val itemNameView: TextView

        init {
            serialNumberView = itemView.barcodeId as TextView
            locationView = itemView.rackId as TextView
            itemNameView = itemView.listname as TextView
        }
    }

    override fun getItemId(product: Product): Any {
        return product
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) as Product
        holder.serialNumberView.setText(item.BarcodeSerialNumber!!)
        holder.locationView.setText(item.RackId!!)
        holder.itemNameView.setText(item.ItemName!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.barcode_list_row, parent, false)
        return ViewHolder(view)
    }
}

