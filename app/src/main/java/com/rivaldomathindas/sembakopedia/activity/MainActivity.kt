package com.rivaldomathindas.sembakopedia.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.labters.lottiealertdialoglibrary.ClickListener
import com.labters.lottiealertdialoglibrary.DialogTypes
import com.labters.lottiealertdialoglibrary.LottieAlertDialog
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.fragment.HomeFragment
import com.rivaldomathindas.sembakopedia.fragment.StatisticsFragment
import com.rivaldomathindas.sembakopedia.fragment.ChatsFragment
import com.rivaldomathindas.sembakopedia.base.BaseActivity
import com.rivaldomathindas.sembakopedia.utils.AppUtils
import com.rivaldomathindas.sembakopedia.utils.K
import com.rivaldomathindas.sembakopedia.utils.PreferenceHelper
import com.rivaldomathindas.sembakopedia.utils.PreferenceHelper.get
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : BaseActivity() {

    private var doubleBackToExit = false

    private lateinit var drawer: Drawer
    private lateinit var prefs: SharedPreferences
    private lateinit var alertDialog : LottieAlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prefs = PreferenceHelper.defaultPrefs(this@MainActivity)

        addNavigationView()
        navigationView.show(1)
        openFragment(HomeFragment())
        setupDrawer()
        setupBottomNavigation()
    }

    //pengaturan drawer
    private fun setupDrawer() {
        val accountHeader = AccountHeaderBuilder().withActivity(this)
            .withSelectionListEnabled(false)
            .withProfileImagesVisible(false)
            .withHeaderBackground(R.drawable.background_drawer)
            .addProfiles(
                ProfileDrawerItem()
                .withName(prefs[K.NAME, ""])
                .withEmail(prefs[K.EMAIL, ""]))
            .build()

        val home = SecondaryDrawerItem().withIdentifier(0).withName(getString(R.string.home)).withIcon(R.drawable.icon_home)
        val mySales = SecondaryDrawerItem().withIdentifier(1).withName(getString(R.string.my_sales)).withIcon(R.drawable.icon_my_sales)
        val myPurchases = SecondaryDrawerItem().withIdentifier(2).withName(getString(R.string.my_purchases)).withIcon(R.drawable.icon_my_purchases)
        val incomingOrders = SecondaryDrawerItem().withIdentifier(3).withName(getString(R.string.incoming_orders)).withIcon(R.drawable.icon_incoming_orders)
        val statistics = SecondaryDrawerItem().withIdentifier(4).withName(getString(R.string.statistics)).withIcon(R.drawable.statistics)
        val chat = SecondaryDrawerItem().withIdentifier(5).withName(getString(R.string.chats)).withIcon(R.drawable.icon_chats)
        val settings = SecondaryDrawerItem().withIdentifier(6).withName(getString(R.string.settings)).withIcon(R.drawable.icon_settings)
        val about = SecondaryDrawerItem().withIdentifier(7).withName(getString(R.string.about)).withIcon(R.drawable.icon_about)
        val logOut = SecondaryDrawerItem().withIdentifier(8).withName(getString(R.string.logout)).withIcon(R.drawable.icon_logout)

        drawer = DrawerBuilder().withActivity(this)
            .withToolbar(toolbar)
            .withAccountHeader(accountHeader)
            .addDrawerItems(home, mySales, myPurchases, incomingOrders, statistics , chat, DividerDrawerItem(), settings, about, logOut)
            .withOnDrawerItemClickListener { _, _, drawerItem ->
                when(drawerItem) {
                    home -> {
                        openFragment(HomeFragment())
                        navigationView.show(1)
                    }
                    mySales -> launchActivity(MySales::class.java)
                    myPurchases -> launchActivity(MyPurchases::class.java)
                    incomingOrders -> launchActivity(OrdersActivity::class.java)
                    statistics -> {
                        openFragment(StatisticsFragment())
                        navigationView.show(2)
                    }
                    chat -> {
                        openFragment(ChatsFragment())
                        navigationView.show(3)
                    }
                    about -> launchActivity(AboutActivity::class.java)
                    settings -> launchActivity(SettingActivity::class.java)
                    logOut -> logOut()
                }
                true
            }
            .build()
    }

    //pengaturan bottom navigation
    private fun setupBottomNavigation() {
        navigationView.setOnClickMenuListener {
            when (it.id) {
                1 -> {
                    val firstFragment =
                        HomeFragment()
                    openFragment(firstFragment)
                    return@setOnClickMenuListener
                }
                2 -> {
                    val secondFragment =
                        StatisticsFragment()
                    openFragment(secondFragment)
                    return@setOnClickMenuListener
                }
                3 -> {
                    val thirdFragment =
                        ChatsFragment()
                    openFragment(thirdFragment)
                    return@setOnClickMenuListener
                }
            }
        }
    }

    //menambah menu pada bottom navigation
    private fun addNavigationView() {
        navigationView.add(MeowBottomNavigation.Model(1,
            R.drawable.ic_home
        ))
        navigationView.add(MeowBottomNavigation.Model(2,
            R.drawable.ic_statistics
        ))
        navigationView.add(MeowBottomNavigation.Model(3,
            R.drawable.ic_baseline_chat_24
        ))
    }

    //log out
    @Suppress("DEPRECATION")
    private fun logOut() {
        alertDialog  = LottieAlertDialog.Builder(this, DialogTypes.TYPE_LOADING)
            .setTitle(getString(R.string.loading))
            .setDescription(getString(R.string.please_wait))
            .build()
        alertDialog.setCancelable(false)
        alertDialog.show()

        Handler().postDelayed(Runnable {
            alertDialog.changeDialog(
                LottieAlertDialog.Builder(this, DialogTypes.TYPE_QUESTION)
                .setTitle(getString(R.string.logout))
                .setDescription(getString(R.string.are_you_sure_to_log_out))
                .setPositiveText(getString(R.string.cancel))
                .setNegativeText(getString(R.string.yes))
                .setPositiveButtonColor(resources.getColor(R.color.positiveButtonColor))
                .setPositiveTextColor(resources.getColor(R.color.white))
                .setNegativeButtonColor(resources.getColor(R.color.negativeButtonColor))
                .setNegativeTextColor(resources.getColor(R.color.white))
                .setPositiveListener(object: ClickListener {
                    override fun onClick(dialog: LottieAlertDialog) {
                        dialog.dismiss()
                    }
                })
                .setNegativeListener(object : ClickListener
                {
                    override fun onClick(dialog: LottieAlertDialog) {
                        val firebaseAuth = FirebaseAuth.getInstance()
                        firebaseAuth.signOut()
                        FirebaseMessaging.getInstance()
                            .unsubscribeFromTopic(K.TOPIC_GLOBAL)
                        startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                        AppUtils.animateEnterLeft(this@MainActivity)
                        finish()
                    }
                })
            )
        },2000)
    }

    //membuka activity dengan animasi
    private fun launchActivity(intentClass: Class<*>) {
        val intent = Intent(this, intentClass)
        startActivity(intent)
        overridePendingTransition(R.anim.enter_b, R.anim.exit_a)

        Handler().postDelayed({
            drawer.closeDrawer()
            drawer.setSelection(0)
        }, 300)

    }

    //membuka fragment
    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


    //memodifikasi fungsi dari tombol back
    override fun onBackPressed() {
        if (doubleBackToExit) {
            super.finish()
        } else {
            doubleBackToExit = true
            toast(getString(R.string.tap_back_again_to_exit))

            Handler().postDelayed({doubleBackToExit = false}, 1500)
        }
    }
}
