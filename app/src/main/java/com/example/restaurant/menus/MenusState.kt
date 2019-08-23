package com.example.restaurant.menus

import com.example.restaurant.menus.data.items.Item
import com.example.restaurant.menus.data.tags.Tag

data class MenusState(
    val tags: List<Tag> = listOf(),
    val tagsLoading: TagsLoading? = null,
    val error: Errors? = null,

    val items: List<Item> = listOf(),
    val itemsInitialState: Boolean = false,
    val itemsLoading: Boolean = false,
    val itemErrors: Errors? = null
)

enum class Errors {
    NO_DATA_AVAILABLE, NO_MORE_OFFLINE_DATA, UNKNOWN
}

enum class TagsLoading {
    LOAD_MORE, LOAD_FROM_SCRATCH
}