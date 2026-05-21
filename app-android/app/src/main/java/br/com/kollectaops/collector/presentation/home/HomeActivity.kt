package br.com.kollectaops.collector.presentation.home

import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import br.com.kollectaops.collector.R
import br.com.kollectaops.collector.databinding.ActivityHomeBinding
import br.com.kollectaops.collector.domain.service.SessionManager
import br.com.kollectaops.collector.presentation.login.LoginActivity
import br.com.kollectaops.collector.presentation.picking.OpenBoxActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    @Inject lateinit var sessionManager: SessionManager

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        setupDrawer()
        setupContextBlock()
        setupModuleGrid()
        setupNetworkMonitor()
    }

    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.nav_open, R.string.nav_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_picking -> {
                    startActivity(Intent(this, OpenBoxActivity::class.java))
                }
                R.id.nav_logout -> {
                    sessionManager.clear()
                    startActivity(Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupContextBlock() {
        val nome = sessionManager.getUserName()
        val filial = sessionManager.getFilialName()
        binding.tvContextInfo.text = getString(R.string.context_info_format, nome, filial)
    }

    private fun setupModuleGrid() {
        val modules = listOf(
            HomeModule(R.string.module_picking, R.drawable.ic_module_picking) {
                startActivity(Intent(this, OpenBoxActivity::class.java))
            }
        )
        val adapter = HomeModuleAdapter(modules)
        binding.rvModules.adapter = adapter
    }

    private fun setupNetworkMonitor() {
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                runOnUiThread { updateNetworkStatus(true) }
            }
            override fun onLost(network: Network) {
                runOnUiThread { updateNetworkStatus(false) }
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)

        val isOnline = connectivityManager.getNetworkCapabilities(
            connectivityManager.activeNetwork
        )?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        updateNetworkStatus(isOnline)
    }

    private fun updateNetworkStatus(online: Boolean) {
        if (online) {
            binding.tvNetworkStatus.text = getString(R.string.status_online)
            binding.tvNetworkStatus.setTextColor(getColor(R.color.success))
        } else {
            binding.tvNetworkStatus.text = getString(R.string.status_offline)
            binding.tvNetworkStatus.setTextColor(getColor(R.color.error))
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        sessionManager.updateLastActivity()
        if (!sessionManager.isSessionValid()) {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
