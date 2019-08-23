package com.example.restaurant.menus.data.items.remote

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ItemsApi {

    @GET("items/{tagName}")
    fun getItems(@Path("tagName") tagName: String): Single<ItemsResponseDto>
}