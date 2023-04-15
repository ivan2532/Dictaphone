package com.ivanb.dictaphone

import java.nio.file.attribute.FileTime
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class RecordModel(
    val name: String,
    private val creationTime: FileTime,
    val path: String
) : java.io.Serializable {

    fun getCreationTimeString(): String {

        return DateTimeFormatter.ofPattern("uuuu-MMM-dd HH:mm:ss", Locale.ENGLISH)
            .withZone(ZoneId.systemDefault())
            .format(creationTime.toInstant())
    }
}