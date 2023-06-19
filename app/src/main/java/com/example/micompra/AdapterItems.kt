package com.example.micompra

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.micompra.models.AdapterItem


class AdapterItems: RecyclerView.Adapter<AdapterItems.ItemViewHolder>(){

    var productos:MutableList<AdapterItem> = ArrayList()
    lateinit var context: Context

    fun AdapterItems(productos: MutableList<AdapterItem>, context: Context){
        this.productos = productos
        this.context = context
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val producto = productos.get(position)

        holder.bind(producto)
    }


    fun setItemList(productos: List<AdapterItem>){
        this.productos = productos.toMutableList()
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int { return productos.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(R.layout.row_item, parent, false))
    }

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val titulo = view.findViewById(R.id.t_name) as TextView
        private val precio = view.findViewById<TextView>(R.id.t_price)
        private val market = view.findViewById<TextView>(R.id.t_market)


        fun bind(producto:AdapterItem){
            titulo.text = producto.item.name
            market.text = producto.market.name
            precio.text = producto.price.toString()+" $"


        }
    }
}
