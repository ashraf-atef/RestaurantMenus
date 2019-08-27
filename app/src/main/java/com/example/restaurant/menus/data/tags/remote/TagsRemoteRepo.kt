package com.example.restaurant.menus.data.tags.remote

import javax.inject.Inject

class TagsRemoteRepo @Inject constructor(private val tagsApi: TagsApi) {

    fun getTags(page: Int) = tagsApi
        .getTags(page)
        .map { it.tags.map { tag -> tag.copy(pageNumber = page) }}
}