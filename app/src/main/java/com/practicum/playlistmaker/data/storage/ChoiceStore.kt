package com.practicum.playlistmaker.data.storage

import android.content.Context

class ChoiceStore(context : Context) :  SharedPreferences<Int>(context, SELECTED_TRACK) {
}
