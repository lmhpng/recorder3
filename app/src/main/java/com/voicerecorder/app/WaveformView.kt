package com.voicerecorder.app

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.sin

class WaveformView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var phase1 = 0f
    private var phase2 = 0f
    private var amplitude = 0f
    private var targetAmplitude = 0f
    private var animating = false

    private val paint1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        strokeCap = Paint.Cap.ROUND
    }
    private val paint2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        strokeCap = Paint.Cap.ROUND
        alpha = 120
    }
    private val path1 = Path()
    private val path2 = Path()

    private val runnable = object : Runnable {
        override fun run() {
            if (!animating) return
            phase1 += 0.08f
            phase2 += 0.05f
            // smooth amplitude transition
            amplitude += (targetAmplitude - amplitude) * 0.15f
            if (amplitude < 0.08f) amplitude = 0.08f
            invalidate()
            postDelayed(this, 16)
        }
    }

    fun startAnimation() {
        animating = true
        targetAmplitude = 0.6f
        post(runnable)
    }

    fun stopAnimation() {
        animating = false
        amplitude = 0f
        targetAmplitude = 0f
        invalidate()
    }

    fun updateAmplitude(maxAmp: Int) {
        targetAmplitude = (maxAmp / 32767f).coerceIn(0.08f, 1f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!animating) return
        val w = width.toFloat()
        val h = height.toFloat()
        val midY = h / 2f
        val maxAmp = midY * 0.85f * amplitude

        // gradient shader
        val shader1 = LinearGradient(0f, 0f, w, 0f,
            intArrayOf(0xFF5B6EF5.toInt(), 0xFF9B5BF5.toInt(), 0xFF5B6EF5.toInt()),
            floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
        paint1.shader = shader1
        val shader2 = LinearGradient(0f, 0f, w, 0f,
            intArrayOf(0x809B5BF5.toInt(), 0x805B6EF5.toInt(), 0x809B5BF5.toInt()),
            floatArrayOf(0f, 0.5f, 1f), Shader.TileMode.CLAMP)
        paint2.shader = shader2

        // wave 1
        path1.reset()
        var first = true
        var x = 0f
        while (x <= w) {
            val y = midY + maxAmp * sin((x / w * 2.5f * Math.PI + phase1).toFloat()).toFloat()
            if (first) { path1.moveTo(x, y); first = false } else path1.lineTo(x, y)
            x += 2f
        }
        canvas.drawPath(path1, paint1)

        // wave 2 (offset)
        path2.reset()
        first = true
        x = 0f
        while (x <= w) {
            val y = midY + maxAmp * 0.6f * sin((x / w * 3f * Math.PI + phase2 + 1.2f).toFloat()).toFloat()
            if (first) { path2.moveTo(x, y); first = false } else path2.lineTo(x, y)
            x += 2f
        }
        canvas.drawPath(path2, paint2)
    }
}
