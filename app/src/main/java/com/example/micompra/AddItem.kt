package com.example.micompra

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.example.micompra.models.ItemProvider.Companion.addItem
import com.example.micompra.databinding.ActivityAddItemBinding



class AddItem: AppCompatActivity() {

    private lateinit var binding: ActivityAddItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)


        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.bAdd.setOnClickListener {

            val producto = binding.etName.text.toString().trim()

            val result = addItem(this, producto)

            if(result > 0){
                Toast.makeText(this, HtmlCompat.fromHtml("Producto <b>${producto}</b> añadido", HtmlCompat.FROM_HTML_MODE_LEGACY), Toast.LENGTH_SHORT).show()
                finish()


            }else if(result == ERROR_EMPTY){
                    binding.tilName.error = "Está vacío"

            }else{
                binding.tilName.error = HtmlCompat.fromHtml("Ya existe <b>${producto}</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
        }

    }
}
