package com.briot.tmmlmss.implementor.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.navigation.Navigation
import com.briot.tmmlmss.implementor.MainActivity
import com.briot.tmmlmss.implementor.R
import com.briot.tmmlmss.implementor.repository.local.PrefConstants
import com.briot.tmmlmss.implementor.repository.local.PrefRepository
import com.briot.tmmlmss.implementor.repository.remote.Asset
import com.briot.tmmlmss.implementor.repository.remote.Audit
import com.pascalwelsch.arrayadapter.ArrayAdapter
import io.github.pierry.progress.Progress
import kotlinx.android.synthetic.main.asset_details_scan_fragment.*
import kotlinx.android.synthetic.main.asset_item_list_row.*
import kotlinx.android.synthetic.main.asset_item_list_row.view.*


class AssetDetailsScanFragment : Fragment() {

    companion object {
        fun newInstance() = AssetDetailsScanFragment()
    }

    private lateinit var viewModel: AssetDetailsScanViewModel
    private var progress: Progress? = null
    private var oldAsset: Asset? = null
    private var assetItems: Int = 13

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.asset_details_scan_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AssetDetailsScanViewModel::class.java)

        (this.activity as AppCompatActivity).setTitle("Asset Details")

        assetItemsList.adapter = AssetItemsAdapter(this.context!!)
        assetItemsList.layoutManager = LinearLayoutManager(this.context)


        viewModel.asset.observe(this, Observer<Asset> {
            MainActivity.hideProgress(this.progress)
            this.progress = null

            if (it != null && it != oldAsset) {
                for(i in 0 until assetItems) {
                    (assetItemsList.adapter as AssetItemsAdapter).add(it)
                }
                assetItemsList.adapter.notifyDataSetChanged()
            }

            oldAsset = it
        })

        viewModel.networkError.observe(this, Observer<Boolean> {
            if (it == true) {
                MainActivity.hideProgress(this.progress)
                this.progress = null

                MainActivity.showAlert(this.activity as AppCompatActivity, "Server is not reachable, please check if your internet connection is working");
            }
        })

        viewAssetDetails.setOnClickListener {
            this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
            viewModel.loadAssetDetails(assetScanText.text.toString())
        }

        assetScanText.setOnEditorActionListener { _, i, keyEvent ->
            var handled = false
            if (i == EditorInfo.IME_ACTION_DONE || (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                this.progress = MainActivity.showProgressIndicator(this.activity as AppCompatActivity, "Please wait")
                viewModel.loadAssetDetails(assetScanText.text.toString())

                handled = true
            }
            handled
        }
    }

}

class AssetItemsAdapter(val context: Context) : ArrayAdapter<Asset, AssetItemsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val assetItemHeadingId: TextView
        val assetItemValueId: TextView

        init {
            assetItemHeadingId = itemView.assetItemHeadingId as TextView
            assetItemValueId = itemView.assetItemTextId as TextView
        }
    }

    override fun getItemId(item: Asset): Any {
        return item
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) as Asset

        when (position) {
            0 -> {
                holder.assetItemHeadingId.setText("Description")
                holder.assetItemValueId.setText(item.assetDescription)
            }
            1 -> {
                holder.assetItemHeadingId.setText("Purchase Order Date")
                holder.assetItemValueId.setText(item.poDate)
            }
            2 -> {
                holder.assetItemHeadingId.setText("Barcode")
                holder.assetItemValueId.setText(item.barcodeSerial)
            }
            3 -> {
                holder.assetItemHeadingId.setText("PO Number")
                holder.assetItemValueId.setText(item.poNumber)
            }
            4 -> {
                holder.assetItemHeadingId.setText("Cost of Asset")
                holder.assetItemValueId.setText(item.costOfAsset.toString())
            }
            5 -> {
                holder.assetItemHeadingId.setText("Current Book Value")
                holder.assetItemValueId.setText(item.calculatedBook.toString())
            }
            6 -> {
                holder.assetItemHeadingId.setText("OEM Serial Number")
                holder.assetItemValueId.setText(item.oemSerialNumber)
            }
            7 -> {
                holder.assetItemHeadingId.setText("Model")
                holder.assetItemValueId.setText(item.model)
            }
            8 -> {
                holder.assetItemHeadingId.setText("Manufacturer")
                holder.assetItemValueId.setText(item.manufacturer)
            }
            9 -> {
                holder.assetItemHeadingId.setText("Site")
                holder.assetItemValueId.setText(item.site)
            }
            10 -> {
                holder.assetItemHeadingId.setText("Location")
                holder.assetItemValueId.setText(item.location)
            }
            11 -> {
                holder.assetItemHeadingId.setText("Sub-Location")
                holder.assetItemValueId.setText(item.subLocation)
            }
            12 -> {
                holder.assetItemHeadingId.setText("State")
                holder.assetItemValueId.setText(item.state)
            }
            else -> {
                print("s does not encode x")
                holder.assetItemHeadingId.setText("")
                holder.assetItemValueId.setText("")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.asset_item_list_row, parent, false)
        return ViewHolder(view)
    }
}
