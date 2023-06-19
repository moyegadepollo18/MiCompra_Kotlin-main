package com.example.micompra.models

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.example.micompra.ERROR_EMPTY
import com.example.micompra.ERROR_EXIST
import com.example.micompra.FeedReaderContract
import com.example.micompra.FeedReaderDbHelper

class ItemProvider {
    companion object{

        fun listItems(context: Context): MutableList<Item>{

            val dbHelper = FeedReaderDbHelper(context)
            val db = dbHelper.readableDatabase
            val projection = arrayOf(BaseColumns._ID, FeedReaderContract.FeedEntry.COLUMN_NAME)
            val cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_ITEMS,
                projection,
                null,
                null,
                null,
                null,
                null
            )

            val lista:MutableList<Item> = mutableListOf()

            with(cursor){
                while (moveToNext()) {


                    val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                    val name = getString(getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME))

                    lista.add(Item(id, name.replaceFirstChar(Char::titlecase)))
                }
            }

            cursor.close()

            return lista
        }


        fun listItemsSort(context: Context, ord: Int): MutableList<Item>{

            val dbHelper = FeedReaderDbHelper(context)
            val db = dbHelper.readableDatabase
            val projection = arrayOf(BaseColumns._ID, FeedReaderContract.FeedEntry.COLUMN_NAME)
            var order = "ASC"
            if(ord == 0){
                order = "DESC"
            }

            val sortOrd = "${FeedReaderContract.FeedEntry.COLUMN_NAME} $order"
            val cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_ITEMS,
                projection,
                null,
                null,
                null,
                null,
                sortOrd
            )

            val lista:MutableList<Item> = mutableListOf()

            with(cursor){
                while (moveToNext()) {


                    val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                    val name = getString(getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME))


                    lista.add(Item(id, name.replaceFirstChar(Char::titlecase)))
                }
            }
            cursor.close()// cerramos el cursor

            return lista
        }


        fun isItem(context: Context, name: String): Int{

            val dbHelper = FeedReaderDbHelper(context)
            val db = dbHelper.readableDatabase
            val projection = arrayOf(BaseColumns._ID, FeedReaderContract.FeedEntry.COLUMN_NAME)
            val selection = "${FeedReaderContract.FeedEntry.COLUMN_NAME} = ?"
            val selectionArgs = arrayOf(name)

            val cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_ITEMS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null,
            )

            val entradas = cursor.count


            cursor.close()

            return entradas
        }

        fun addItem(context: Context, name: String): Long{

            if(name.isNotEmpty()) {

                val name_min = name.lowercase()

                val exist = isItem(context, name_min)

                if (exist == 0) {

                    val dbHelper = FeedReaderDbHelper(context)

                    val db = dbHelper.writableDatabase

                    val values = ContentValues().apply {
                        put(FeedReaderContract.FeedEntry.COLUMN_NAME, name_min)
                    }

                    return db.insert(FeedReaderContract.FeedEntry.TABLE_ITEMS, null, values)

                }
            }else{
                return ERROR_EMPTY
            }


            return ERROR_EXIST
        }
    }
}