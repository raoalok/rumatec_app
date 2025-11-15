package com.js_loop_erp.components.fragments

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.widget.Button
import android.widget.Toast


class CustomView(context: Context): View(context), View.OnClickListener {

    private val paint = Paint()
    private var _binding: CustomView? = null

    private lateinit var buttonClick: Button

//    var onClickListener: () -> Unit = {
//        Toast.makeText(context, "1234.....", Toast.LENGTH_SHORT).show()
//    }
    init {
        paint.color = Color.RED
    }

    override fun onDraw(canvas: Canvas){
/*
        Toast.makeText(context, "1234", Toast.LENGTH_SHORT).show()
        //_binding = findViewById(R.id.custom_view__)
        canvas.drawCircle(width/2f, height/2f, 100f,paint)
        rootView.bringToFront()*/
        super.onDraw(canvas)

    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        Toast.makeText(context, "4321", Toast.LENGTH_SHORT).show()
       // _binding = findViewById(R.id.custom_view_text)
    }

    public fun showAToast(view: View){
        Toast.makeText(context, "1234.....", Toast.LENGTH_SHORT).show()

    }

    override fun onClick(p0: View?) {
        //TODO("Not yet implemented")
        Toast.makeText(context, "1234.....", Toast.LENGTH_SHORT).show()
    }

}

