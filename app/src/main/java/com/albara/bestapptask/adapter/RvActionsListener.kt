package com.albara.bestapptask.adapter

import com.albara.bestapptask.data.objects.Movie

interface RvActionsListener {

    fun onItemClick(movie : Movie)

    fun onFavoriteCheckedChange(movie : Movie)
}