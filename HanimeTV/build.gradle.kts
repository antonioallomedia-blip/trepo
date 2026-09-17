// HanimeTV/build.gradle.kts
// Plugin-specific Gradle configuration for the HanimeTV CloudStream extension.

version = 1  // Increment this integer each time you release an update.

cloudstream {
    description = "Hanime.tv provider for CloudStream. Browse, search, and stream adult anime content directly from the official Hanime.tv API."
    authors = listOf("yourname")
    status = 1                    // 1 = OK (working)
    tvTypes = listOf("Movie")     // Hanime.tv content is primarily single-video entries.
    requiresResources = false
    language = "en"
    iconUrl = "https://hanime.tv/favicon.ico"
}