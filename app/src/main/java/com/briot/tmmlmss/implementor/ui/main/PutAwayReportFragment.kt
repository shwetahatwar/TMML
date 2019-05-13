package com.briot.tmmlmss.implementor.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.briot.tmmlmss.implementor.MainActivity

import com.briot.tmmlmss.implementor.R
import com.briot.tmmlmss.implementor.repository.remote.Product
import io.github.pierry.progress.Progress
import kotlinx.android.synthetic.main.put_away_report_fragment.*
import com.pascalwelsch.arrayadapter.ArrayAdapter
import kotlinx.android.synthetic.main.product_report_list_row.view.*

class PutAwayReportFragment : Fragment() {

    companion object {
        fun newInstance() = PutAwayReportFragment()
    }

    private lateinit var viewModel: PutAwayReportViewModel
    private var progress: Progress? = null
    private var oldResponse: List<Product>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.put_away_report_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PutAwayReportViewModel::class.java)
        // TODO: Use the ViewModel

        (this.activity as AppCompatActivity).setTitle("Audit")

        productsReport.adapter = ProductReportAdapter(this.context!!)
        productsReport.layoutManager = LinearLayoutManager(this.context)

        viewModel.products.observe(this, Observer<List<Product>> {
            MainActivity.hideProgress(this.progress)
            this.progress = null

            if (it != null && it != oldResponse) {
                if (it != viewModel.invalidProductList) {
                    MainActivity.showToast(this.activity as AppCompatActivity, "Products have been fetched");

                    (productsReport.adapter as ProductReportAdapter).clear()
                    (productsReport.adapter as ProductReportAdapter).addAll(it)
                    productsReport.adapter.notifyDataSetChanged()
                } else {
                    MainActivity.showAlert(this.activity as AppCompatActivity, "Audit report could not be created");
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

class ProductReportAdapter(val context: Context) : ArrayAdapter<Product, ProductReportAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val serialNumberView: TextView
        val locationView: TextView
        val itemNameView: TextView

        init {
            serialNumberView = itemView.report_barcodeId as TextView
            locationView = itemView.report_rackId as TextView
            itemNameView = itemView.report_listname as TextView
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
                .inflate(R.layout.product_report_list_row, parent, false)
        return ViewHolder(view)
    }
}
