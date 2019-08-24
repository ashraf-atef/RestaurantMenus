package com.example.restaurant.menus.data.tags.local

import com.example.restaurant.menus.data.tags.Tag
import com.example.restaurant.menus.data.tags.PAGE_LIMIT
import io.reactivex.Maybe
import javax.inject.Inject

class TagLocalRepo @Inject constructor(private val tagDao: TagDao) {

    fun insert(tagList: List<Tag>) = tagDao.insert(tagList)

    fun getTags(page: Int): Maybe<List<Tag>> = tagDao.getTags((page -1) * PAGE_LIMIT, PAGE_LIMIT)
}