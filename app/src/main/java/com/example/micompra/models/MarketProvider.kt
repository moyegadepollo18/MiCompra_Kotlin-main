package com.example.micompra.models

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.example.micompra.ERROR_EMPTY
import com.example.micompra.ERROR_EXIST
import com.example.micompra.FeedReaderContract
import com.example.micompra.FeedReaderDbHelper



class MarketProvider {

    companion object{


        fun listMarkets(context: Context): MutableList<Market>{

            val dbHelper = FeedReaderDbHelper(context)
            val db = dbHelper.readableDatabase
            val projection = arrayOf(BaseColumns._ID, FeedReaderContract.FeedEntry.COLUMN_NAME)
            val sortOrd = "${FeedReaderContract.FeedEntry.COLUMN_NAME} ASC"
            val cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_MARKET,
                projection,
                null,
                null,
                null,
                null,
                sortOrd
            )

            val lista:MutableList<Market> = mutableListOf()


            with(cursor){

                while (moveToNext()){
                    val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                    val name = getString(getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME))

                    lista.add(Market(id, name.replaceFirstChar(Char::titlecase)))
                }
            }
            cursor.close()

            return lista
        }

        fun isMarket(context: Context, name: String): Int{

            val dbHelper = FeedReaderDbHelper(context)
            val db = dbHelper.readableDatabase
            val projection = arrayOf(BaseColumns._ID, FeedReaderContract.FeedEntry.COLUMN_NAME)
            val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME} = ?"
            val selectionArgs = arrayOf(name)
            val cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_MARKET,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            )

            val entradas = cursor.count
            cursor.close()
            return entradas
        }

        fun addMarket(context: Context, name: String): Long {

            if(name.isNotEmpty()) {

                val name_min = name.lowercase().trim()

                val existe = isMarket(context, name_min)

                if (existe == 0) {

                    val dbHelper = FeedReaderDbHelper(context)

                    val db = dbHelper.writableDatabase


                    val values = ContentValues().apply {
                        put(FeedReaderContract.FeedEntry.COLUMN_NAME, name_min)
                    }

                    return db.insert(FeedReaderContract.FeedEntry.TABLE_MARKET, null, values)
                }
            }else{
                return ERROR_EMPTY
            }

            return ERROR_EXIST
        }
    }
}