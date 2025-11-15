package com.js_loop_erp.components

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import com.google.android.material.textview.MaterialTextView

class VerticalProgressBar(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var barWidth = 20f // Default width of each step
    private var barHeight = 200f // Default height of progress bar
    private var stepCount = 15 // Default total number of steps
    var currentStep = 5 // Default current step

    private val completeColor = 0xFF00FF00.toInt() // Color of completed steps
    private val incompleteColor = 0xFFCCCCCC.toInt() // Color of incomplete steps

    init {
        orientation = VERTICAL
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setupProgressBar()
    }

    private fun setupProgressBar() {
        removeAllViews()

        val stepHeight = barHeight / stepCount

        for (i in 0 until stepCount) {
            val cardView = CardView(context)
            val layoutParams = LinearLayout.LayoutParams(barWidth.toInt()*100, stepHeight.toInt()).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }

            cardView.layoutParams = layoutParams

            if (i < currentStep) {
                cardView.setCardBackgroundColor(completeColor)
            } else {
                cardView.setCardBackgroundColor(incompleteColor)
            }

            val label = MaterialTextView(context).apply {
                text = "Step ${i + 1}"
                gravity = Gravity.CENTER
            }

            cardView.addView(label)

            addView(cardView)
        }
    }
}

