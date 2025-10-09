
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.ads.*
import androidx.compose.foundation.layout.height

@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adUnitId: String = "ca-app-pub-9714957294087753/8067390727"
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isAdLoaded by remember { mutableStateOf(false) }

    // Create AdView
    val adView = remember {
        AdView(context).apply {
            setAdSize(AdSize.BANNER)
            this.adUnitId = adUnitId
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    isAdLoaded = true
                }
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    isAdLoaded = false
                }
            }
            loadAd(AdRequest.Builder().build())
        }
    }

    // Show only when loaded
    if (isAdLoaded) {
        AndroidView(factory = { adView }, modifier = modifier.height(50.dp))

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> adView.resume()
                    Lifecycle.Event.ON_PAUSE -> adView.pause()
                    Lifecycle.Event.ON_DESTROY -> adView.destroy()
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                adView.destroy()
            }
        }
    }
}
