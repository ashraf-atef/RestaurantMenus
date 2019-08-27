package com.example.restaurant.menus.data.tags

import androidx.annotation.VisibleForTesting
import com.example.restaurant.menus.data.errors.NoMoreOfflineDataThrowable
import com.example.restaurant.menus.data.tags.local.TagLocalRepo
import com.example.restaurant.menus.data.tags.remote.TagsRemoteRepo
import io.reactivex.Single
import javax.inject.Inject

class TagsGeneralRepo @Inject constructor(
    private val tagsRemoteRepo: TagsRemoteRepo,
    private val tagsLocalRepo: TagLocalRepo
) {

    @VisibleForTesting
    var page: Int = 1

    fun rest() {
        page = 1
    }

    fun getTags(): Single<List<Tag>> =
        tagsRemoteRepo.getTags(page)
            .doOnSuccess {
                tagsLocalRepo.insert(it).subscribe()
            }
            .onErrorResumeNext {
                tagsLocalRepo.getTags(page)
                    .doOnSuccess {
                        if (it.isEmpty()) throw NoMoreOfflineDataThrowable()
                    }
            }
            .doOnSuccess {
                if (it.isNotEmpty()) page += 1
            }
}