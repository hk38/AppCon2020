package ucl.hk69.appcon2020

import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.DataOutputStream
import java.lang.Exception
import java.util.UUID
import java.util.Date

open class PlateData(
     @PrimaryKey open var id:String = UUID.randomUUID().toString(),
     open var name:String = "",
     open var backColor:String = "",
     open var textColor:String = "",
     open var title:String = "",
     open var subTitle:String = "",
     open var createdAt: Date = Date(System.currentTimeMillis())
):RealmObject()

open class SettingData(
     @PrimaryKey open var name:String = "setting",
     open var token:String = "",
     open var tokenCanged:Boolean = false,
     open var raspiName:String = ""
):RealmObject()

class MyMode{
     companion object {
          const val MODE_HOME = 0
          const val MODE_LIST = 1
          const val MODE_SETTING = 2
          const val TEXT_BLACK = false
          const val TEXT_WHITE = true
     }
}

class SendData(val title: String, val subTitle: String, val backColor: String, val textColor: String, val ctx: Context){
     fun send(){
          val realm = Realm.getDefaultInstance()
          var name = "raspberrypi"
          realm.executeTransaction {
               name = realm.where(SettingData::class.java).equalTo("name", "setting").findFirst()?.raspiName ?: "raspberrypi"
          }
          val btManager = ctx.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
          val btAdapter = btManager.adapter
          val btDevices = btAdapter.bondedDevices.toList()
          var btSoc: BluetoothSocket? = null
          for (device in btDevices) {
               if (device.name == name) btSoc = device.createRfcommSocketToServiceRecord(UUID.fromString("5E7B99D0-F404-4425-8125-98A2265B4333"))
          }

          if(btSoc == null) {
               Log.d("sendData", "BT Device $name is Null")
               return
          }


          try {
               Log.d("bt debug", "before connect")
               btSoc.connect()
               Log.d("bt debug", "after connect, before getDOS")
               val btDos = DataOutputStream(btSoc.outputStream)

               Log.d("bt debug", "make Map")
               val map: MutableMap<String, String> = mutableMapOf()
               map["title"] = title
               map["subTitle"] = subTitle
               map["backColor"] = backColor
               map["textColor"] = textColor
               realm.executeTransaction {
                    map["token"] = realm.where(SettingData::class.java).equalTo("name", "setting").findFirst()?.token ?: ""
               }

               val msg = Gson().toJson(map)
               Log.d("bt debug", "$msg, before send")

               btDos.writeUTF(msg)
               btDos.flush()
               Log.d("bt debug", "after send")

               Thread.sleep(1000)
               Log.d("bt debug", "before Soc close")
               btDos.close()
               Log.d("bt debug", "after sock close")
          }catch (e: Exception){ e.printStackTrace() }finally {
               try {
                    btSoc.close()
               }catch (e: Exception){ e.printStackTrace() }
          }
     }
}

