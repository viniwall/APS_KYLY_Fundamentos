package br.com.kollectaops.collector.domain.service

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {

    companion object {
        private const val PREFS_NAME = "kollecta_secure_prefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_CRACHA = "cracha"
        private const val KEY_FILIAL_ID = "filial_id"
        private const val KEY_FILIAL_NAME = "filial_name"
        private const val KEY_LAST_ACTIVITY = "last_activity"
        private const val SESSION_TIMEOUT_MS = 60 * 60 * 1000L // 60 minutos
    }

    private val prefs: SharedPreferences = try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context, PREFS_NAME, masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveSession(token: String, userId: Long, nome: String, cracha: String, filialId: Long, filialNome: String) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putLong(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, nome)
            putString(KEY_CRACHA, cracha)
            putLong(KEY_FILIAL_ID, filialId)
            putString(KEY_FILIAL_NAME, filialNome)
            putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis())
        }.apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, -1L)

    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "") ?: ""

    fun getFilialName(): String = prefs.getString(KEY_FILIAL_NAME, "") ?: ""

    fun getCracha(): String = prefs.getString(KEY_CRACHA, "") ?: ""

    fun updateLastActivity() {
        prefs.edit().putLong(KEY_LAST_ACTIVITY, System.currentTimeMillis()).apply()
    }

    fun isSessionValid(): Boolean {
        val token = getToken() ?: return false
        if (token.isEmpty()) return false
        val lastActivity = prefs.getLong(KEY_LAST_ACTIVITY, 0L)
        return (System.currentTimeMillis() - lastActivity) < SESSION_TIMEOUT_MS
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
