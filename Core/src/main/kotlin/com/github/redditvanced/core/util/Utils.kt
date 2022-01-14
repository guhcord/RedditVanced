package com.github.redditvanced.core.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.widget.Toast
import com.github.redditvanced.core.Main
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias BaseActivity = com.reddit.frontpage.a
typealias FrontpageSettings = xw.c

@Suppress("unused")
@SuppressLint("StaticFieldLeak") // This never happens as the patch always replaces it with new one
object Utils {
	/**
	 * The main thread.
	 * Do UI updates here otherwise they won't work.
	 */
	val mainThread = Handler(Looper.getMainLooper())

	/**
	 * ThreadPool. Please use this for asynchronous Tasks instead of creating Threads manually
	 * as spinning up new Threads everytime is heavy on the CPU
	 */
	val threadPool = Executors.newCachedThreadPool() as ExecutorService

	/**
	 * The current Android activity
	 */
	lateinit var appActivity: BaseActivity

	lateinit var appContext: Context

	/**
	 * Launches an URL in the user's preferred Browser
	 * @param url The url to launch
	 */
	@JvmStatic
	fun launchUrl(url: String) {
		launchUrl(Uri.parse(url))
	}

	/**
	 * Launches an URL in the user's preferred Browser
	 * @param url The url to launch
	 */
	@JvmStatic
	fun launchUrl(url: Uri) {
		appActivity.startActivity(Intent(Intent.ACTION_VIEW).setData(url))
	}

	/**
	 * Converts the singular term of the `noun` into plural.
	 * @param amount Amount of the noun.
	 * @param noun The noun
	 * @return Pluralised `noun`
	 */
	fun pluralise(amount: Int, noun: String) = "$amount $noun${if (amount != 1) "s" else ""}"

	/**
	 * Send a toast from any thread.
	 * @param message Message to show
	 * @param showLonger Whether to show toast for an extended period of time
	 */
	@JvmOverloads
	@JvmStatic
	fun showToast(message: String, showLonger: Boolean = false) {
		mainThread.post {
			Toast.makeText(
				appContext,
				message,
				if (showLonger) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
			).show()
		}
	}

	private val resIdCache = HashMap<String, Int>()

	/**
	 * Get a resource id from the reddit package.
	 * @param name Name of the resource.
	 * @param type Type of the resource.
	 * @return ID of the resource, or 0 if not found.
	 */
	@JvmStatic
	fun getResId(name: String, type: String) = resIdCache.computeIfAbsent(name) { k ->
		appContext.resources.getIdentifier(
			k,
			type,
			"com.reddit.frontpage"
		)
	}

	/**
	 * Initialize patches
	 */
	fun init() {
		Main.patcher.before<BaseActivity>("onCreate", Bundle::class.java) {
			appActivity = this
			appContext = this.applicationContext
		}
	}
}
