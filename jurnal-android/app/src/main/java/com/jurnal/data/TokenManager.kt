package com.jurnal.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class TokenManager(context: Context) {
    private val prefs = EncryptedSharedPreferences.create(
        "jurnal_secure",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var token: String
        get() = prefs.getString("gh_token", "") ?: ""
        set(value) = prefs.edit().putString("gh_token", value).apply()

    var repo: String
        get() = prefs.getString("gh_repo", "") ?: ""
        set(value) = prefs.edit().putString("gh_repo", value).apply()

    val isConfigured: Boolean get() = token.isNotBlank() && repo.isNotBlank()
}
