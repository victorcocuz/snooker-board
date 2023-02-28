package com.quickpoint.snookerboard.admob

import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.quickpoint.snookerboard.BuildConfig
import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.utils.getActivity
import timber.log.Timber

var mInterstitialAd: InterstitialAd? = null

fun loadInterstitialAd(context: Context) {
    InterstitialAd.load(context,
        context.getString(if (BuildConfig.DEBUG_TOGGLE) R.string.admob_id_interstitial_test else R.string.admob_id_interstitial),
        AdRequest.Builder().build(),
        object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Timber.e(adError.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Timber.i("Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
}

fun interstitialAdSetContentCallbacks(context: Context) {
    mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdClicked() { // Called when a click is recorded for an ad.
            Timber.i("Ad was clicked.")
        }

        override fun onAdDismissedFullScreenContent() { // Called when ad is dismissed.
            Timber.i("Ad dismissed fullscreen content.")
            mInterstitialAd = null
            loadInterstitialAd(context)
        }

        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
            super.onAdFailedToShowFullScreenContent(p0)
            Timber.e("Ad failed to show fullscreen content.")
            mInterstitialAd = null

        }

        override fun onAdImpression() { // Called when an impression is recorded for an ad.
            Timber.i("Ad recorded an impression.")
        }

        override fun onAdShowedFullScreenContent() { // Called when ad is shown.
            Timber.i("Ad showed fullscreen content.")
        }
    }
}

fun showInterstitialAd(context: Context) {
    val activity = context.getActivity()

    if (mInterstitialAd != null && activity != null && shouldAdShow()) {
        interstitialAdSetContentCallbacks(context)
        mInterstitialAd?.show(activity)
    } else {
        Timber.i("The interstitial ad wasn't ready yet.")
    }
}

fun shouldAdShow() = (1..100).random() <= BuildConfig.ADS_SHOW_PERCENTAGE