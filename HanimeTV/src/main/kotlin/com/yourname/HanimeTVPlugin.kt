package com.yourname

import android.content.Context
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin

/**
 * CloudStream plugin entry point for the HanimeTV provider.
 *
 * The `@CloudstreamPlugin` annotation marks this class as the discovery
 * target so that CloudStream can load the plugin at runtime.
 */
@CloudstreamPlugin
class HanimeTVPlugin : Plugin() {

    override fun load(context: Context) {
        // Register the main content provider.
        registerMainAPI(HanimeTV())

        // (Optional) You can add settings UI hooks here if needed.
        // openSettings = { ctx ->
        //     // Show a settings fragment / dialog.
        // }
    }
}
