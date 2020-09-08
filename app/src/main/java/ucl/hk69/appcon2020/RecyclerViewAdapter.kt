package ucl.hk69.appcon2020

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.plate_item.view.*

class RecyclerViewAdapter(private val context: Context,
                          private var list:OrderedRealmCollection<PlateData>?,
                          private var listener: OnItemClickListener,
                          private var longListener: OnLongClickListener,
                          private val autoUpdate:Boolean):
    RealmRecyclerViewAdapter<PlateData, RecyclerViewAdapter.ViewHolder>(list, autoUpdate) {

    override fun getItemCount() = list?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: PlateData = list?.get(position) ?: return

        holder.container.setOnClickListener {
            listener.onItemClick(item)
        }

        holder.container.setOnLongClickListener {
            longListener.onItemLongClickListener(item)
            return@setOnLongClickListener true
        }

        holder.templateColor.setBackgroundColor(Color.parseColor("#${item.backColor}"))
        holder.templateName.text = item.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.plate_item, parent, false))
    }

    class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val container : ConstraintLayout = v.container
        val templateColor:ImageView = v.templateColor
        val templateName:TextView = v.templateName
    }

    interface OnItemClickListener {
        fun onItemClick(item: PlateData)
    }

    interface OnLongClickListener{
        fun onItemLongClickListener(item:PlateData)
    }
}