package com.quickpoint.snookerboard.core.utils

import android.content.Context
import com.quickpoint.snookerboard.domain.models.DomainAppReleaseDetails

fun parseChangelog(context: Context): List<DomainAppReleaseDetails> {
    val releaseDetails = mutableListOf<DomainAppReleaseDetails>()
    val changeLog = readFileFromAssets(context, "changelog.md")

    val uncommentedChangelog = mutableListOf<String>()
    var inComment = false

    changeLog.lineSequence().forEach { line ->
        when {
            line.trim().startsWith("//") -> {}
            line.contains("<!--") -> {
                uncommentedChangelog.add(line.substringBefore("<!--"))
                inComment = true
            }

            line.contains("-->") -> {
                uncommentedChangelog.add(line.substringAfter("-->"))
                inComment = false
            }

            !inComment && line.isNotBlank() -> {
                uncommentedChangelog.add(line)
            }
        }
    }

    var version = ""
    var notes = mutableListOf<String>()
    var skipVersion = true
    var skipCategory = true
    uncommentedChangelog.forEach { line ->
        when {
            line.startsWith("## [v") -> {
                skipVersion = line.contains("X")
                if (!skipVersion) {
                    if (version.isNotEmpty()) {
                        releaseDetails.add(DomainAppReleaseDetails(version, notes))
                        notes = mutableListOf()
                    }
                    version = "Version ${line.substring(4, line.indexOf(']'))}"
                }
            }

            line.startsWith("###") -> {
                skipCategory = !line.contains("Release")
            }

            else -> {
                if (!skipVersion && !skipCategory) {
                    notes.add(line.replace("*", "•"))
                }
            }
        }
    }
    if (version.isNotEmpty()) {
        releaseDetails.add(DomainAppReleaseDetails(version, notes))
    }
    return releaseDetails
}

private fun readFileFromAssets(context: Context, filename: String): String {
    val inputStream = context.assets.open(filename)
    val size = inputStream.available()
    val buffer = ByteArray(size)
    inputStream.read(buffer)
    inputStream.close()
    return String(buffer)
}