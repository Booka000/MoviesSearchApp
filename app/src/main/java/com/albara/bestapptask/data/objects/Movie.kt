package com.albara.bestapptask.data.objects

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_table")
data class Movie(
    @PrimaryKey
    val id: Int,
    val name: String,
    @Embedded
    val poster: Poster,
    @Embedded
    val rating: Rating,
    @Embedded
    val votes: Votes,
    var isFavorited : Boolean = false
)