package ucl.hk69.appcon2020

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : Fragment() {
    private var mainAct:MainActivity? = null

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if(activity is MainActivity) this.mainAct = activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mainAct?.setMode(R.drawable.ic_check_24, MyMode.MODE_SETTING)
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val realm = Realm.getDefaultInstance()

        realm.executeTransaction {
            val setting = realm.where(SettingData::class.java).equalTo("name", "setting").findFirst() ?: return@executeTransaction
            textToken.text = "token: ${setting?.token}"
            textToken.setTextColor(
                if(setting.tokenCanged) Color.RED
                else Color.BLACK
            )
        }
    }

    fun saveSetting(){
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val setting = realm.where(SettingData::class.java).equalTo("name", "setting").findFirst()
            setting?.raspiName = editBtName.text.toString()
            Log.d("token", "${setting?.token}")
        }
    }
}