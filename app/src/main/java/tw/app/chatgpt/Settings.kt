package tw.app.chatgpt

import android.content.SharedPreferences

class Settings(
    private val sharedPreferences: SharedPreferences
) {

    private val API_KEY = "API_KEY"

    fun getApiKey(): String? {
        return getString(API_KEY).ifBlank {
            null
        }
    }

    fun setApiKey(apiKey: String) {
        setString(API_KEY, apiKey)
    }

    private fun setString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    private fun getString(key: String): String {
        return sharedPreferences.getString(key, "") ?: ""
    }

}