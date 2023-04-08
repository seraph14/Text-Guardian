package com.example.safesend

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.safesend.ui.home.MessagesFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var messagesFragment: MessagesFragment
    val REQUEST_CODE_ASK_PERMISSION = 123
    val REQUEST_CODE_ASK_DEFAULT = 3343
    var ReadPermission: Boolean = false
    var DefaultPermission: Boolean = false
    private lateinit var br: MessageReceivedBroadcastReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestDefaultSmsPermission()

        br = MessageReceivedBroadcastReceiver()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, _: Bundle? ->
            if (nd.id == nc.graph.startDestination) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
        navView.setupWithNavController(navController)
        messagesFragment = MessagesFragment()
    }

    override fun onResume() {
        super.onResume()
        if(ReadPermission && DefaultPermission){
            Toast.makeText(this@MainActivity, "Permission not granted!", Toast.LENGTH_LONG).show()
        }else{
            registerReceiver(br, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
        }

    }

    private fun requestDefaultSmsPermission(){
        if (Telephony.Sms.getDefaultSmsPackage(applicationContext) != packageName) {
            val setSmsAppIntent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
            setSmsAppIntent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
            startActivityForResult(setSmsAppIntent, REQUEST_CODE_ASK_DEFAULT)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.about_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ASK_DEFAULT && resultCode == RESULT_OK) {
            DefaultPermission = true
            Toast.makeText(this@MainActivity, "Yes, now app is default sms app!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this@MainActivity, "Set the app as default sms app!", Toast.LENGTH_LONG).show()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
         if (requestCode == REQUEST_CODE_ASK_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
             ReadPermission = true
             Toast.makeText(this@MainActivity, "yeah! permission granted!", Toast.LENGTH_LONG).show()
         }
         else {
             Toast.makeText(this@MainActivity, "Please accept the permissions!", Toast.LENGTH_LONG).show()
         }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(br)
    }

}