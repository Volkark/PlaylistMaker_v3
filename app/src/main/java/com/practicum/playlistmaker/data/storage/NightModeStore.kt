package com.practicum.playlistmaker.data.storage

import android.content.Context

class NightModeStore(context: Context) : SharedPreferences<Int>(context, APP_NIGHT_MODE) {
}
