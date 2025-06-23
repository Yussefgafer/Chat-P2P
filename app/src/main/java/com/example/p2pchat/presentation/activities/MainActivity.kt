package com.example.p2pchat.presentation.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.p2pchat.R
import com.example.p2pchat.core.animations.AdvancedAnimationManager
import com.example.p2pchat.core.theme.DynamicThemeManager
import com.example.p2pchat.databinding.ActivityMainBinding
import com.example.p2pchat.presentation.adapters.PeerListAdapter
import com.example.p2pchat.presentation.viewmodels.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
// import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
// import javax.inject.Inject

/**
 * Main Activity with advanced UI/UX features
 * Handles peer discovery, connection management, and navigation
 */
// @AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    // @Inject
    private val animationManager = AdvancedAnimationManager()

    // @Inject
    private val themeManager = DynamicThemeManager()

    private lateinit var peerListAdapter: PeerListAdapter

    private val requiredPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    )

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            initializeP2PFeatures()
        } else {
            showPermissionDeniedDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply dynamic theme
        applyDynamicTheme()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()
        checkPermissions()

        // Start with splash animation
        startSplashAnimation()
    }

    private fun applyDynamicTheme() {
        // themeManager.currentTheme.observe(this) { themeData ->
        //     // Apply theme colors to UI elements
        //     window.statusBarColor = themeData.primaryColor
        //     // Additional theme application logic
        // }
    }

    private fun setupUI() {
        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        // Setup RecyclerView for peer list
        peerListAdapter = PeerListAdapter { peer ->
            // Handle peer selection with animation
            animationManager.createRippleEffect(
                binding.recyclerViewPeers,
                binding.recyclerViewPeers.width / 2f,
                binding.recyclerViewPeers.height / 2f
            )

            // Navigate to chat
            navigateToChat(peer)
        }

        binding.recyclerViewPeers.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = peerListAdapter
        }

        // Setup FAB with spring animation
        binding.fabScan.setOnClickListener {
            animationManager.animateFAB(binding.fabScan, false)

            lifecycleScope.launch {
                kotlinx.coroutines.delay(200)
                viewModel.startPeerDiscovery()
                animationManager.animateFAB(binding.fabScan, true)
            }
        }

        // Setup settings button
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // Setup connection status indicator
        setupConnectionStatusIndicator()
    }

    private fun setupConnectionStatusIndicator() {
        binding.connectionStatusIndicator.setOnClickListener {
            showConnectionStatusDialog()
        }
    }

    private fun setupObservers() {
        // Observe peer list
        viewModel.discoveredPeers.observe(this) { peers ->
            peerListAdapter.updatePeers(peers)

            // Animate list items
            animationManager.animateListItems(binding.recyclerViewPeers)

            // Update empty state
            updateEmptyState(peers.isEmpty())
        }

        // Observe connection state
        viewModel.connectionState.observe(this) { state ->
            updateConnectionStatus(state)
        }

        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            updateLoadingState(isLoading)
        }

        // Observe error messages
        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                showErrorSnackbar(it)
                viewModel.clearError()
            }
        }

        // Observe theme changes
        // themeManager.currentTheme.observe(this) { themeData ->
        //     updateUIWithTheme(themeData)
        // }
    }

    private fun updateUIWithTheme(themeData: DynamicThemeManager.ThemeData) {
        // Apply theme colors with smooth transitions
        binding.root.setBackgroundColor(themeData.backgroundColor)
        binding.toolbar.setBackgroundColor(themeData.primaryColor)
        binding.fabScan.backgroundTintList =
            android.content.res.ColorStateList.valueOf(themeData.accentColor)

        // Animate color changes
        val colorAnimator = android.animation.ValueAnimator.ofArgb(
            binding.cardConnectionStatus.cardBackgroundColor.defaultColor,
            themeData.surfaceColor
        )
        colorAnimator.duration = 300
        colorAnimator.addUpdateListener { animator ->
            binding.cardConnectionStatus.setCardBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimator.start()
    }

    private fun startSplashAnimation() {
        // Hide main content initially
        binding.mainContent.alpha = 0f
        binding.mainContent.translationY = 100f

        // Show splash elements
        binding.splashLogo.visibility = View.VISIBLE
        binding.splashTitle.visibility = View.VISIBLE

        // Animate splash
        binding.splashLogo.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(1000)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                // Hide splash and show main content
                hideSplashAndShowMain()
            }
            .start()
    }

    private fun hideSplashAndShowMain() {
        binding.splashLogo.animate()
            .alpha(0f)
            .setDuration(300)
            .start()

        binding.splashTitle.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                binding.splashLogo.visibility = View.GONE
                binding.splashTitle.visibility = View.GONE

                // Show main content with animation
                binding.mainContent.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(500)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }
            .start()
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.recyclerViewPeers.visibility = View.GONE

            // Animate empty state
            binding.emptyStateLayout.alpha = 0f
            binding.emptyStateLayout.animate()
                .alpha(1f)
                .setDuration(300)
                .start()
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.recyclerViewPeers.visibility = View.VISIBLE
        }
    }

    private fun updateConnectionStatus(state: String) {
        binding.textConnectionStatus.text = state

        // Animate connection status with appropriate color
        val isConnected = state.contains("Connected", ignoreCase = true)
        animationManager.animateConnectionStatus(binding.connectionStatusIndicator, isConnected)
    }

    private fun updateLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.fabScan.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.fabScan.isEnabled = true
        }
    }

    private fun checkPermissions() {
        val missingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            permissionLauncher.launch(missingPermissions.toTypedArray())
        } else {
            initializeP2PFeatures()
        }
    }

    private fun initializeP2PFeatures() {
        viewModel.initializeP2P()
        Timber.d("P2P features initialized")
    }

    private fun showPermissionDeniedDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.permissions_required_title)
            .setMessage(R.string.permissions_required_message)
            .setPositiveButton(R.string.grant_permissions) { _, _ ->
                checkPermissions()
            }
            .setNegativeButton(R.string.exit_app) { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun showConnectionStatusDialog() {
        val status = viewModel.getDetailedConnectionStatus()

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.connection_status_title)
            .setMessage(status)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    private fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction(R.string.retry) {
                viewModel.retryLastAction()
            }
            .show()
    }

    private fun navigateToChat(peer: Any) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            // Add peer information to intent
            putExtra("peer_id", peer.toString())
        }

        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshPeerList()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pauseDiscovery()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cleanup()
    }
}
