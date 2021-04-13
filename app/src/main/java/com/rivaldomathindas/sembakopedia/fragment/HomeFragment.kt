package com.rivaldomathindas.sembakopedia.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.activity.PartActivity
import com.rivaldomathindas.sembakopedia.adapter.ProductsAdapter
import com.rivaldomathindas.sembakopedia.callbacks.ProductCallback
import com.rivaldomathindas.sembakopedia.model.Product
import com.rivaldomathindas.sembakopedia.base.BaseFragment
import com.rivaldomathindas.sembakopedia.utils.*
import kotlinx.android.synthetic.main.fragment_product.*

class HomeFragment : BaseFragment(), ProductCallback {
    private lateinit var productsAdapter: ProductsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        loadParts()
    }

    private fun initViews() {
        rv.setHasFixedSize(true)
        rv.layoutManager = GridLayoutManager(requireActivity(), 2)
        rv.addItemDecoration(RecyclerFormatter.GridItemDecoration(requireActivity(), 2, 10))
        rv.itemAnimator = DefaultItemAnimator()
        (rv.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false

        productsAdapter = ProductsAdapter(this)
        rv.adapter = productsAdapter

    }

    private fun loadParts() {
        getFirestore().collection(K.PRODUCTS)
            .orderBy(K.TIMESTAMP, Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    noProducts()
                }

                if (querySnapshot == null || querySnapshot.isEmpty) {
                    noProducts()
                } else {
                    hasProducts()

                    for (docChange in querySnapshot.documentChanges) {

                        when(docChange.type) {
                            DocumentChange.Type.ADDED -> {
                                val product = docChange.document.toObject(Product::class.java)
                                productsAdapter.addProduct(product)
                            }

                            DocumentChange.Type.MODIFIED -> {
                                val product = docChange.document.toObject(Product::class.java)
                                productsAdapter.updateProduct(product)
                            }

                            DocumentChange.Type.REMOVED -> {
                                val product = docChange.document.toObject(Product::class.java)
                                productsAdapter.removeProduct(product)
                            }

                        }

                    }

                }
            }
    }

    private fun hasProducts() {
        rv?.hideShimmerAdapter()
        empty?.hideView()
        rv?.showView()
    }

    private fun noProducts() {
        rv?.hideShimmerAdapter()
        rv?.hideView()
        empty?.showView()
    }

    override fun onClick(v: View, product: Product) {
        val i = Intent(activity, PartActivity::class.java)
        i.putExtra(K.product, product)
        i.putExtra(K.MINE, (product.sellerId == getUid()))
        startActivity(i)
        AppUtils.animateFadein(requireActivity())

    }
}