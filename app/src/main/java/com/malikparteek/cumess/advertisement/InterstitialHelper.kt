package com.malikparteek.cumess.advertisement

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.util.*

object InterstitialHelper {

    private var interstitialAd: InterstitialAd? = null

    private const val PREFS_NAME = "ad_prefs"
    private const val KEY_LAST_SHOWN = "last_shown"

    private var isAdShownThisSession = false

    // Load Interstitial Ad
    fun load(
        context: Context,
        adUnitId: String = "ca-app-pub-3940256099942544/1033173712", // Test Ad ID
        onLoaded: (() -> Unit)? = null
    ) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    onLoaded?.invoke()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    // Check if ad is loaded
    fun isAdLoaded(): Boolean = interstitialAd != null

    // Show ad once per day
    fun showIfFirstOpenOfDay(activity: Activity, onDismiss: (() -> Unit)? = null) {
        val prefs = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastShown = prefs.getLong(KEY_LAST_SHOWN, 0L)
        val now = System.currentTimeMillis()

        if (!isSameDay(lastShown, now)) {
            show(activity) {
                prefs.edit().putLong(KEY_LAST_SHOWN, now).apply()
                load(activity) // preload next
                onDismiss?.invoke()
            }
        } else {
            onDismiss?.invoke()
        }
    }

    // Show ad once per app session
    fun showOncePerSession(activity: Activity, onDismiss: (() -> Unit)? = null) {
        if (!isAdShownThisSession) {
            show(activity) {
                isAdShownThisSession = true
                load(activity) // preload next
                onDismiss?.invoke()
            }
        } else {
            onDismiss?.invoke()
        }
    }

    // Internal: actual ad display
    private fun show(activity: Activity, onDismiss: (() -> Unit)? = null) {
        interstitialAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    onDismiss?.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(err: com.google.android.gms.ads.AdError) {
                    interstitialAd = null
                    onDismiss?.invoke()
                }
            }
            ad.show(activity)
        } ?: onDismiss?.invoke()
    }

    private fun isSameDay(time1: Long, time2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    // Optional: Reset session flag on app restart
    fun resetSession() {
        isAdShownThisSession = false
    }
}
