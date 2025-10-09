package com.example.app

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.review.ReviewManagerFactory

object ReviewManagerHelper {

    private const val PREFS_NAME = "review_prefs"
    private const val KEY_LAUNCH_COUNT = "launch_count"
    private const val KEY_REVIEW_SHOWN = "review_shown"

    fun incrementLaunchCount(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val newCount = prefs.getInt(KEY_LAUNCH_COUNT, 0) + 1
        prefs.edit().putInt(KEY_LAUNCH_COUNT, newCount).apply()
    }

    fun shouldShowReview(context: Context, requiredLaunches: Int = 10): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val launchCount = prefs.getInt(KEY_LAUNCH_COUNT, 0)
        val reviewShown = prefs.getBoolean(KEY_REVIEW_SHOWN, false)
        return launchCount >= requiredLaunches && !reviewShown
    }

    fun markReviewShown(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_REVIEW_SHOWN, true).apply()
    }

    fun showInAppReview(activity: Activity, onComplete: (() -> Unit)? = null) {
        val manager = ReviewManagerFactory.create(activity)
        val request = manager.requestReviewFlow()

        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener {
                    Log.d("InAppReview", "Review flow finished")
                    markReviewShown(activity) // mark as shown
                    onComplete?.invoke()
                }
            } else {
                Log.e("InAppReview", "Review request failed")
                onComplete?.invoke()
            }
        }
    }
}
