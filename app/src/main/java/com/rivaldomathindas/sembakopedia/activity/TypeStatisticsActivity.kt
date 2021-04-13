package com.rivaldomathindas.sembakopedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.adapter.TypeAdapter
import com.rivaldomathindas.sembakopedia.callbacks.TypeCallback
import com.rivaldomathindas.sembakopedia.model.DetailProduct
import com.rivaldomathindas.sembakopedia.model.Product
import com.rivaldomathindas.sembakopedia.model.ProductCategory
import com.rivaldomathindas.sembakopedia.model.Type
import com.rivaldomathindas.sembakopedia.base.BaseActivity
import com.rivaldomathindas.sembakopedia.utils.AppUtils
import com.rivaldomathindas.sembakopedia.utils.K
import kotlinx.android.synthetic.main.activity_type_statistics.*

class TypeStatisticsActivity : BaseActivity(), TypeCallback {
    private lateinit var category: ProductCategory
    private lateinit var typeadapter: TypeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_type_statistics)

        setSupportActionBar(toolbarTypeStatistics)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.statistics)

        initViews()

        category = intent.getSerializableExtra(K.CATEGORY_PRODUCT) as ProductCategory
        loadTypes()
    }

    private fun initViews() {
        rvType.setHasFixedSize(true)
        rvType.layoutManager = LinearLayoutManager(this)
        rvType.itemAnimator = DefaultItemAnimator()
        (rvType.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false

        typeadapter = TypeAdapter(this)
        rvType.adapter = typeadapter
    }

    private fun loadTypes() {
        typeadapter.addType(category.type as ArrayList<Type>)
        getFirestore().collection(K.PRODUCTS)
            .orderBy(K.TIMESTAMP, Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                }

                if (querySnapshot == null || querySnapshot.isEmpty) {
                } else {

                    for (docChange in querySnapshot.documentChanges) {

                        when (docChange.type) {
                            DocumentChange.Type.ADDED -> {
                                val product = docChange.document.toObject(Product::class.java)
                                typeadapter.addProduct(product)
                            }

                            DocumentChange.Type.MODIFIED -> {
                                val product = docChange.document.toObject(Product::class.java)
                                typeadapter.updateProduct(product)
                            }

                            DocumentChange.Type.REMOVED -> {
                                val product = docChange.document.toObject(Product::class.java)
                                typeadapter.removeProduct(product)
                            }

                        }

                    }

                }
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.animateEnterLeft(this)
    }

    override fun onClick(v: View, detailProduct: DetailProduct) {
        val i = Intent(this, DetailStatisticsActivity::class.java)
        i.putExtra(K.TYPE_PRODUCT, detailProduct)
        startActivity(i)
        AppUtils.animateFadein(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}