package com.malikparteek.cumess.advertisement

import android.content.Context
import android.view.LayoutInflater
import android.widget.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.malikparteek.cumess.R

@Composable
fun NativeAdComposable(context: Context) {
    AndroidView(
        factory = { ctx ->
            val frameLayout = FrameLayout(ctx)

            val adLoader = AdLoader.Builder(ctx, "ca-app-pub-9714957294087753/5435137343")
                .forNativeAd { nativeAd ->
                    val inflater = LayoutInflater.from(ctx)
                    val adView = inflater.inflate(R.layout.native_ad_layout, null) as NativeAdView

                    // Bind views
                    val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
                    val iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)
                    val callToActionView = adView.findViewById<Button>(R.id.ad_call_to_action)

                    // Assign views
                    headlineView.text = nativeAd.headline
                    callToActionView.text = nativeAd.callToAction
                    nativeAd.icon?.drawable?.let {
                        iconView.setImageDrawable(it)
                        adView.iconView = iconView
                    }

                    adView.headlineView = headlineView
                    adView.callToActionView = callToActionView

                    adView.setNativeAd(nativeAd)

                    // Add to layout
                    frameLayout.removeAllViews()
                    frameLayout.addView(adView)
                }
                .withAdListener(object : AdListener() {
                    // Removed logging
                })
                .build()

            adLoader.loadAd(AdRequest.Builder().build())

            frameLayout
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    )
}
