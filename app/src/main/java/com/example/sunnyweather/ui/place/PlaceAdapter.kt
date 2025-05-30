package com.example.sunnyweather.ui.place

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.sunnyweather.R
import com.example.sunnyweather.logic.model.Place
import org.w3c.dom.Text

//标准的适配器写法  没什么值得注释的
class PlaceAdapter(private val fragment: Fragment, private val placeList: List<Place>) : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.place_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        var place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var placeName : TextView = view.findViewById<TextView>(R.id.placeName)
        var placeAddress : TextView = view.findViewById<TextView>(R.id.placeAddress)
    }
}