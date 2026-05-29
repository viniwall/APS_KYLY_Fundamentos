package br.com.kollectaops.collector.domain.service

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundService @Inject constructor(@ApplicationContext private val context: Context) {

    private val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vm.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun beepSuccess() {
        toneGenerator.startTone(ToneGenerator.TONE_DTMF_8, 200)
        vibrate(50)
    }

    fun beepSuccessSkuComplete() {
        toneGenerator.startTone(ToneGenerator.TONE_DTMF_8, 150)
        Thread.sleep(180)
        toneGenerator.startTone(ToneGenerator.TONE_DTMF_8, 150)
        vibrate(50)
    }

    fun beepError() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 2000)
        vibrate(300)
    }

    fun beepBoxFinished() {
        repeat(3) {
            toneGenerator.startTone(ToneGenerator.TONE_DTMF_9, 100)
            Thread.sleep(150)
        }
        vibrate(100)
    }

    // 2 bipes curtos + 1 tom de aviso longo — padrão distinto do beepBoxFinished
    fun beepPartialBox() {
        toneGenerator.startTone(ToneGenerator.TONE_DTMF_9, 100)
        Thread.sleep(150)
        toneGenerator.startTone(ToneGenerator.TONE_DTMF_9, 100)
        Thread.sleep(200)
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 500)
        vibrate(200)
    }

    fun vibrate(durationMs: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }
}
