package com.engtrx.clayedit

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import androidx.media3.ui.PlayerView
import java.io.File

class EditorActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var transformer: Transformer
    private var videoUri: Uri? = null
    private var startMs: Long = 0
    private var endMs: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uriString = intent.getStringExtra("VIDEO_URI")
        if (uriString == null) {
            finish()
            return
        }
        videoUri = Uri.parse(uriString)

        val bgColor = Color.parseColor("#E0E5EC")
        val textColor = Color.parseColor("#3E4A59")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bgColor)
            setPadding(40, 40, 40, 40)
        }

        playerView = PlayerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 800
            )
            val bg = GradientDrawable().apply {
                cornerRadius = 40f
                setColor(Color.parseColor("#2C3E50"))
            }
            background = bg
        }

        // شريط التمرير (للقص)
        val seekBar = SeekBar(this).apply {
            max = 100
            progress = 0
        }

        val trimStartBtn = Button(this).apply {
            text = "تحديد بداية القص"
            setOnClickListener {
                startMs = player.currentPosition
                Toast.makeText(this@EditorActivity, "البداية: ${startMs/1000} ثانية", Toast.LENGTH_SHORT).show()
            }
        }

        val trimEndBtn = Button(this).apply {
            text = "تحديد نهاية القص"
            setOnClickListener {
                endMs = player.currentPosition
                Toast.makeText(this@EditorActivity, "النهاية: ${endMs/1000} ثانية", Toast.LENGTH_SHORT).show()
            }
        }

        val exportBtn = Button(this).apply {
            text = "تصدير الفيديو (Export)"
            textSize = 18f
            setTextColor(Color.WHITE)
            val bg = GradientDrawable().apply {
                cornerRadius = 60f
                setColor(Color.parseColor("#50E3C2"))
                setStroke(4, Color.parseColor("#FFFFFF"))
            }
            background = bg
            setPadding(0, 30, 0, 30)
            setOnClickListener { exportVideo() }
        }

        layout.addView(playerView)
        layout.addView(seekBar)
        layout.addView(trimStartBtn)
        layout.addView(trimEndBtn)
        layout.addView(exportBtn)
        setContentView(layout)

        // تهيئة المشغل
        player = ExoPlayer.Builder(this).build()
        playerView.player = player
        val mediaItem = MediaItem.fromUri(videoUri!!)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        // ربط شريط التمرير بالفيديو
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = player.duration
                    if (duration > 0) {
                        player.seekTo((duration * progress) / 100)
                    }
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // تحديث شريط التمرير تلقائياً
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        val updateSeekBar = object : Runnable {
            override fun run() {
                val duration = player.duration
                if (duration > 0) {
                    seekBar.progress = ((player.currentPosition * 100) / duration).toInt()
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(updateSeekBar)

        // تهيئة محرك التصدير
        transformer = Transformer.Builder(this)
            .setVideoMimeType(MimeTypes.VIDEO_H264)
            .addListener(object : Transformer.Listener {
                override fun onCompleted(composition: androidx.media3.transformer.Composition, exportResult: ExportResult) {
                    runOnUiThread {
                        Toast.makeText(this@EditorActivity, "تم التصدير بنجاح! تجده في مجلد الأفلام", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onError(composition: androidx.media3.transformer.Composition, exportResult: ExportResult, exportException: ExportException) {
                    runOnUiThread {
                        Toast.makeText(this@EditorActivity, "فشل التصدير: ${exportException.message}", Toast.LENGTH_LONG).show()
                    }
                }
            })
            .build()
    }

    private fun exportVideo() {
        if (videoUri == null) return
        
        // إذا لم يحدد المستخدم نهاية القص، نأخذ طول الفيديو كاملاً
        if (endMs == 0L) {
            endMs = player.duration
        }

        Toast.makeText(this, "جاري تصدير الفيديو... انتظر قليلاً", Toast.LENGTH_LONG).show()

        val outputFile = File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), "ClayEdit_${System.currentTimeMillis()}.mp4")
        
        val mediaItem = MediaItem.Builder()
            .setUri(videoUri!!)
            .setClippingConfiguration(
                MediaItem.ClippingConfiguration.Builder()
                    .setStartPositionMs(startMs)
                    .setEndPositionMs(endMs)
                    .build()
            )
            .build()

        transformer.start(mediaItem, outputFile.absolutePath)
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}
