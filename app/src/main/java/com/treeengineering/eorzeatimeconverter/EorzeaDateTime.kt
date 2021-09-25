package com.treeengineering.eorzeatimeconverter

import java.lang.Math.floor
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class EorzeaDateTime private constructor(
    var year: Int,
    var month: Short,
    var day: Short,
    var hour: Byte,
    var minute: Byte,
    var second: Byte
) {
    fun format(formatter: String): String = formatter.format(year, month, day, hour, minute, second)

    fun convertToLocalTime(): LocalDateTime {
        val utc = (((year - 1) * YEAR.toLong() +
                (month - 1) * MONTH +
                (day - 1) * DAY +
                hour * HOUR +
                minute * MINUTE +
                second) / EORZEA_MULTIPLIER).toLong() * 1000
        return Instant.ofEpochMilli(utc).atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    fun checkPassAndIncrease() {
        val now = now()
        if (now.hour >= hour && now.minute >= minute) day++
    }

    companion object {
        const val YEAR = 33177600 //384 days
        const val MONTH = 2764800  //32 days
        const val DAY = 86400
        const val HOUR = 3600
        const val MINUTE = 60
        const val SECOND = 1

        const val EORZEA_MULTIPLIER = 3600.0 / 175.0

        private fun convert(epochMilli: Long): EorzeaDateTime {
            val date = epochMilli / 1000.0
            val eorzeaTime = floor(date * EORZEA_MULTIPLIER).toLong()

            return EorzeaDateTime(
                (eorzeaTime / YEAR + 1).toInt(),
                (eorzeaTime / MONTH % 12 + 1).toShort(),
                (eorzeaTime / DAY % 32 + 1).toShort(),
                (eorzeaTime / HOUR % 24).toByte(),
                (eorzeaTime / MINUTE % 60).toByte(),
                (eorzeaTime / SECOND % 60).toByte()
            )
        }

        fun now(offset: Long = 0): EorzeaDateTime =
            convert(System.currentTimeMillis() + offset)

        fun convertLTtoET(localDateTime: LocalDateTime, offset: Int) =
            convert(localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli() + offset)
    }
}