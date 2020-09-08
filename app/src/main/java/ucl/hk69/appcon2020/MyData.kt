package ucl.hk69.appcon2020

import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.DataOutputStream
import java.lang.Exception
import java.net.Socket
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
     open var raspiIp:String = "",
     open var raspiPort:Int = 55555
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
          var ip:String? = null
          var port = 55555

          val map: MutableMap<String, String> = mutableMapOf()
          map["Title"] = title
          map["SubTitle"] = subTitle
          map["Background_Color"] = backColor
          map["Text_Color"] = textColor

          realm.executeTransaction {
               val data = realm.where(SettingData::class.java).equalTo("name", "setting").findFirst()
               ip = data?.raspiIp
               port = data?.raspiPort ?: 55555

               map["Token"] = realm.where(SettingData::class.java).equalTo("name", "setting").findFirst()?.token ?: ""
          }


          if(ip == null) {
               Log.d("connecting", "ip addr is null")
               return
          }

          val msg = Gson().toJson(map)
          Log.d("msg", msg)



          GlobalScope.launch {
               try {
                    val soc = Socket(ip, port)
                    val dos = DataOutputStream(soc.getOutputStream())
                    Log.d("com debug", "maked dos")
                    dos.writeUTF(msg)
                    Thread.sleep(1000)
                    dos.close()
                    soc.close()
                    Log.d("com debug", "soc closed")
               }catch (e:Exception){
                    e.printStackTrace()
               }
          }
     }
}

