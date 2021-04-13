package com.rivaldomathindas.sembakopedia.fragment


import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.rivaldomathindas.sembakopedia.R
import com.google.firebase.database.*
import com.rivaldomathindas.sembakopedia.adapter.ProductOrdersAdapter
import com.rivaldomathindas.sembakopedia.model.ProductOrder
import com.rivaldomathindas.sembakopedia.base.BaseFragment
import com.rivaldomathindas.sembakopedia.utils.K
import com.rivaldomathindas.sembakopedia.utils.RecyclerFormatter
import com.rivaldomathindas.sembakopedia.utils.hideView
import com.rivaldomathindas.sembakopedia.utils.showView
import kotlinx.android.synthetic.main.fragment_product_orders.*
import timber.log.Timber

class OrdersPartsFragment : BaseFragment() {
    private lateinit var productOrdersAdapter: ProductOrdersAdapter
    private lateinit var ordersQuery: Query

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        ordersQuery = getDatabaseReference().child(K.ORDERS).child(getUid())
        return inflater.inflate(R.layout.fragment_product_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)

        ordersQuery.addValueEventListener(carsValueListener)
        ordersQuery.addChildEventListener(carsChildListener)
    }

    private fun initViews(v: View) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(activity)
        rv.itemAnimator = DefaultItemAnimator()
        rv.addItemDecoration(RecyclerFormatter.SimpleDividerItemDecoration(requireActivity()))

        productOrdersAdapter = ProductOrdersAdapter(requireActivity())
        rv.adapter = productOrdersAdapter
        rv.showShimmerAdapter()
    }

    private val carsValueListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            noProduct()
        }

        override fun onDataChange(p0: DataSnapshot) {
            if (p0.exists()) {
                hasProduct()
            } else {
                noProduct()
            }
        }
    }

    private val carsChildListener= object : ChildEventListener {

        override fun onCancelled(p0: DatabaseError) {
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val order = p0.getValue(ProductOrder::class.java)
            productOrdersAdapter.addProductOrder(order!!)
        }

        override fun onChildRemoved(p0: DataSnapshot) {
        }
    }

    private fun hasProduct() {
        rv?.hideShimmerAdapter()
        empty?.hideView()
        rv?.showView()
    }

    private fun noProduct() {
        rv?.hideShimmerAdapter()
        rv?.hideView()
        empty?.showView()
    }


    override fun onDestroy() {
        super.onDestroy()
        ordersQuery.removeEventListener(carsValueListener)
        ordersQuery.removeEventListener(carsChildListener)
    }


}
