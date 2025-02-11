package com.dressden.app.utils.animations

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import com.dressden.app.R
import com.dressden.app.utils.Constants

object ViewAnimationUtils {

    fun animateFavoriteButton(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.2f, 1f)
        val rotation = ObjectAnimator.ofFloat(view, View.ROTATION, 0f, -15f, 15f, 0f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, rotation)
            duration = Constants.ANIM_DURATION_SHORT
            interpolator = OvershootInterpolator()
            start()
        }
    }

    fun animateAddToCart(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 0.8f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 0.8f, 1f)
        val alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0.5f, 1f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            duration = Constants.ANIM_DURATION_SHORT
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    fun animateItemInsertion(view: View) {
        view.alpha = 0f
        view.translationY = 100f

        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(Constants.ANIM_DURATION_MEDIUM)
            .setInterpolator(OvershootInterpolator())
            .start()
    }

    fun animateItemRemoval(view: View, onComplete: () -> Unit) {
        view.animate()
            .alpha(0f)
            .translationX(view.width.toFloat())
            .setDuration(Constants.ANIM_DURATION_MEDIUM)
            .withEndAction { onComplete() }
            .start()
    }

    fun animateErrorShake(view: View) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.shake_animation)
        view.startAnimation(animation)
    }

    fun animateSuccessCheckmark(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 0f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0f, 1.2f, 1f)
        val rotation = ObjectAnimator.ofFloat(view, View.ROTATION, -45f, 0f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, rotation)
            duration = Constants.ANIM_DURATION_MEDIUM
            interpolator = OvershootInterpolator()
            start()
        }
    }

    fun animateRefresh(view: View) {
        val rotation = ObjectAnimator.ofFloat(view, View.ROTATION, 0f, 360f)
        rotation.duration = Constants.ANIM_DURATION_MEDIUM
        rotation.repeatCount = ObjectAnimator.INFINITE
        rotation.start()
    }

    fun animateExpand(view: View, initialHeight: Int, targetHeight: Int) {
        val heightAnimator = ObjectAnimator.ofInt(view, "height", initialHeight, targetHeight)
        heightAnimator.duration = Constants.ANIM_DURATION_MEDIUM
        heightAnimator.interpolator = AccelerateDecelerateInterpolator()
        heightAnimator.start()
    }

    fun animateCollapse(view: View, initialHeight: Int, targetHeight: Int) {
        val heightAnimator = ObjectAnimator.ofInt(view, "height", initialHeight, targetHeight)
        heightAnimator.duration = Constants.ANIM_DURATION_MEDIUM
        heightAnimator.interpolator = AccelerateDecelerateInterpolator()
        heightAnimator.start()
    }

    fun animatePress(view: View) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(Constants.ANIM_DURATION_SHORT)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(Constants.ANIM_DURATION_SHORT)
                    .start()
            }
            .start()
    }

    fun animateRipple(view: View, x: Float, y: Float) {
        view.isPressed = true
        view.postDelayed({ view.isPressed = false }, Constants.ANIM_DURATION_SHORT)
    }

    fun animateBounce(view: View) {
        val animator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, -20f, 0f)
        animator.duration = Constants.ANIM_DURATION_MEDIUM
        animator.interpolator = OvershootInterpolator()
        animator.start()
    }

    fun animatePulse(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.1f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.1f, 1f)
        val alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0.8f, 1f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            duration = Constants.ANIM_DURATION_LONG
            repeatCount = ObjectAnimator.INFINITE
            start()
        }
    }
}
