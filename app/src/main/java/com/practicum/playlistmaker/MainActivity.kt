package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Нажатие кнопки <Поиск> через через реализацию анонимного класса
        val button_search = findViewById<Button>(R.id.button_search)
        val buttonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val displayIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(displayIntent)
                // Toast.makeText(this@MainActivity, "Нажали на кнопку <Поиск>", Toast.LENGTH_SHORT).show()
            }
        }
        button_search.setOnClickListener(buttonClickListener)

        // Нажатие кнопки <Медиатека> с помощью лямбда-выражения
        val button_media = findViewById<Button>(R.id.button_media)
        button_media.setOnClickListener {
            val displayIntent = Intent(this, MediaActivity::class.java)
            startActivity(displayIntent)
            // Toast.makeText(this@MainActivity, "Нажали на кнопку <Медиатека>", Toast.LENGTH_SHORT).show()
        }

        // Нажатие кнопки <Настройки> с помощью лямбда-выражения
        val button_settings = findViewById<Button>(R.id.button_settings)
        button_settings.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
            // Toast.makeText(this@MainActivity, "Нажали на кнопку <Настройки>", Toast.LENGTH_SHORT).show()
        }
   }
}