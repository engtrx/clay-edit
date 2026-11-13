package com.engtrx.clayedit

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
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView

    private val pickVideo = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            playVideo(uri)
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
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val title = TextView(this).apply {
            text = "Clay Edit"
            textSize = 32f
            setTextColor(textColor)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 60)
        }

        playerView = PlayerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 600
            ).apply {
                setMargins(0, 0, 0, 60)
            }
            setBackgroundColor(Color.BLACK)
            val bg = GradientDrawable().apply {
                cornerRadius = 50f
                setColor(Color.parseColor("#2C3E50"))
            }
            background = bg
        }

        val importBtn = Button(this).apply {
            text = "استيراد فيديو"
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

        val exportBtn = Button(this).apply {
            text = "حفظ التعديلات (تصدير)"
            textSize = 18f
            setTextColor(Color.WHITE)
            val bg = GradientDrawable().apply {
                cornerRadius = 60f
                setColor(Color.parseColor("#50E3C2"))
                setStroke(4, Color.parseColor("#FFFFFF"))
            }
            background = bg
            setPadding(0, 40, 0, 40)
            setOnClickListener {
                Toast.makeText(this@MainActivity, "قريباً: سيتم تصدير الفيديو هنا!", Toast.LENGTH_SHORT).show()
            }
        }

        layout.addView(title)
        layout.addView(playerView)
        layout.addView(importBtn)
        layout.addView(TextView(this).apply { setPadding(0, 20, 0, 20) })
        layout.addView(exportBtn)

        setContentView(layout)

        player = ExoPlayer.Builder(this).build()
        playerView.player = player
    }

    private fun playVideo(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
        Toast.makeText(this, "تم تحميل الفيديو بنجاح", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}
