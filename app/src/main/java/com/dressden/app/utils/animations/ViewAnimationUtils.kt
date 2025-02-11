package com.dressden.app.utils.animations

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat

object ViewAnimationUtils {
    private const val DEFAULT_DURATION = 300L

    fun fadeIn(view: View, duration: Long = DEFAULT_DURATION): ViewPropertyAnimatorCompat {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        return ViewCompat.animate(view)
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(DecelerateInterpolator())
    }

    fun fadeOut(view: View, duration: Long = DEFAULT_DURATION): ViewPropertyAnimatorCompat {
        return ViewCompat.animate(view)
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(AccelerateInterpolator())
    }

    fun slideIn(view: View, duration: Long = DEFAULT_DURATION): AnimatorSet {
        val translateAnimator = ObjectAnimator.ofFloat(
            view,
            "translationX",
            view.width.toFloat(),
            0f
        )
        val fadeAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)

        return AnimatorSet().apply {
            playTogether(translateAnimator, fadeAnimator)
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    fun slideOut(view: View, duration: Long = DEFAULT_DURATION): AnimatorSet {
        val translateAnimator = ObjectAnimator.ofFloat(
            view,
            "translationX",
            0f,
            view.width.toFloat()
        )
        val fadeAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)

        return AnimatorSet().apply {
            playTogether(translateAnimator, fadeAnimator)
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    fun scaleIn(view: View, duration: Long = DEFAULT_DURATION): AnimatorSet {
        view.scaleX = 0f
        view.scaleY = 0f
        view.visibility = View.VISIBLE

        val scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f)

        return AnimatorSet().apply {
            playTogether(scaleXAnimator, scaleYAnimator)
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    fun scaleOut(view: View, duration: Long = DEFAULT_DURATION): AnimatorSet {
        val scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f)
        val scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f)

        return AnimatorSet().apply {
            playTogether(scaleXAnimator, scaleYAnimator)
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                }
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
    }

    fun bounce(view: View, duration: Long = DEFAULT_DURATION): AnimatorSet {
        val scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f, 1f)
        val scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f, 1f)

        return AnimatorSet().apply {
            playTogether(scaleXAnimator, scaleYAnimator)
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    fun shake(view: View, duration: Long = DEFAULT_DURATION): ObjectAnimator {
        return ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f).apply {
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    fun rotateIn(view: View, duration: Long = DEFAULT_DURATION): AnimatorSet {
        view.alpha = 0f
        view.rotation = -180f
        view.visibility = View.VISIBLE

        val rotateAnimator = ObjectAnimator.ofFloat(view, "rotation", -180f, 0f)
        val fadeAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)

        return AnimatorSet().apply {
            playTogether(rotateAnimator, fadeAnimator)
            this.duration = duration
            interpolator = AccelerateDecelerateInterpolator()
        }
    }
}
