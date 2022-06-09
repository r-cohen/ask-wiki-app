package com.r.cohen.askwiki

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.Html
import android.util.Log
import androidx.annotation.UiThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.r.cohen.askwiki.models.CycleState
import com.r.cohen.askwiki.repos.wikipedia.WikiApi
import com.r.cohen.askwiki.repos.witai.WitAiApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

private const val tag = "vm"

class MainViewModel: ViewModel() {
    private var languageLocale = Locale.US
    private var ttsInitialized = false

    lateinit var resources: Resources
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    private lateinit var tts: TextToSpeech
    private var ttsListener = object: UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {
            state.postValue(CycleState.SPEAKING)
        }

        override fun onDone(utteranceId: String?) {
            state.postValue(CycleState.IDLE)
        }

        @Deprecated("Deprecated in Java", ReplaceWith("isSpeaking.postValue(false)"))
        override fun onError(utteranceId: String?) {
            state.postValue(CycleState.IDLE)
        }
    }

    val recognizingSpeech = MutableLiveData(false)
    val recognizedText = MutableLiveData("")
    val backgroundColor = MutableLiveData(getRandomColor())
    val state = MutableLiveData(CycleState.IDLE)


    fun startSpeechRecognition() {
        recognizedText.postValue("")
        recognizingSpeech.postValue(true)
        speechRecognizer.startListening(recognizerIntent)
    }

    fun stopSpeechRecognition() {
        recognizingSpeech.postValue(false)
        speechRecognizer.stopListening()
    }

    private fun getRandomColor(): Int = Color.argb(
        255,
        Random().nextInt(256),
        Random().nextInt(256),
        Random().nextInt(256))

    fun initSpeechRecognizer(recognizer: SpeechRecognizer) {
        speechRecognizer = recognizer
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        with(recognizerIntent) {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageLocale)
        }

        speechRecognizer.setRecognitionListener(object: RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}

            override fun onError(error: Int) {
                Log.e(tag, "speech reco error $error")
                handleParseFailure()
            }

            override fun onResults(results: Bundle?) {
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let { texts ->
                    state.postValue(CycleState.SEARCHING)

                    val sentence = texts.joinToString(separator = " ")
                    Log.d(tag, "recognized text $sentence")
                    recognizedText.postValue(sentence)

                    CoroutineScope(Dispatchers.IO).launch {
                        WitAiApi.parseQuestionForEntity(sentence)?.let { entity ->
                            WikiApi.searchWiki(entity)?.let { searchResult ->
                                Log.d(tag, "searchResult $searchResult")
                                WikiApi.getWikiContent(searchResult.articleName)?.let { result ->
                                    Log.d(tag, "result $result")
                                    val textResponse = Html.fromHtml(result, Html.FROM_HTML_MODE_COMPACT)
                                    speak(textResponse.toString())

                                    state.postValue(CycleState.IDLE)
                                } ?: handleParseFailure()
                            } ?: handleParseFailure()
                        } ?: handleParseFailure()
                    }
                }
            }
        })
    }

    fun handleParseFailure() {
        state.postValue(CycleState.IDLE)
        speak(resources.getString(R.string.sorry_didnt_get_that))
    }

    fun initTextToSpeech(context: Context) {
        tts = TextToSpeech(context
        ) { initSuccess ->
            if (initSuccess == TextToSpeech.SUCCESS) {
                tts.language = languageLocale
                tts.setOnUtteranceProgressListener(ttsListener)

                ttsInitialized = true
            }
        }
    }

    @UiThread
    fun speak(text: String) {
        Log.d(tag, "speak ttsInitialized $ttsInitialized $text")
        if (text.isNotEmpty() && ttsInitialized) {
            val maxLength = TextToSpeech.getMaxSpeechInputLength()
            val textToSpeak =
                if (text.length > maxLength) text.take(maxLength)
                else text
            tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString())
        }
    }

    @UiThread
    fun stopSpeaking() {
        if (ttsInitialized) {
            tts.stop()
            state.postValue(CycleState.IDLE)
        }
    }

    fun onPause() {
        if (ttsInitialized && tts.isSpeaking) {
            stopSpeaking()
        }
    }
}