package com.aw.forcement.api

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.*

object TextToSpeechUtil : TextToSpeech.OnInitListener {

    private lateinit var textToSpeech: TextToSpeech
    private var isInitialized = false
    private var onInitCallback: (() -> Unit)? = null

    fun initialize(context: Context, onInitCallback: () -> Unit) {
        this.onInitCallback = onInitCallback
        textToSpeech = TextToSpeech(context, this)
    }

    fun speak(text: String) {
        if (isInitialized) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                @Suppress("DEPRECATION")
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                // Handle language data missing or not supported
                // You can prompt the user to install the necessary language data
            } else {
                // Text-to-speech is initialized successfully
                isInitialized = true
                onInitCallback?.invoke()
            }
        } else {
            // Handle initialization failure
        }
    }

    fun setOnUtteranceProgressListener(listener: UtteranceProgressListener) {
        if (isInitialized) {
            textToSpeech.setOnUtteranceProgressListener(listener)
        }
    }

    fun shutdown() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
            isInitialized = false
        }
    }
}
