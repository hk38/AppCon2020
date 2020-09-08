package ucl.hk69.appcon2020

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {
    private var mainAct:MainActivity? = null

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if(activity is MainActivity) this.mainAct = activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mainAct?.setMode(R.drawable.ic_add_24, MyMode.MODE_LIST)
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val realm = Realm.getDefaultInstance()
        val dataList = realm.where(PlateData::class.java).findAll().sort("createdAt", Sort.ASCENDING)
        val adapter = RecyclerViewAdapter(requireContext(), dataList, object:RecyclerViewAdapter.OnItemClickListener{
            override fun onItemClick(item: PlateData) {
                val editAct = Intent(context, EditActivity::class.java)
                editAct.putExtra("UUID", item.id)
                startActivity(editAct)
            }
        }, object:RecyclerViewAdapter.OnLongClickListener{
            override fun onItemLongClickListener(item: PlateData) {
                realm.executeTransaction {
                    item.deleteFromRealm()
                }
            }
        }, true)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }
}