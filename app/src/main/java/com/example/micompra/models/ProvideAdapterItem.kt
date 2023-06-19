package com.example.micompra.models

import android.content.Context
import com.example.micompra.models.ItemProvider.Companion.listItems
import com.example.micompra.models.MarketProvider.Companion.listMarkets
import com.example.micompra.models.PriceProvider.Companion.listPrices

class ProvideAdapterItem {
    companion object{
        //genera una lista de productos con el supermercado con menor precio
        fun listItemsAdapter(context: Context): MutableList<AdapterItem> {
            val items = listItems(context)
            val markets = listMarkets(context)
            val prices = listPrices(context)

            val lista:MutableList<AdapterItem> = mutableListOf()

            //recorremos los productos
            for (item in items){

                val filterPrice = prices.filter { price -> price.id_item == item.id }

                //si no hay ningun precio de ese producto no se añade
                if(filterPrice.isNotEmpty()) {
                    //obtenemos el supermercado
                    val filterMarket =
                        markets.filter { market -> market.id == filterPrice[0].id_market }

                    //añadimos el producto, el supermercado y el precio
                    lista.add(AdapterItem(item, filterMarket[0], filterPrice[0].price))
                }
            }

            return lista
        }
    }
}
