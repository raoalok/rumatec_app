package com.js_loop_erp.components.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.findViewTreeOnBackPressedDispatcherOwner
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.textview.MaterialTextView
import  com.js_loop_erp.components.R
import  com.js_loop_erp.components.VerticalProgressBar
import  com.js_loop_erp.components.databinding.TripPlanProgressFragmentBinding


class TripPlanProgressFragment : DialogFragment() {

    private var _binding: TripPlanProgressFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var verticalProgressBar: VerticalProgressBar
    private lateinit var button: Button

    override fun  onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ChildDialogGreyTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding =  TripPlanProgressFragmentBinding.inflate(inflater,container, false)

        getDialog()?.setTitle("Trip Plan Update Progress Form")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view,savedInstanceState)

        button = view.findViewById<Button>(R.id.button)
        //verticalProgressBar = view.findViewById(R.id.verticalProgressBar)

        // Customize the vertical progress bar if needed
        // For example, you can set the current step:
        //verticalProgressBar.currentStep = 3 // Example value
        val verticalProgressBarLayout: LinearLayout = binding.verticalProgressBar
        //addStepCardViews(verticalProgressBarLayout)
        //val stepTexts = listOf("Step 1: Start", "Step 2: Middle", "Step 3: End") // Customize step texts
        //addStepCardViews(verticalProgressBarLayout, 5, stepTexts)

        button.setOnClickListener {
            //val stepTexts = listOf("Step 1: Start", "Step 2: Middle", "Step 3: End") // Customize step texts
            //addStepCardViews(verticalProgressBarLayout, 2, stepTexts)
        }

        view.rootView.findViewTreeOnBackPressedDispatcherOwner()?.onBackPressedDispatcher?.addCallback {
            dismiss()
        }

    }

    private fun addStepCardViews(layout: LinearLayout, currentStep: Int, stepTexts: List<String>) {
        layout.removeAllViews()
        val stepsCount = stepTexts.size // Get the total number of steps from the list size

        for (i in 0 until stepsCount) {
            val cardView = CardView(requireContext())
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, 16) // Adjust margins as needed
            cardView.layoutParams = params
            cardView.cardElevation = 4f // Adjust elevation as needed
            cardView.radius = 8f // Adjust corner radius as needed

            val linearLayout = LinearLayout(requireContext())
            linearLayout.orientation = LinearLayout.HORIZONTAL
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            linearLayout.gravity = Gravity.LEFT
            linearLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.success))

            val verticalBar = View(requireContext())
            val barParams = LinearLayout.LayoutParams(
                20, // Width of the vertical bar
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            verticalBar.layoutParams = barParams
            verticalBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            linearLayout.addView(verticalBar)

            // Create ImageView
            val imageView = AppCompatImageView(requireContext())
            val imageParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            imageView.layoutParams = imageParams
            imageView.setImageResource(if (i < currentStep) R.drawable.baseline_add_24 else R.drawable.baseline_delete_24) // Set the image based on the current step
            imageView.setPadding(8, 8, 8, 8) // Adjust padding as needed
            imageView.setOnClickListener {
                // Handle click on the image
                // For example, launch an activity or perform some action
                Toast.makeText(requireContext(), "Image $i clicked", Toast.LENGTH_SHORT).show()
            }
            linearLayout.addView(imageView)

            // Create TextView for custom step text
            val textView = MaterialTextView(requireContext())
            val textParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            textParams.marginStart = 16 // Adjust margin as needed
            textView.layoutParams = textParams
            textView.gravity = Gravity.CENTER_VERTICAL
            textView.setPadding(8, 8, 8, 8) // Adjust padding as needed
            textView.text = HtmlCompat.fromHtml(stepTexts[i], HtmlCompat.FROM_HTML_MODE_COMPACT)

            linearLayout.addView(textView)
            // Create the vertical bar

            cardView.addView(linearLayout)
            layout.addView(cardView)
        }
    }


}