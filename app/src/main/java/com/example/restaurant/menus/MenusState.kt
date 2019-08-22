package com.example.restaurant.menus

import com.example.restaurant.menus.data.tags.Tag

data class MenusState(
    var tagList: List<Tag> = listOf(),
    val loading: DataLoading? = null,
    val error: DataErrors? = null
)
enum class DataErrors {
    NO_DATA_AVAILABLE, NO_MORE_OFFLINE_DATA, UNKNOWN
}

enum class DataLoading {
    LOAD_MORE, LOAD_FROM_SCRATCH
}