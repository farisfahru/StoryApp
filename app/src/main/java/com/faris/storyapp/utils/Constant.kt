package com.faris.storyapp.utils

import androidx.datastore.preferences.core.stringPreferencesKey

// Edit Text Type Flag
const val EDIT_TEXT_NAME = 1
const val EDIT_TEXT_EMAIL = 2
const val EDIT_TEXT_PASSWORD = 3

// Edit Text Background Flag
const val EDIT_TEXT_BLACK = "black"
const val EDIT_TEXT_GREEN = "green"
const val EDIT_TEXT_RED = "red"

const val preferenceName = "preference_name"
val USER_TOKEN = stringPreferencesKey("userToken")

const val INITIAL_PAGE_INDEX = 1
