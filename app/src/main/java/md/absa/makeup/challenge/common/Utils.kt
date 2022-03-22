package md.absa.makeup.challenge.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.view.Window
import android.view.WindowManager
import androidx.core.view.WindowCompat

object Utils {

    /**
     * Check connectivity
     */
    fun checkInternetConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }

    /**
     * Coloring the status bar
     */
    fun setStatusBarColor(window: Window, color: Int, fitWindow: Boolean) {
        window.apply {
            when {
                Build.VERSION.SDK_INT in 21..29 -> {
                    WindowCompat.setDecorFitsSystemWindows(window, fitWindow)
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                }
                Build.VERSION.SDK_INT >= 30 -> {
                    WindowCompat.setDecorFitsSystemWindows(window, fitWindow)
                }
                else -> {
                }
            }
            statusBarColor = color
        }
    }
}
