package com.pluto.ui.container

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import com.pluto.Pluto
import com.pluto.R
import com.pluto.databinding.PlutoActivityPlutoBinding
import com.pluto.plugin.PluginHelper.Companion.ID_LABEL
import com.pluto.utilities.extensions.toast
import com.pluto.utilities.share.ContentShareViewModel
import com.pluto.utilities.share.ShareAction
import com.pluto.utilities.share.ShareFragment
import com.pluto.utilities.share.copyToClipboard
import com.pluto.utilities.share.lazyContentSharer
import com.pluto.utilities.share.share
import com.pluto.utilities.share.shareFile

class PlutoActivity : AppCompatActivity() {

    private val sharer: ContentShareViewModel by lazyContentSharer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = PlutoActivityPlutoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.pluto___dark)
        sharer.data.observe(this) {
            val shareFragment = ShareFragment()
            shareFragment.arguments = Bundle().apply {
                putString("title", it.title)
                putString("content", it.content)
                putString("fileName", it.fileName)
            }
            shareFragment.show(supportFragmentManager, "bottomSheetFragment")
        }

        sharer.action.observe(this) {
            when (it) {
                is ShareAction.ShareAsText -> share(it.shareable.processedContent, it.shareable.title)
                is ShareAction.ShareAsFile -> shareFile(it.shareable.processedContent, it.shareable.title, it.shareable.fileName, it.type)
                is ShareAction.ShareAsCopy -> copyContent(it.shareable.content, it.shareable.title)
            }
        }
        handleIntent(intent)
        Pluto.resetDataCallback.state.removeObserver(uiStateListener)
        Pluto.resetDataCallback.state.observe(this, uiStateListener)
    }

    private fun copyContent(content: String, title: String) {
        copyToClipboard(content, title)
        toast("Content copied to Clipboard")
    }

    private fun handleIntent(intent: Intent?) {
        intent?.getStringExtra(ID_LABEL)?.let { id ->
            Pluto.pluginManager.get(id)?.let {
                val fragment = it.getView()
                supportFragmentManager.beginTransaction().apply {
                    this.runOnCommit {
                        it.onPluginViewCreated(it.savedInstance)
                    }
                    this.replace(R.id.container, fragment).commit()
                }
                return
            }
            applicationContext.toast("Plugin [$id] not installed")
            finish()
        }
    }

    private val uiStateListener = Observer<Boolean> {
        if (it) {
            finish()
        }
    }

//    override fun onResume() {
//        super.onResume()
//        if (!Pluto.session.isConsentAlreadyShown && Pluto.notch?.enabled == true && !canDrawOverlays()) {
//            lifecycleScope.delayedLaunchWhenResumed(CONSENT_SHOW_DELAY) {
//                OverConsentFragment().show(supportFragmentManager, CONSENT_SHOW_TAG)
//            }
//        }
//    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private companion object {
        const val CONSENT_SHOW_DELAY = 400L
        const val CONSENT_SHOW_TAG = "overlay_consent"
    }
}
