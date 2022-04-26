package com.r.cohen.askwiki

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import com.r.cohen.askwiki.databinding.ActivityMainBinding
import com.r.cohen.askwiki.stores.AskWikiPreferences
import com.skydoves.balloon.createBalloon
import com.skydoves.balloon.showAlignTop

private const val permissionsRequestCode = 1

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var preferences: AskWikiPreferences
    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = AskWikiPreferences(this)
        viewModel.initSpeechRecognizer(SpeechRecognizer.createSpeechRecognizer(this))
        viewModel.initTextToSpeech(this)
        viewModel.resources = this.resources

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        rootView = binding.root
    }

    override fun onResume() {
        super.onResume()
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayListOf(Manifest.permission.RECORD_AUDIO).toTypedArray(),
                permissionsRequestCode)
            return
        }

        if (!preferences.isFirstTimeToolTipShown) {
            show1stTimeTooltip()
            preferences.isFirstTimeToolTipShown = true
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    private fun show1stTimeTooltip() {
        val micView = rootView.findViewById<CardView>(R.id.cardViewMic)
        val tooltip = createBalloon(this) {
            setText(getString(R.string.hold_mic_tooltip))
            setTextSize(15f)
            setWidthRatio(0.5f)
            setPaddingTop(9)
            setPaddingBottom(9)
            setLifecycleOwner(this@MainActivity)
            setDismissWhenClicked(true)
            build()
        }
        micView.showAlignTop(tooltip)
    }
}