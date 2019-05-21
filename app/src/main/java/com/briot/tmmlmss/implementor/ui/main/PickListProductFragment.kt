package com.briot.tmmlmss.implementor.ui.main

import androidx.lifecycle.Observer
import androidx.appcompat.app.AppCompatActivity
import com.briot.tmmlmss.implementor.MainActivity
import com.briot.tmmlmss.implementor.repository.local.PrefConstants
import com.briot.tmmlmss.implementor.repository.local.PrefRepository
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.navigation.Navigation
import com.briot.tmmlmss.implementor.R
import com.briot.tmmlmss.implementor.repository.remote.PendingPicklist
import com.pascalwelsch.arrayadapter.ArrayAdapter
import io.github.pierry.progress.Progress
import kotlinx.android.synthetic.main.picklist_product_fragment.*
import kotlinx.android.synthetic.main.picklist_products_list_row.view.*


class PickListProductFragment: androidx.fragment.app.Fragment(){

    companion object {
        fun newInstance() = PickListProductFragment()
    }

    private lateinit var viewModel: PickListProductViewModel
    private var progress: Progress? = null
    private var oldProduct: List<PendingPicklist>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.picklist_product_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PickListProductViewModel::class.java)

        picklistProductScan.adapter = ProductBarocdeAdapter(this.context!!)
        picklistProductScan.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.context)

        viewModel.picklistProductScan(PrefRepository.singleInstance.getValueOrDefault(PrefConstants().PICKLISTID,""))

        viewModel.picklist.observe(this, Observer<List<PendingPicklist>>{
            if (it != null && it != oldProduct){
                for(i in 0 until it.size){
                    (picklistProductScan.adapter as ProductBarocdeAdapter).add(it[i])
                }

                (picklistProductScan.adapter as ProductBarocdeAdapter).notifyDataSetChanged()
            }
            oldProduct = it
        })

        picklistProductScanText.setOnEditorActionListener { textView, i, keyEvent ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_DONE || (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                //viewModel.checkpicklistproduct(picklistProductScanText.text.toString(),newPicklistID)
                viewModel.pickListProductSubmit(picklistProductScanText.text.toString(),PrefRepository.singleInstance.getValueOrDefault(PrefConstants().PICKLISTID,""))
                //this.progress = null
                handled = true
            }
            handled
        }

        completePicklistProduct.setOnClickListener {
            val keyboard = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            //keyboard.hideSoftInputFromWindow(activity!!.currentFocus.getWindowToken(), 0)

            //this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
            viewModel.completePicklistProduct(PrefRepository.singleInstance.getValueOrDefault(PrefConstants().PICKLISTID,""))
//            Navigation.findNavController(it).navigate(R.id.action_PickListProductFragment_to_pickListFragment3)
        }

        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null

                MainActivity.showAlert(this.activity as AppCompatActivity, "Server is not reachable, please check if your internet connection is working");
            }
        })

    }
}

class ProductBarocdeAdapter(val context: Context) : ArrayAdapter<PendingPicklist, ProductBarocdeAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val barcodeSerialIds: TextView

        init {
            barcodeSerialIds = itemView.barcodeSerialId as TextView
        }
    }

    override fun getItemId(product: PendingPicklist): Any {
        return product
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) as PendingPicklist
        holder.barcodeSerialIds.setText(item.Barcode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.picklist_products_list_row, parent, false)
        return ViewHolder(view)
    }
}