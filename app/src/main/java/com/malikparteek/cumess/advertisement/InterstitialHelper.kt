package com.malikparteek.cumess.advertisement

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object InterstitialHelper {

    private var interstitialAd: InterstitialAd? = null
    private var isAdLoaded = false
    private var isAdShownThisSession = false

    // ✅ Official Google test ID
    private const val TEST_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    fun preload(context: Context, adUnitId: String = TEST_AD_UNIT_ID) {
        if (isAdLoaded || isAdShownThisSession) return

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isAdLoaded = true
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isAdLoaded = false
                }
            }
        )
    }

    fun showOncePerSession(activity: Activity, onDismiss: (() -> Unit)? = null) {
        if (!isAdShownThisSession && isAdLoaded) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    isAdLoaded = false
                    isAdShownThisSession = true
                    preload(activity) // load next one silently
                    onDismiss?.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(err: com.google.android.gms.ads.AdError) {
                    interstitialAd = null
                    isAdLoaded = false
                    onDismiss?.invoke()
                }
            }
            interstitialAd?.show(activity)
        } else {
            onDismiss?.invoke()
        }
    }

    fun resetSession() {
        isAdShownThisSession = false
    }

    fun isLoaded(): Boolean = isAdLoaded
}
