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
import com.quickpoint.snookerboard.utils.activity
import timber.log.Timber

class AdMob(private val context: Context) {

    var mInterstitialAd: InterstitialAd? = null

    fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(context, context.getString(R.string.ad_mob_id_interstitial_end_of_game), adRequest, object : InterstitialAdLoadCallback() {
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

    fun interstitialAdSetContentCallbacks() {
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() { // Called when a click is recorded for an ad.
                Timber.i("Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() { // Called when ad is dismissed.
                Timber.i("Ad dismissed fullscreen content.")
                mInterstitialAd = null
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
                loadInterstitialAd()
            }
        }
    }

    fun showInterstitialAd() {
        if (mInterstitialAd != null && !BuildConfig.DEBUG_TOGGLE && shouldAdShow()) {
            mInterstitialAd?.show(context.activity()!!)
        } else {
            Timber.i("The interstitial ad wasn't ready yet.")
        }
    }
}

fun shouldAdShow() = (1..100).random() <= 25