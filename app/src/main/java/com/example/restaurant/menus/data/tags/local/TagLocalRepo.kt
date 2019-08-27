package com.example.restaurant.menus.data.tags.local

import com.example.restaurant.menus.data.tags.Tag
import io.reactivex.Single
import javax.inject.Inject

class TagLocalRepo @Inject constructor(private val tagDao: TagDao) {

    fun insert(tagList: List<Tag>) = tagDao.insert(tagList)

    fun getTags(page: Int): Single<List<Tag>> = tagDao.getTags(page)
}