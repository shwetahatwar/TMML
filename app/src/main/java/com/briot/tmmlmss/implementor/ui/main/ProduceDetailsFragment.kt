package com.briot.tmmlmss.implementor.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.briot.tmmlmss.implementor.R
import com.briot.tmmlmss.implementor.repository.local.PrefConstants
import com.briot.tmmlmss.implementor.repository.local.PrefRepository
import kotlinx.android.synthetic.main.produce_details_fragment.*


class ProduceDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = ProduceDetailsFragment()
    }

    private lateinit var viewModel: ProduceDetailsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.produce_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ProduceDetailsViewModel::class.java)

        (this.activity as AppCompatActivity).setTitle("Asset Details")

        val prefConstants = PrefConstants()
        text_account.text = PrefRepository.singleInstance.getValueOrDefault(prefConstants.PRODUCT_ACCOUNTNAME, "NA")
        text_productName.text = PrefRepository.singleInstance.getValueOrDefault(prefConstants.PRODUCT_PRODUCTNAME, "NA")
        text_itemName.text = PrefRepository.singleInstance.getValueOrDefault(prefConstants.PRODUCT_ITEMNAME, "NA")
        text_quantity.text = PrefRepository.singleInstance.getValueOrDefault(prefConstants.PRODUCT_QUANTITY, "0")
        text_unit.text = PrefRepository.singleInstance.getValueOrDefault(prefConstants.PRODUCT_UNITNAME, "NA")
        text_productDetailsId.text = PrefRepository.singleInstance.getValueOrDefault(prefConstants.PRODUCT_DETAILS_ID, "NA")
        text_projectId.text = PrefRepository.singleInstance.getValueOrDefault(prefConstants.PROJECT_ID, "NA")
        text_projectName.text = PrefRepository.singleInstance.getValueOrDefault(prefConstants.PROJECT_NAME, "NA")
        text_productStockId.text = PrefRepository.singleInstance.getValueOrDefault(prefConstants.PRODUCT_STOCK_ID, "NA")
        text_itemId.text = PrefRepository.singleInstance.getValueOrDefault(prefConstants.PRODUCT_ITEM_ID, "NA")
    }

}
