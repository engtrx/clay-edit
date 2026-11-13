package com.engtrx.clayedit

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val pickVideo = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            // الانتقال لشاشة المونتاج
            val intent = Intent(this, EditorActivity::class.java)
            intent.putExtra("VIDEO_URI", uri.toString())
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bgColor = Color.parseColor("#E0E5EC")
        val textColor = Color.parseColor("#3E4A59")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bgColor)
            setPadding(60, 80, 60, 80)
            gravity = Gravity.CENTER
        }

        val title = TextView(this).apply {
            text = "Clay Pro Edit"
            textSize = 32f
            setTextColor(textColor)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 60)
        }

        val importBtn = Button(this).apply {
            text = "استيراد فيديو للمونتاج"
            textSize = 18f
            setTextColor(Color.WHITE)
            val bg = GradientDrawable().apply {
                cornerRadius = 60f
                setColor(Color.parseColor("#4A90E2"))
                setStroke(4, Color.parseColor("#FFFFFF"))
            }
            background = bg
            setPadding(0, 40, 0, 40)
            setOnClickListener { pickVideo.launch("video/*") }
        }

        layout.addView(title)
        layout.addView(importBtn)
        setContentView(layout)
    }
}
