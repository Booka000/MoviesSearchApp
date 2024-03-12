package com.albara.bestapptask.util

import androidx.recyclerview.widget.DiffUtil
import com.albara.bestapptask.data.objects.Movie

/*
  This is considered a better practice than using "notifyDataSetChanged" since it
  calculates the difference between the old list and new list and updetes only the
  changed items rahter than updating all the items, which is expinsve in cases where
  the list is large
*/

class DataDiffCallback(private val oldList: List<Movie>, private val newList: List<Movie>)
    : DiffUtil.Callback() {
    override fun getOldListSize() : Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}