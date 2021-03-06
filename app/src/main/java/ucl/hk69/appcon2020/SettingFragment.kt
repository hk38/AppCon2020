package ucl.hk69.appcon2020

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
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
            editIp.setText(setting.raspiIp)
        }

        buttonVisible.setOnClickListener {
            when (textToken.visibility) {
                View.INVISIBLE -> {
                    AlertDialog.Builder(context)
                        .setTitle("Tokenを表示しますか？")
                        .setPositiveButton("はい"){_, _ ->
                            buttonVisible.setBackgroundResource(R.drawable.ic_visibility_24)
                            textToken.visibility = View.VISIBLE
                        }
                        .setNegativeButton("いいえ") { _, _ ->
                            textToken.visibility = View.INVISIBLE
                        }
                        .show()
                }
                View.VISIBLE -> {
                    buttonVisible.setBackgroundResource(R.drawable.ic_visibility_off_24)
                    textToken.visibility = View.INVISIBLE
                }
            }
        }
    }

    fun saveSetting(){
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            val setting = realm.where(SettingData::class.java).equalTo("name", "setting").findFirst()
            setting?.raspiIp = editIp.text.toString()
            setting?.raspiPort = editPort.text.toString().toInt()
            Log.d("token", "${setting?.token}")
        }

        Snackbar.make(requireView(), "saved!", Snackbar.LENGTH_SHORT).show()
    }
}