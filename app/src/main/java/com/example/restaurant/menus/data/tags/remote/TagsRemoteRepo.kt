package com.example.restaurant.menus.data.tags.remote

import com.example.restaurant.common.dataLayer.remote.error.ErrorFunction
import javax.inject.Inject

class TagsRemoteRepo @Inject constructor(private val tagsApi: TagsApi) {

    fun getTags(page: Int) = tagsApi
        .getTags(page)
        .map { it.tags }
        .onErrorReturn(ErrorFunction())
}