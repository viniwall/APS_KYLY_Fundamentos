package br.com.kollectaops.collector.presentation.common

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import br.com.kollectaops.collector.R
import br.com.kollectaops.collector.domain.service.SessionManager
import br.com.kollectaops.collector.presentation.home.HomeActivity
import br.com.kollectaops.collector.presentation.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            val target = if (sessionManager.isSessionValid()) HomeActivity::class.java
                         else LoginActivity::class.java
            startActivity(Intent(this, target))
            finish()
        }, 800)
    }
}
