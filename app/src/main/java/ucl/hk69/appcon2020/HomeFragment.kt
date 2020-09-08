package ucl.hk69.appcon2020

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private var mainAct:MainActivity? = null

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if(activity is MainActivity) this.mainAct = activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mainAct?.setMode(R.drawable.ic_send_24, MyMode.MODE_HOME)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val realm = Realm.getDefaultInstance()

        val pref = mainAct?.getSharedPreferences("store", Context.MODE_PRIVATE)
        setPlate(
            PlateData(
                title = pref?.getString("title", "title") ?: "title",
                subTitle = pref?.getString("subTitle", "subTitle") ?: "subTitle",
                backColor = pref?.getString("backColor", "ffffffff") ?: "ffffffff",
                textColor = pref?.getString("textColor", "ff000000") ?: "ff000000"
            ))


        buttonSetTemp.setOnClickListener {
            val dataList = realm.where(PlateData::class.java).findAll().sort("createdAt", Sort.ASCENDING)
            val strList = arrayOfNulls<String>(dataList.size)

            dataList.forEachIndexed { i, data ->
                strList[i] = data.name
            }

            AlertDialog.Builder(context) // FragmentではActivityを取得して生成
                .setTitle("適用するテンプレートを選択")
                .setItems(strList) { _, which ->
                    setPlate(dataList[which])
                }
                .show()
        }
    }

    override fun onPause() {
        val plate = getCurrentPlate()
        mainAct?.getSharedPreferences("store", Context.MODE_PRIVATE)?.edit()?.apply(){
            putString("title", plate.title)
            putString("subTitle", plate.subTitle)
            putString("backColor", plate.backColor)
            putString("textColor", plate.textColor)
            apply()
        }
        super.onPause()
    }

    private fun setPlate(data: PlateData?){
        editTitle.setText(data?.title)
        editSubTitle.setText(data?.subTitle)
        linearLayout.setBackgroundColor(Color.parseColor("#${data?.backColor}"))
        editTitle.setTextColor(Color.parseColor("#${data?.textColor}"))
        editSubTitle.setTextColor(Color.parseColor("#${data?.textColor}"))
    }

    fun getCurrentPlate():PlateData{
        return PlateData(
            backColor = Integer.toHexString((linearLayout.background as ColorDrawable).color),
            textColor = Integer.toHexString(editTitle.currentTextColor),
            title = editTitle.text.toString(),
            subTitle = editSubTitle.text.toString()
        )
    }
}