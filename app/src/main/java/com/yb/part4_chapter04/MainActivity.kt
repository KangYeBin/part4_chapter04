package com.yb.part4_chapter04

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.AppBarLayout
import com.yb.part4_chapter04.databinding.ActivityMainBinding
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    val activityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var isGatheringMotionAnimating: Boolean = false
    private var isCurationMotionAnimating: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        makeStatusBarTransparent()
        initAppBar()
        initInsetMargin()
        initScrollViewListeners()
        initMotionLayoutListener()


    }

    private fun initScrollViewListeners() {
        activityMainBinding.scrollView.smoothScrollTo(0, 0)

        activityMainBinding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrolledValue = activityMainBinding.scrollView.scrollY
            Log.d("스크롤", "$scrolledValue")
            Log.d("스크롤하이트", activityMainBinding.scrollView.height.toString())

            if (scrolledValue > 150f.dpToPx(this@MainActivity).toInt()) {
                if (isGatheringMotionAnimating.not()) {
                    activityMainBinding.gatheringDigitalThingsMotionLayout.transitionToEnd()
                    activityMainBinding.gatheringDigitalThingsBackgroundMotionLayout.transitionToEnd()
                    activityMainBinding.buttonShownMotionLayout.transitionToEnd()
                }
            } else {
                if (isGatheringMotionAnimating.not()) {
                    activityMainBinding.gatheringDigitalThingsMotionLayout.transitionToStart()
                    activityMainBinding.gatheringDigitalThingsBackgroundMotionLayout.transitionToStart()
                    activityMainBinding.buttonShownMotionLayout.transitionToStart()
                }
            }

            if (scrolledValue >= activityMainBinding.scrollView.height-150) {
                if (isCurationMotionAnimating.not()) {
                    activityMainBinding.curationAnimationMotionLayout.setTransition(R.id.curation_animation_start1, R.id.curation_animation_end1)
                    activityMainBinding.curationAnimationMotionLayout.transitionToEnd()
                    isCurationMotionAnimating = true
                }
            }
        }
    }

    private fun initMotionLayoutListener() {
        activityMainBinding.gatheringDigitalThingsMotionLayout.setTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
            ) {
                isGatheringMotionAnimating = true
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float,
            ) {
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                isGatheringMotionAnimating = false
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float,
            ) {
            }

        })

        activityMainBinding.curationAnimationMotionLayout.setTransitionListener(object :
            MotionLayout.TransitionListener {
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
            ) = Unit

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float,
            ) {
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                when (currentId) {
                    R.id.curation_animation_end1 -> {
                        activityMainBinding.curationAnimationMotionLayout.setTransition(R.id.curation_animation_start2, R.id.curation_animation_end2)
                        activityMainBinding.curationAnimationMotionLayout.transitionToEnd()
                    }
                }
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float,
            ) {
            }

        })

    }

    //스크롤 했을 때 alpha 값 지정
    private fun initAppBar() {
        activityMainBinding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val topPadding = 280f.dpToPx(this)
            val realAlphaScrollHeight = appBarLayout.measuredHeight - appBarLayout.totalScrollRange
            val abstractOffset = abs(verticalOffset)

            val realAlphaVerticalOffset =
                if (abstractOffset - topPadding < 0) 0f else abstractOffset - topPadding

            if (abstractOffset < topPadding) {
                activityMainBinding.toolbarBackgroundView.alpha = 0f
                return@OnOffsetChangedListener
            }
            val percentage = realAlphaVerticalOffset / realAlphaScrollHeight
            activityMainBinding.toolbarBackgroundView.alpha =
                1 - (if (1 - percentage * 2 < 0) 0f else 1 - percentage * 2)
        })
        initActionBar()
    }

    private fun initActionBar() = with(activityMainBinding) {
        toolbar.navigationIcon = null
        toolbar.setContentInsetsAbsolute(0, 0)
        setSupportActionBar(activityMainBinding.toolbar)
        supportActionBar?.let {
            it.setHomeButtonEnabled(false)
            it.setDisplayHomeAsUpEnabled(false)
            it.setDisplayShowHomeEnabled(false)
        }
    }

    private fun initInsetMargin() = with(activityMainBinding) {
        ViewCompat.setOnApplyWindowInsetsListener(coordinator) { v: View, insets: WindowInsetsCompat ->
            val params = v.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = insets.systemWindowInsetBottom
            toolbarContainer.layoutParams =
                (toolbarContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    setMargins(0, insets.systemWindowInsetTop, 0, 0)
                }
            collapsingToolbarContainer.layoutParams =
                (collapsingToolbarContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    setMargins(0, 0, 0, 0)
                }

            insets.consumeSystemWindowInsets()
        }
    }

    private fun Float.dpToPx(context: Context): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            this,
            context.resources.displayMetrics)

    private fun makeStatusBarTransparent() {
        with(window) {
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
    }
}