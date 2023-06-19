package com.example.micompra

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.micompra.models.ItemProvider
import com.example.micompra.models.MarketProvider
import com.example.micompra.databinding.ActivityMainBinding
import com.example.micompra.models.AdapterItem
import com.example.micompra.models.MarketProvider.Companion.addMarket
import com.example.micompra.models.PriceProvider.Companion.addPrice
import com.example.micompra.models.PriceProvider.Companion.isPrice
import com.example.micompra.models.ProvideAdapterItem.Companion.listItemsAdapter
import com.google.android.material.textfield.TextInputLayout


class MainActivity : AppCompatActivity() {


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var hidden = View.GONE
    private var isAllFabsVisible: Boolean? = null
    private lateinit var rv_item: RecyclerView
    private val adapter:AdapterItems= AdapterItems()
    private lateinit var lista: MutableList<AdapterItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setUpRecyclerView()

        binding.addItemFab.visibility = hidden
        binding.addMarketFab.visibility = hidden
        binding.addPriceFab.visibility = hidden
        isAllFabsVisible = false
        binding.addFab.shrink()
        binding.addFab.setOnClickListener {


            isAllFabsVisible = if(!isAllFabsVisible!!){

                binding.addItemFab.show()
                binding.addMarketFab.show()
                binding.addPriceFab.show()

                binding.addFab.setIconResource(R.drawable.ic_baseline_close_24)
                binding.addFab.extend()

                true
            }else{

                binding.addPriceFab.hide()
                binding.addItemFab.hide()
                binding.addMarketFab.hide()


                binding.addFab.setIconResource(R.drawable.ic_baseline_add_24)
                binding.addFab.shrink()

                false
            }
        }


        binding.addMarketFab.setOnClickListener {
            addDialogMarket()
        }


        binding.addItemFab.setOnClickListener {
            startActivity(Intent(this,AddItem::class.java))
        }


        binding.addPriceFab.setOnClickListener {
            addDialogPrice()
        }
    }


    private fun setUpRecyclerView(){


        lista = listItemsAdapter(this)

        rv_item = binding.rvItems

        rv_item.layoutManager = GridLayoutManager(applicationContext, 2)

        adapter.AdapterItems(lista, this)
        rv_item.adapter = adapter
    }


    override fun onRestart() {
        super.onRestart()
        update()
    }


    private fun update(){
        lista = listItemsAdapter(this)
        adapter.setItemList(lista)
    }

    private fun addDialogMarket(){

        val input = this.layoutInflater.inflate(R.layout.activity_add_market, null)
        val til_name = input.findViewById<TextInputLayout>(R.id.til_name)
        val dialog = createDialog(input, "Añadir supermercado")

        dialog.setOnShowListener {
            val acept: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            acept.setOnClickListener {

                val et_name = input.findViewById<EditText>(R.id.et_name)
                val name = et_name.text.toString()
                val result = addMarket(this, name)


                if(result > 0) {
                    Toast.makeText(this, HtmlCompat.fromHtml("Añadido <b>${name}</b>", HtmlCompat.FROM_HTML_MODE_LEGACY), Toast.LENGTH_SHORT).show()
                    dialog.dismiss()


                }else if(result == ERROR_EMPTY){
                    til_name.error = "Está vacío"
                }
                else{

                    til_name.error = HtmlCompat.fromHtml("Ya existe <b>${name}</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
                }
            }
        }

        dialog.show()
    }


    private fun addDialogPrice(){

        val input = this.layoutInflater.inflate(R.layout.activity_add_price, null)
        val items = ItemProvider.listItemsSort(this, 1)
        val list_items_string: MutableList<String> = mutableListOf()

        for( i in items){
            list_items_string.add(i.name)
        }


        val spItemsAdapter:ArrayAdapter<String> = ArrayAdapter(this, androidx.transition.R.layout.support_simple_spinner_dropdown_item,list_items_string)
        val spItems = input.findViewById<Spinner>(R.id.s_productos)

        spItems.adapter = spItemsAdapter

        val markets = MarketProvider.listMarkets(this)
        val list_markets_string: MutableList<String> = mutableListOf()

        for( i in markets){
            list_markets_string.add(i.name)
        }


        val spMarketsAdapter:ArrayAdapter<String> = ArrayAdapter(this, androidx.transition.R.layout.support_simple_spinner_dropdown_item,list_markets_string)
        val spMarkets = input.findViewById<Spinner>(R.id.s_market)

        spMarkets.adapter = spMarketsAdapter

        val til_price = input.findViewById<TextInputLayout>(R.id.til_price)
        val til_market = input.findViewById<TextInputLayout>(R.id.til_market)
        val til_producto = input.findViewById<TextInputLayout>(R.id.til_producto)
        val dialog = createDialog(input, "Añadir supermercado")

        dialog.setOnShowListener {
            val acept: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            acept.setOnClickListener {

                val id_item = spItems.selectedItemId.toInt()
                val id_market = spMarkets.selectedItemId.toInt()
                val et_price = input.findViewById<EditText>(R.id.et_price)
                val price = et_price.text.toString().trim()
                val exist = isPrice(this, items[id_item], markets[id_market])
                val result = addPrice(this, items[id_item], markets[id_market], price)

                if(result > 0) {
                    Toast.makeText(this, "Precio Añadido correctamente", Toast.LENGTH_SHORT).show()
                    update()
                    dialog.dismiss()
                }else{

                    if(result == ERROR_EMPTY){
                        til_price.error = "Está vacío"
                    }else if(result == ERROR_0){
                        til_price.error = HtmlCompat.fromHtml("El valor tiene que ser <b>mayor a 0</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
                    }else{
                        til_price.error = null
                    }
                    if(exist > 0){
                        til_producto.error = HtmlCompat.fromHtml("Ya tiene un precio el producto <b>${items[id_item].name}</b> en <b>${markets[id_market].name}</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
                        til_market.error = HtmlCompat.fromHtml("Ya tiene un precio el producto <b>${items[id_item].name}</b> en <b>${markets[id_market].name}</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
                    }else{
                        til_producto.error = null
                        til_market.error = null
                    }
                }
            }
        }

        dialog.show()
    }

    fun createDialog(view: View, title: String):AlertDialog{
        return AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setCancelable(false)
            .setPositiveButton("Guardar", null)
            .setNegativeButton("Cancelar", null)
            .create()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_settings -> Toast.makeText(this, "En proceso de creación", Toast.LENGTH_SHORT).show()
        }


        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}