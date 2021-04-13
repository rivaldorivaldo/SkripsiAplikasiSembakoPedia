package com.rivaldomathindas.sembakopedia.activity

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.ionicons_typeface_library.Ionicons
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.filter.Filter
import kotlinx.android.synthetic.main.activity_add_product.*
import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.adapter.ImagesAdapter
import com.rivaldomathindas.sembakopedia.model.Product
import com.rivaldomathindas.sembakopedia.base.BaseActivity
import com.rivaldomathindas.sembakopedia.utils.*
import com.rivaldomathindas.sembakopedia.utils.PreferenceHelper.get
import org.jetbrains.anko.toast

class AddPartActivity : BaseActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var imagesAdapter: ImagesAdapter
    private lateinit var KEY: String
    private val images = mutableMapOf<String, String>()
    private var pickedImages = mutableListOf<Uri>()
    private val product = Product()

    private lateinit var makes: Array<String>
    private lateinit var beras: Array<String>
    private lateinit var gula: Array<String>
    private lateinit var minyakGoreng: Array<String>
    private lateinit var daging: Array<String>
    private lateinit var susu: Array<String>
    private lateinit var minyakLPG: Array<String>
    private lateinit var hasilTani: Array<String>
    private lateinit var telur: Array<String>
    private lateinit var garam: Array<String>
    private lateinit var lainnya: Array<String>


    companion object {
        private const val IMAGE_PICKER = 401
        private const val BERAS = "Beras"
        private const val GULA = "Gula"
        private const val MINYAK_GORENG = "Minyak Goreng"
        private const val DAGING = "Daging"
        private const val MINYAK_LPG = "Minyak Tanah dan Gas LPG"
        private const val SUSU = "Susu"
        private const val HASIL_TANI = "Hasil tani"
        private const val TELUR = "Telur"
        private const val GARAM = "Garam"
        private const val LAINNYA = "Lainnya"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        prefs = PreferenceHelper.defaultPrefs(this)

        initViews()
    }

    private fun initViews() {
        setSupportActionBar(toolbar3)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.upload_products)

        photosRv.setHasFixedSize(true)
        photosRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imagesAdapter = ImagesAdapter()
        photosRv.adapter = imagesAdapter

        addPhoto.setDrawable(AppUtils.setDrawable(this, Ionicons.Icon.ion_android_camera, R.color.colorPrimary, 15))
        addPhoto.setOnClickListener { pickPhotos() }

        initArrays()
        post.setOnClickListener { postProduct() }
    }

    //Spinner jenis sembako
    private fun initArrays() {
        makes = resources.getStringArray(R.array.makes)
        beras = resources.getStringArray(R.array.beras)
        gula = resources.getStringArray(R.array.gula)
        minyakGoreng = resources.getStringArray(R.array.minyak_goreng)
        minyakLPG = resources.getStringArray(R.array.minyak_lpg)
        daging = resources.getStringArray(R.array.daging)
        susu = resources.getStringArray(R.array.susu)
        hasilTani = resources.getStringArray(R.array.hasil_tani)
        telur = resources.getStringArray(R.array.telur)
        garam = resources.getStringArray(R.array.garam)
        lainnya = resources.getStringArray(R.array.lainnya)

        category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()

                setModels(selectedItem)
            }
        }
    }

    //Spinner tipe dari jenis sembako
    private fun setModels(make: String) {
        when(make) {
            BERAS -> setModelSpinner(beras)
            GULA -> setModelSpinner(gula)
            MINYAK_GORENG -> setModelSpinner(minyakGoreng)
            MINYAK_LPG -> setModelSpinner(minyakLPG)
            DAGING -> setModelSpinner(daging)
            SUSU -> setModelSpinner(susu)
            HASIL_TANI -> setModelSpinner(hasilTani)
            TELUR -> setModelSpinner(telur)
            GARAM -> setModelSpinner(garam)
            LAINNYA -> setModelSpinner(lainnya)
        }
    }

    //Model spinner
    private fun setModelSpinner(models: Array<String>) {
        val spinnerArrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, models)
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item )

        type.adapter = spinnerArrayAdapter
    }

    //Memilih foto dari gallery
    private fun pickPhotos() {
        if (!storagePermissionGranted()) {
            requestStoragePermission()
            return
        }

        Matisse.from(this)
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(10)
                .addFilter(GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(MyGlideEngine())
                .forResult(IMAGE_PICKER)
    }

    //Mengambil foto
    private fun setImages() {
        if (pickedImages.size < 1) return

        if (pickedImages.size == 1) {
            mainImage.setImageURI(pickedImages[0])
        } else {
            mainImage.setImageURI(pickedImages[0])
            photosRv.showView()

            for (i in 1..(pickedImages.size-1)) {
                imagesAdapter.addImage(pickedImages[i])
            }

        }

    }

    //Mengunggah produk
    private fun postProduct() {
        if (!isConnected()) {
            toast(getString(R.string.please_connect_to_the_internet))
            return
        }

        if (pickedImages.size < 2) {
            toast(getString(R.string.please_select_atleast_2_images))
            return
        }

        if(!AppUtils.validated(product_name, location, price, desc, quantity)) {
            toast(getString(R.string.please_fill_all_fields))
            return
        }

        KEY = getFirestore().collection(K.PRODUCTS).document().id

        showLoading(getString(R.string.uploading_images))
        uploadImages()
    }

    //Upload foto ke firebase
    private fun uploadImages() {
        for (i in 0..(pickedImages.size-1)) {
            val ref = getStorageReference().child(K.PRODUCTS).child(KEY).child(i.toString())

            val uploadTask = ref.putFile(pickedImages[i])
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (i == 0) {
                        product.image = task.result.toString()
                        images["0"] = task.result.toString()

                    } else {
                        images[i.toString()] = task.result.toString()

                        Handler().postDelayed({
                            if(i == (pickedImages.size-1)) {
                                product.images.putAll(images)
                                hideLoading()

                                setDetails()
                            }
                        }, 1500)
                    }

                }
            }
        }
    }

    //Detail dalam firebase
    private fun setDetails() {
        showLoading(getString(R.string.uploading_product_details))

        product.id = KEY
        product.name = product_name.text.toString().trim()
        product.sellerId = getUid()
        product.sellerName = prefs[K.NAME]
        product.time = System.currentTimeMillis()
        product.price = price.text.toString().trim()
        product.quantity = (quantity.text.toString()).toInt()
        product.category = category.selectedItem.toString()
        product.type = type.selectedItem.toString()
        product.location = location.text.toString().trim()
        product.description = desc.text.toString().trim()

        getFirestore().collection(K.PRODUCTS).document(KEY).set(product)
                .addOnSuccessListener {
                    hideLoading()

                    toast(getString(R.string.product_successfully_uploaded))
                }
                .addOnFailureListener {
                    hideLoading()

                    toast(getString(R.string.error_uploading_product))
                }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICKER && resultCode == Activity.RESULT_OK) {
            pickedImages = Matisse.obtainResult(data)

            setImages()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.animateEnterLeft(this)

    }

}
