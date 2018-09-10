package io.mobilife.upscreenmessage.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import io.mobilife.upscreenmessage.UpScreenMessage
import java.util.*

class MainActivity : AppCompatActivity() {

	private val TYPE_ERROR = 1
	private val TYPE_SUCCESS = 2

	private lateinit var viewContainer: ViewGroup

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		UpScreenMessage.initialize(android.R.id.text1)
		UpScreenMessage.registerMessageType(TYPE_ERROR, UpScreenMessage.MessageParams(io.mobilife.upscreenmessage.R.layout.default_up_screen_message,
				android.R.id.text1, R.color.red, android.R.color.white))
		UpScreenMessage.registerMessageType(TYPE_SUCCESS, UpScreenMessage.MessageParams(io.mobilife.upscreenmessage.R.layout.default_up_screen_message,
				android.R.id.text1, R.color.green, android.R.color.white))

		viewContainer = findViewById(R.id.container)
		findViewById<View>(R.id.btn_show_error_message)?.setOnClickListener { showMessage(TYPE_ERROR) }
		findViewById<View>(R.id.btn_show_success_message)?.setOnClickListener { showMessage(TYPE_SUCCESS) }
	}

	private fun showMessage(type: Int) {
		UpScreenMessage.showMessage("Sample message", type, viewContainer)
	}
}
