package com.example.micompra.models

import android.content.ContentValues
import android.content.Context
import com.example.micompra.*



class PriceProvider {
    companion object{

        //genera una lista de precios
        fun listPrices(context: Context): MutableList<Price>{

            //accedemos a la base de datos
            val dbHelper = FeedReaderDbHelper(context)

            // indicamos que queremos leer la base de datos
            val db = dbHelper.readableDatabase

            //indicamos que queremos ordenar los valores a devolver por el nombre descendentemente
            val sortOrd = "${FeedReaderContract.FeedEntry.COLUMN_PRICE} ASC"

            //creamos la query que nos devolvera un cursor
            val cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_PRICE,   // nombre de la tabla a acceder
                null,                                 // columnas a devolver
                null,                               // las columnas para el WHERE clause
                null,                            // los valores para el WHERE clause
                null,                                // para grupos de columnas
                null,                                 // para filtrar por grupos de columnas
                sortOrd                                 // para ordenar los valores a devolver
            )

            // lista donde almacenara los valores de los precios
            val lista:MutableList<Price> = mutableListOf()

            //recorro los resultados
            with(cursor){
                while (moveToNext()) {
                    //obtengo el id del item y market, y el precio
                    val id_item = getLong(getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_ITEM))
                    val id_marker = getLong(getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_MARKET))
                    val price = getFloat(getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_PRICE))

                    //se añade a la lista
                    lista.add(Price(id_item, id_marker, price))
                }
            }
            // cerramos el cursor
            cursor.close()

            return lista
        }

        //comprueba si existe ya un precio de ese producto en ese supermercado
        fun isPrice(context: Context, item: Item, market: Market): Int{

            //accedemos a la base de datos
            val dbHelper = FeedReaderDbHelper(context)

            //indicamos que vamos a leer la base de datos
            val db = dbHelper.readableDatabase

            //indicamos las columnas a devolver
            val projection = arrayOf(FeedReaderContract.FeedEntry.COLUMN_PRICE)

            //filtramos los datos por ids indicando las columnas
            val selection = "${FeedReaderContract.FeedEntry.COLUMN_ITEM} = ? AND ${FeedReaderContract.FeedEntry.COLUMN_MARKET} = ?"

            //indicamos los valores de los argumentos
            val selectionArgs = arrayOf(item.id.toString(), market.id.toString())

            val cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_PRICE,   // indicamos la tabla
                projection,                                 // las columnas a devolver
                selection,                                  // indicamos las columnas a filtrar
                selectionArgs,                              // indicamos los valores a filtrar en dichas columnas
                null,                                       // para grupos de columnas
                null,                                       // para filtrar grupos de columnas
                null                                       // para ordenar los valores
            )

            //guardamos el numero de entradas
            val entradas = cursor.count

            //cerramos el cursor
            cursor.close()

            return entradas
        }

        //añade un nuevo precio
        fun addPrice(context:Context, item:Item, market:Market, price: String): Long{

            //si no esta vacio
            if(price.isNotEmpty()){

                //lo convierte a float
                val price_f = price.toFloat()

                //si el precio no es 0
                if(price_f>0.0){
                    //accedemos a la base de datos
                    val dbHelper = FeedReaderDbHelper(context)

                    //indicamos que vamos a escribir en la base de datos
                    val db = dbHelper.writableDatabase

                    //comprobamos que no existen
                    val exist = isPrice(context, item, market)
                    if(exist == 0) {
                        //creamos el mapa de valores, (columna, valor)
                        val values = ContentValues().apply {
                            put(FeedReaderContract.FeedEntry.COLUMN_ITEM, item.id)
                            put(FeedReaderContract.FeedEntry.COLUMN_MARKET, market.id)
                            put(FeedReaderContract.FeedEntry.COLUMN_PRICE, price_f)
                        }

                        //añadimos los datos a la base de datos
                        return db.insert(FeedReaderContract.FeedEntry.TABLE_PRICE, null, values)
                    }

                    //error el producto existe
                    return ERROR_EXIST
                    /**
                     * Tengo pensado modificarlo para que si existe que actualice el precio
                     */

                }

                //error el precio es 0
                return ERROR_0
            }

            //error el precio esta vacio
            return ERROR_EMPTY
        }
    }
}