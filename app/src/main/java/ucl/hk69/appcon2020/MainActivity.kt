package ucl.hk69.appcon2020

import android.app.TimePickerDialog
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.iid.FirebaseInstanceId
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import java.io.DataOutputStream
import java.lang.Exception
import java.util.*
import java.util.concurrent.Executor


class MainActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {
    var fabClickMode = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_list,
                R.id.navigation_setting
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)


        val realm = Realm.getDefaultInstance()
        if(realm.where(PlateData::class.java).findAll().isEmpty()){
            realm.executeTransaction {
                val defData = it.createObject(PlateData::class.java, UUID.randomUUID().toString())
                defData.name = "デフォルトテンプレート"
                defData.title = "部屋名"
                defData.subTitle = "ひとことメモ"
                defData.backColor = "ffffffff"
                defData.textColor = "ff000000"
            }

            getSharedPreferences("store", Context.MODE_PRIVATE).edit().apply(){
                putString("title", "部屋名")
                putString("subTitle", "ひとことメモ")
                putString("backColor", "ffffffff")
                putString("textColor", "ff000000")
                apply()
            }

        }
        
        if(realm.where(SettingData::class.java).findAll().isEmpty()){
            realm.executeTransaction {
                val setting = it.createObject(SettingData::class.java, "setting")
                setting.raspiName = ""
                setting.token = ""
                setting.tokenCanged = false
            }
        }


        fab.setOnClickListener {
            val navFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager?.fragments?.get(0)
            when(fabClickMode){
                MyMode.MODE_HOME -> {
                    if(navFragment is HomeFragment) {
                        val plate = navFragment.getCurrentPlate()
                        sendPlateData(plate)
                    }
                }
                MyMode.MODE_LIST -> {
                    val intent = Intent(this, EditActivity::class.java)
                    startActivity(intent)
                }
                MyMode.MODE_SETTING -> {
                    if(navFragment is SettingFragment){
                        navFragment.saveSetting()
                    }
                }
            }
        }

        fab.setOnLongClickListener {
            val navFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager?.fragments?.get(0)
            if( fabClickMode == MyMode.MODE_HOME && navFragment is HomeFragment ){
                showTimePickerDialog()
            }
            return@setOnLongClickListener true
        }

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            if (!task.isSuccessful) realm.executeTransaction {
                val setting = realm.where(SettingData::class.java).equalTo("name", "setting").findFirst()
                setting?.token = task.result?.token ?: ""
                setting?.tokenCanged = true
                Log.d("token", "${setting?.token}")
            }
            else return@addOnCompleteListener

        }
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val timerCount = (hourOfDay - Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) * 60 + minute - Calendar.getInstance().get(Calendar.MINUTE)
        if(timerCount < 0) return

        val navFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager?.fragments?.get(0)
        if( fabClickMode == MyMode.MODE_HOME && navFragment is HomeFragment ){
            val intent = Intent(this, TimerIntentService::class.java)
            val plate = navFragment.getCurrentPlate()
            intent.putExtra("lim", timerCount)
            intent.putExtra("bgColor", plate.backColor)
            intent.putExtra("textColor", plate.textColor)
            intent.putExtra("title", plate.title)
            intent.putExtra("subTitle", plate.subTitle)
            startService(intent)
        }
    }

    private fun showTimePickerDialog() {
        val newFragment = TimePick()
        newFragment.show(supportFragmentManager, "timePicker")
    }

    private fun sendPlateData(data: PlateData){
        val sender = SendData(data.title, data.subTitle, data.backColor, data.textColor, applicationContext)
        sender.send()
    }

    fun setMode(resId: Int, mode: Int){
        fab.setImageResource(resId)
        fabClickMode = mode
    }
}