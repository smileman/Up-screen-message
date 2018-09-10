package io.mobilife.upscreenmessage

import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.constraint.ConstraintLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.ContextCompat
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

object UpScreenMessage {
	
	@JvmStatic
	val DEFAULT_AUTO_HIDE_DELAY = 3000L

	private var uniqueLayoutId: Int = 0

	private val messageTypes: MutableMap<Int, MessageParams> = mutableMapOf()

	data class MessageParams(@LayoutRes val layoutResId: Int,
	                         @IdRes val textViewIdRes: Int,
	                         @DrawableRes val backgroundDrawableRes: Int,
	                         @ColorRes val textColor: Int)

	fun initialize(@IdRes layoutId: Int) {
		uniqueLayoutId = layoutId
	}

	fun registerMessageType(type: Int, messageParams: MessageParams) {
		messageTypes[type] = messageParams
	}
	
	fun showMessage(message: String, type: Int, container: ViewGroup, autoHideDelayMs: Long? = DEFAULT_AUTO_HIDE_DELAY) {
		val params: MessageParams = messageTypes[type] ?: return
		hideMessage(container)
		container.removeCallbacks(autoHideRunnable)
		TransitionManager.beginDelayedTransition(container)
		val errorView = makeErrorMessageView(message, params, container)
		when (container) {
			is LinearLayout -> showMessage(errorView, container)
			is FrameLayout -> showMessage(errorView, container)
			is RelativeLayout -> showMessage(errorView, container)
			is CoordinatorLayout -> showMessage(errorView, container)
			is ConstraintLayout -> showMessage(errorView, container)
			else -> throw IllegalStateException("This container is not supported by ErrorMessageHelper")
		}
		if (autoHideDelayMs != null) {
			autoHideMessage(autoHideDelayMs, container)
		}
	}

	private fun showMessage(messageView: View, container: LinearLayout) {
		container.addView(messageView, 0)
	}

	private fun showMessage(messageView: View, container: FrameLayout) {
		container.addView(messageView)
	}
	
	private fun showMessage(messageView: View, container: RelativeLayout) {
		container.addView(messageView)
	}

	private fun showMessage(messageView: View, container: CoordinatorLayout) {
		container.addView(messageView)
	}

	private fun showMessage(messageView: View, container: ConstraintLayout) {
		messageView.layoutParams = ConstraintLayout.LayoutParams(messageView.layoutParams.width, messageView.layoutParams.height).apply {
			this.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
			this.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
			this.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
		}
		container.addView(messageView)
	}
	
	fun hideMessage(container: ViewGroup) {
		val errorView: View? = container.findViewById(uniqueLayoutId)
		errorView?.let {
			TransitionManager.beginDelayedTransition(container)
			container.removeView(it)
		}
	}
	
	//region ==================== Internal logic ====================
	
	private fun checkIfInitialized() {
		if (uniqueLayoutId == 0) {
			throw IllegalStateException("ErrorMessageHelper is not initialized! Initialize with ErrorMessageHelper.initialize() call")
		}
	}
	
	private fun makeErrorMessageView(message: String, params: MessageParams, container: ViewGroup): View {
		checkIfInitialized()
		val context = container.context
		val layoutInflater = LayoutInflater.from(context)
		val view = layoutInflater.inflate(params.layoutResId, container, false)
		val tvMessage = view.findViewById<TextView>(params.textViewIdRes)
		tvMessage.text = message
		view.setBackgroundResource(params.backgroundDrawableRes)
		tvMessage.setTextColor(ContextCompat.getColor(context, params.textColor))
		view.id = uniqueLayoutId
		return view
	}

	private val autoHideRunnable = object : Runnable {

		var container: ViewGroup? = null

		override fun run() {
			container?.let { hideMessage(it) }
		}
	}
	
	private fun autoHideMessage(delayInMilliseconds: Long, container: ViewGroup) {
		container.postDelayed(autoHideRunnable.also { it.container = container }, delayInMilliseconds)
	}
	
	//endregion
	
}