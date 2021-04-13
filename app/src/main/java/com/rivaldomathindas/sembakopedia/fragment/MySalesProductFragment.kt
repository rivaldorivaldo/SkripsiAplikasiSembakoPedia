package com.rivaldomathindas.sembakopedia.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.DocumentChange
import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.activity.PartActivity
import com.rivaldomathindas.sembakopedia.adapter.ProductsAdapter
import com.rivaldomathindas.sembakopedia.callbacks.ProductCallback
import com.rivaldomathindas.sembakopedia.model.Product
import com.rivaldomathindas.sembakopedia.base.BaseFragment
import com.rivaldomathindas.sembakopedia.utils.*
import kotlinx.android.synthetic.main.fragment_my_products.view.*
import kotlinx.android.synthetic.main.fragment_product.*

class MySalesProductFragment : BaseFragment(), ProductCallback {
    private lateinit var productsAdapter: ProductsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_products, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)

        loadParts()
    }

    private fun loadParts() {
        getFirestore().collection(K.PRODUCTS)
            //.orderBy(K.TIMESTAMP, Query.Direction.DESCENDING)
            .whereEqualTo("sellerId", getUid())
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    noParts()
                }

                if (querySnapshot == null || querySnapshot.isEmpty) {
                    noParts()
                } else {
                    hasParts()

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

    private fun initViews(v: View) {
        rv.setHasFixedSize(true)
        rv.layoutManager = GridLayoutManager(requireActivity(), 2)
        rv.itemAnimator = DefaultItemAnimator()
        rv.addItemDecoration(RecyclerFormatter.GridItemDecoration(requireActivity(), 2, 10))

        productsAdapter = ProductsAdapter(this)
        rv.adapter = productsAdapter
        rv.showShimmerAdapter()

        Handler().postDelayed({
            v.rv.hideShimmerAdapter()
        }, 2500)
    }

    private fun hasParts() {
        rv?.hideShimmerAdapter()
        empty?.hideView()
        rv?.showView()
    }

    private fun noParts() {
        rv?.hideShimmerAdapter()
        rv?.hideView()
        empty?.showView()
    }

    override fun onClick(v: View, product: Product) {
        val i = Intent(activity, PartActivity::class.java)
        i.putExtra(K.MINE, (product.sellerId == getUid()))
        i.putExtra(K.product, product)
        startActivity(i)
        AppUtils.animateFadein(requireActivity())
    }
}