package com.example.animatedapp

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var girlImage: ImageView
    private lateinit var btnClick: Button
    private lateinit var btnWave: Button
    private lateinit var btnJump: Button
    private lateinit var popUpText: TextView

    private lateinit var clickSound: MediaPlayer
    private lateinit var waveSound: MediaPlayer
    private lateinit var jumpSound: MediaPlayer

    private var currentOutfitIndex = 0
    private var currentExpressionIndex = 0

    private val outfits = arrayOf(
        arrayOf(R.drawable.girl1, R.drawable.girl2),
        arrayOf(R.drawable.girl3, R.drawable.girl4),
        arrayOf(R.drawable.girl5, R.drawable.girl6),
        arrayOf(R.drawable.girl7, R.drawable.girl8)
    )

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var autoChangeRunnable: Runnable
    private lateinit var floatAnim: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        girlImage = findViewById(R.id.girlImage)
        btnClick = findViewById(R.id.btnClick)
        btnWave = findViewById(R.id.btnWave)
        btnJump = findViewById(R.id.btnJump)
        popUpText = findViewById(R.id.popUpText)

        clickSound = MediaPlayer.create(this, R.raw.click)
        waveSound = MediaPlayer.create(this, R.raw.wave)
        jumpSound = MediaPlayer.create(this, R.raw.jump)

        // Floating animation for idle effect
        floatAnim = AnimationUtils.loadAnimation(this, R.anim.floating)
        girlImage.startAnimation(floatAnim)

        // CLICK action
        btnClick.setOnClickListener {
            playAction(R.anim.blink, clickSound, "Click!")
        }

        // WAVE action (pause float, wave, resume float)
        btnWave.setOnClickListener {
            playAction(R.anim.wave, waveSound, "Wave!")
        }

        // JUMP action (pause float, jump, resume float)
        btnJump.setOnClickListener {
            playAction(R.anim.jump, jumpSound, "Jump!")
        }

        // Auto outfit change every 5 seconds
        autoChangeRunnable = object : Runnable {
            override fun run() {
                currentOutfitIndex = (currentOutfitIndex + 1) % outfits.size
                currentExpressionIndex =
                    (currentExpressionIndex + 1) % outfits[currentOutfitIndex].size
                val fade = AnimationUtils.loadAnimation(this@MainActivity, R.anim.fade_in)
                girlImage.startAnimation(fade)
                girlImage.setImageResource(outfits[currentOutfitIndex][currentExpressionIndex])
                handler.postDelayed(this, 2000)
            }
        }
        handler.postDelayed(autoChangeRunnable, 2000)
    }

    // Unified animation handler
    private fun playAction(animRes: Int, sound: MediaPlayer, message: String) {
        // Pause floating temporarily
        girlImage.clearAnimation()

        // Load action animation
        val actionAnim = AnimationUtils.loadAnimation(this, animRes)
        actionAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                sound.start()
                showPopUp(message)
            }

            override fun onAnimationEnd(animation: Animation) {
                // Resume floating
                girlImage.startAnimation(floatAnim)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        girlImage.startAnimation(actionAnim)
    }

    // Pop-up message above image
    private fun showPopUp(message: String) {
        popUpText.text = message
        popUpText.visibility = TextView.VISIBLE
        val popUpAnim = AnimationUtils.loadAnimation(this, R.anim.pop_up)
        popUpText.startAnimation(popUpAnim)
        Handler(Looper.getMainLooper()).postDelayed({
            popUpText.visibility = TextView.GONE
        }, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        clickSound.release()
        waveSound.release()
        jumpSound.release()
        handler.removeCallbacks(autoChangeRunnable)
    }
}
