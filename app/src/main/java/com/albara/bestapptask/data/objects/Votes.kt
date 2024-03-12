package com.albara.bestapptask.data.objects

import androidx.room.ColumnInfo

data class Votes(
    @ColumnInfo(name = "votes")
    val imdb: Int
)