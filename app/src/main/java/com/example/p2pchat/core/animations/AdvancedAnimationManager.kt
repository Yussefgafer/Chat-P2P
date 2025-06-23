package com.example.p2pchat.core.animations

import android.animation.*
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.airbnb.lottie.LottieAnimationView
// import javax.inject.Inject
// import javax.inject.Singleton
import kotlin.math.*

/**
 * Advanced Animation Manager for sophisticated UI animations
 * Includes 3D effects, particle systems, and dynamic interactions
 */
// @Singleton
class AdvancedAnimationManager /* @Inject constructor() */ {

    companion object {
        private const val DEFAULT_DURATION = 300L
        private const val SPRING_DAMPING_RATIO = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
        private const val SPRING_STIFFNESS = SpringForce.STIFFNESS_LOW
    }

    /**
     * 3D Card Flip Animation
     */
    fun flipCard(frontView: View, backView: View, duration: Long = DEFAULT_DURATION) {
        val context = frontView.context
        val scale = context.resources.displayMetrics.density
        val cameraDist = 8000 * scale

        frontView.cameraDistance = cameraDist
        backView.cameraDistance = cameraDist

        val flipOut = ObjectAnimator.ofFloat(frontView, "rotationY", 0f, 90f).apply {
            this.duration = duration / 2
            interpolator = AccelerateInterpolator()
        }

        val flipIn = ObjectAnimator.ofFloat(backView, "rotationY", -90f, 0f).apply {
            this.duration = duration / 2
            interpolator = DecelerateInterpolator()
        }

        flipOut.doOnEnd {
            frontView.visibility = View.GONE
            backView.visibility = View.VISIBLE
            flipIn.start()
        }

        flipOut.start()
    }

    /**
     * Morphing Transition between views
     */
    fun morphViews(fromView: View, toView: View, duration: Long = DEFAULT_DURATION) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0f, 1f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f, 1f)

        val morphAnimator = ObjectAnimator.ofPropertyValuesHolder(fromView, scaleX, scaleY, alpha).apply {
            this.duration = duration
            interpolator = FastOutSlowInInterpolator()
        }

        morphAnimator.addUpdateListener { animator ->
            val fraction = animator.animatedFraction
            if (fraction >= 0.5f && toView.visibility != View.VISIBLE) {
                fromView.visibility = View.GONE
                toView.visibility = View.VISIBLE
                toView.scaleX = 0f
                toView.scaleY = 0f
                toView.alpha = 0f

                ObjectAnimator.ofPropertyValuesHolder(
                    toView,
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 1f),
                    PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
                ).apply {
                    duration = this@apply.duration / 2
                    interpolator = DecelerateInterpolator()
                }.start()
            }
        }

        morphAnimator.start()
    }

    /**
     * Parallax Scrolling Effect
     */
    fun createParallaxEffect(backgroundView: View, foregroundView: View, scrollOffset: Float) {
        val parallaxFactor = 0.5f
        backgroundView.translationY = scrollOffset * parallaxFactor
        foregroundView.translationY = scrollOffset
    }

    /**
     * Ripple Effect Animation
     */
    fun createRippleEffect(view: View, centerX: Float, centerY: Float) {
        val maxRadius = max(
            max(centerX, view.width - centerX),
            max(centerY, view.height - centerY)
        )

        val rippleAnimator = ValueAnimator.ofFloat(0f, maxRadius).apply {
            duration = 600L
            interpolator = FastOutSlowInInterpolator()
        }

        rippleAnimator.addUpdateListener { animator ->
            val radius = animator.animatedValue as Float
            val alpha = 1f - animator.animatedFraction

            // Custom ripple drawing would be implemented in a custom view
            view.invalidate()
        }

        rippleAnimator.start()
    }

    /**
     * Floating Action Button with Spring Animation
     */
    fun animateFAB(fab: View, show: Boolean) {
        val springAnim = SpringAnimation(fab, DynamicAnimation.SCALE_X).apply {
            spring = SpringForce().apply {
                finalPosition = if (show) 1f else 0f
                dampingRatio = SPRING_DAMPING_RATIO
                stiffness = SPRING_STIFFNESS
            }
        }

        val springAnimY = SpringAnimation(fab, DynamicAnimation.SCALE_Y).apply {
            spring = SpringForce().apply {
                finalPosition = if (show) 1f else 0f
                dampingRatio = SPRING_DAMPING_RATIO
                stiffness = SPRING_STIFFNESS
            }
        }

        springAnim.start()
        springAnimY.start()
    }

    /**
     * Message Bubble Animation
     */
    fun animateMessageBubble(messageView: View, isIncoming: Boolean) {
        val startX = if (isIncoming) -messageView.width.toFloat() else messageView.width.toFloat()

        messageView.apply {
            translationX = startX
            alpha = 0f
            scaleX = 0.8f
            scaleY = 0.8f
        }

        val animatorSet = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(messageView, View.TRANSLATION_X, startX, 0f),
                ObjectAnimator.ofFloat(messageView, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(messageView, View.SCALE_X, 0.8f, 1f),
                ObjectAnimator.ofFloat(messageView, View.SCALE_Y, 0.8f, 1f)
            )
            duration = 400L
            interpolator = OvershootInterpolator(1.2f)
        }

        animatorSet.start()
    }

    /**
     * Typing Indicator Animation
     */
    fun animateTypingIndicator(dot1: View, dot2: View, dot3: View): Animator {
        val animatorSet = AnimatorSet()

        val bounce1 = createBounceAnimation(dot1, 0L)
        val bounce2 = createBounceAnimation(dot2, 150L)
        val bounce3 = createBounceAnimation(dot3, 300L)

        animatorSet.playTogether(bounce1, bounce2, bounce3)
        return animatorSet
    }

    private fun createBounceAnimation(view: View, startDelay: Long): Animator {
        return ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, -20f, 0f).apply {
            duration = 600L
            startDelay = startDelay
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    /**
     * Connection Status Animation
     */
    fun animateConnectionStatus(statusView: View, isConnected: Boolean) {
        val pulseAnimator = ObjectAnimator.ofFloat(statusView, View.ALPHA, 1f, 0.3f, 1f).apply {
            duration = 1000L
            repeatCount = if (isConnected) 0 else ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
        }

        val colorAnimator = if (isConnected) {
            // Green pulse for connected
            ValueAnimator.ofArgb(0xFF00FF00.toInt(), 0xFF00AA00.toInt(), 0xFF00FF00.toInt())
        } else {
            // Red pulse for disconnected
            ValueAnimator.ofArgb(0xFFFF0000.toInt(), 0xFFAA0000.toInt(), 0xFFFF0000.toInt())
        }.apply {
            duration = 1000L
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            addUpdateListener { animator ->
                statusView.setBackgroundColor(animator.animatedValue as Int)
            }
        }

        AnimatorSet().apply {
            playTogether(pulseAnimator, colorAnimator)
            start()
        }
    }

    /**
     * Particle Effect for Message Sending
     */
    fun createParticleEffect(container: ViewGroup, startX: Float, startY: Float) {
        val particleCount = 20
        val particles = mutableListOf<View>()

        repeat(particleCount) {
            val particle = View(container.context).apply {
                setBackgroundResource(android.R.drawable.ic_menu_send)
                layoutParams = ViewGroup.LayoutParams(8, 8)
                x = startX
                y = startY
                alpha = 0.8f
            }

            container.addView(particle)
            particles.add(particle)

            // Random direction and distance
            val angle = Random().nextFloat() * 2 * PI
            val distance = 100 + Random().nextFloat() * 200
            val endX = startX + cos(angle).toFloat() * distance
            val endY = startY + sin(angle).toFloat() * distance

            val animatorSet = AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(particle, View.X, startX, endX),
                    ObjectAnimator.ofFloat(particle, View.Y, startY, endY),
                    ObjectAnimator.ofFloat(particle, View.ALPHA, 0.8f, 0f),
                    ObjectAnimator.ofFloat(particle, View.SCALE_X, 1f, 0f),
                    ObjectAnimator.ofFloat(particle, View.SCALE_Y, 1f, 0f)
                )
                duration = 1000L + Random().nextLong() % 500L
                interpolator = DecelerateInterpolator()
            }

            animatorSet.doOnEnd {
                container.removeView(particle)
            }

            animatorSet.start()
        }
    }

    /**
     * Screen Transition with Shared Elements
     */
    fun createSharedElementTransition(sharedElement: View, duration: Long = DEFAULT_DURATION) {
        val scaleX = ObjectAnimator.ofFloat(sharedElement, View.SCALE_X, 1f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(sharedElement, View.SCALE_Y, 1f, 1.2f, 1f)
        val rotation = ObjectAnimator.ofFloat(sharedElement, View.ROTATION, 0f, 360f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, rotation)
            this.duration = duration
            interpolator = FastOutSlowInInterpolator()
            start()
        }
    }

    /**
     * Lottie Animation Helper
     */
    fun playLottieAnimation(lottieView: LottieAnimationView, animationRes: String, loop: Boolean = false) {
        lottieView.apply {
            setAnimation(animationRes)
            if (loop) {
                repeatCount = LottieAnimationView.INFINITE
            }
            playAnimation()
        }
    }

    /**
     * Staggered List Animation
     */
    fun animateListItems(container: ViewGroup, staggerDelay: Long = 100L) {
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i)
            child.apply {
                alpha = 0f
                translationY = 100f
            }

            ObjectAnimator.ofFloat(child, View.ALPHA, 0f, 1f).apply {
                duration = 300L
                startDelay = i * staggerDelay
                start()
            }

            ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, 100f, 0f).apply {
                duration = 400L
                startDelay = i * staggerDelay
                interpolator = DecelerateInterpolator()
                start()
            }
        }
    }
}
