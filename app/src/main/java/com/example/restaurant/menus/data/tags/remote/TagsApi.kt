package com.example.restaurant.menus.data.tags.remote

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface TagsApi {

    @GET("tags/{page}")
    fun getTags(@Path("page") page: Int): Single<TagResponseDto>
}