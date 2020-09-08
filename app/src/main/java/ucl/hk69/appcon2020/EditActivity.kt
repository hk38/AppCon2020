package ucl.hk69.appcon2020

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.core.widget.addTextChangedListener
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_edit.*
import java.util.*
import kotlin.math.pow

class EditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        title = "Edit Template"
        val editRgbArray = arrayOf(editR, editG, editB)
        val rgbBarArray = arrayOf(seekBarR, seekBarG, seekBarB)

        for(i in 0..2){
            editRgbArray[i].addTextChangedListener {
                when {
                    editRgbArray[i].text.isEmpty() -> editRgbArray[i].setText(0.toString())
                    Integer.parseInt(editRgbArray[i].text.toString()) > 255 -> editRgbArray[i].setText(255.toString())
                    Integer.parseInt(editRgbArray[i].text.toString()) < 0 -> editRgbArray[i].setText(0.toString())
                }
                editRgbArray[i].setSelection(editRgbArray[i].text.length)
                rgbBarArray[i].progress = Integer.parseInt(editRgbArray[i].text.toString())

                plateLL.setBackgroundColor(Color.rgb(
                    Integer.parseInt(editR.text.toString()),
                    Integer.parseInt(editG.text.toString()),
                    Integer.parseInt(editB.text.toString())
                ))

                if(checkTextColor() == MyMode.TEXT_WHITE){
                    editTitle.setTextColor(Color.WHITE)
                    editSubTitle.setTextColor(Color.WHITE)
                }else{
                    editTitle.setTextColor(Color.BLACK)
                    editSubTitle.setTextColor(Color.BLACK)
                }

            }
            rgbBarArray[i].setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, formUser: Boolean) {
                    editRgbArray[i].setText(progress.toString())
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
        
        val id = intent.getStringExtra("UUID")
        val realm = Realm.getDefaultInstance()

        if(id.isNullOrBlank()){
            setPlate("", "", "", "FFFFFFFF")
        } else {
            val item = realm.where(PlateData::class.java).equalTo("id", id).findFirst()
            setPlate(item?.name, item?.title, item?.subTitle, item?.backColor)
        }

        fab.setOnClickListener {
            realm.executeTransaction {
                val item = if(id.isNullOrBlank()) {
                    val uuid = UUID.randomUUID().toString()
                    it.createObject(PlateData::class.java, uuid)

                }
                else {
                    realm.where(PlateData::class.java).equalTo("id", id).findFirst()
                }

                item?.name = editName.text.toString()
                item?.title = editTitle.text.toString()
                item?.subTitle = editSubTitle.text.toString()
                item?.backColor = Integer.toHexString((plateLL.background as ColorDrawable).color)
                item?.textColor = Integer.toHexString(editTitle.currentTextColor)
            }
            finish()
        }
    }
    
    private fun setPlate(name:String?, main:String?, sub:String?, bgColor:String?){
        val chunk = bgColor?.chunked(2)
        editName.setText(name)
        editTitle.setText(main)
        editSubTitle.setText(sub)

        editName.hint = "テンプレート名"
        editTitle.hint = "メインタイトル"
        editSubTitle.hint = "サブタイトル"

        if(chunk.isNullOrEmpty() || chunk.size < 4) return
        editR.setText(chunk[1].toInt(16).toString())
        editG.setText(chunk[2].toInt(16).toString())
        editB.setText(chunk[3].toInt(16).toString())
    }


    private fun checkTextColor():Boolean{

        val chunk = (Integer.toHexString((plateLL.background as ColorDrawable).color)).chunked(2)

        val lum = 0.2126 * relativeLuminance(chunk[1].toInt(16) / 255.0) +
                0.7152 * relativeLuminance(chunk[2].toInt(16) / 255.0) +
                0.0722 * relativeLuminance(chunk[3].toInt(16) / 255.0)

        return if( (1.05 / lum + 0.05) > (lum + 0.05 / 0.05)) MyMode.TEXT_WHITE
        else MyMode.TEXT_BLACK
    }

    private fun relativeLuminance(value: Double):Double{
        return if(value <= 0.03928){
            value / 12.92
        }else{
            ((value + 0.055) / 1.055).pow(2.4)
        }
    }

}