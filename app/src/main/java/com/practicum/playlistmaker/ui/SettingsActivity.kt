package com.practicum.playlistmaker.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.storage.NightModeStore

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Обработка нажатия кнопки <Назад> экрана <Настройки> для возврата на главный экран
        val returnFromSettings = findViewById<Button>(R.id.return_from_settings)
        returnFromSettings.setOnClickListener { finish() }

        // Установка переключателя <Темная тема> в правильное положение
        val switch_night = findViewById<SwitchMaterial>(R.id.switch_dark_theme)
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            switch_night.isChecked = true
        else
            switch_night.isChecked = false
        // Обработка нажатия на переключатель <Темная тема>
        switch_night.setOnClickListener {
            setNightLight(switch_night.isChecked)
        }
        // Обработка нажатия кнопки <Поделится приложением>
        val button_share = findViewById<Button>(R.id.button_share)
        button_share.setOnClickListener {
            shareApp()
        }
        // Обработка нажатия кнопки <Написать в поддержку>
        val button_support = findViewById<Button>(R.id.button_support)
        button_support.setOnClickListener {
            writeToSupport()
        }
        // Обработка нажатия кнопки <Пользовательское соглашение>
        val button_condition = findViewById<Button>(R.id.button_agreement)
        button_condition.setOnClickListener {
            userAgreement()
        }
    }

    // Обработка нажатия на переключатель <Темная тема>
    private fun setNightLight(isChecked: Boolean)
    {
        var appNightMode = AppCompatDelegate.MODE_NIGHT_NO
        if (isChecked)
            appNightMode = AppCompatDelegate.MODE_NIGHT_YES
        AppCompatDelegate.setDefaultNightMode(appNightMode)
        // Сохранить выбранную тему в SharedPreferences
        NightModeStore(this).save(appNightMode)
    }

    // Обработка нажатия кнопки <Поделится приложением>
    private fun shareApp()
    {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
        shareIntent.type = "text/plain"
        startActivity(shareIntent)
    }

    // Обработка нажатия кнопки <Написать в поддержку>
    private fun writeToSupport()
    {
        val mailIntent = Intent(Intent.ACTION_SENDTO)
        mailIntent.data = Uri.parse("mailto:")
        mailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.send_mail)))
        mailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.send_text))
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.send_subject))
        startActivity(mailIntent)
    }

    // Обработка нажатия кнопки <Пользовательское соглашение>
    private fun userAgreement()
    {
        val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.practicum_offer)))
        startActivity(viewIntent)
    }
}
