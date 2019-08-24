package com.example.restaurant.menus.data.tags

import androidx.annotation.VisibleForTesting
import com.example.restaurant.menus.data.errors.NoDataAvailableThrowable
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
        tagsLocalRepo.getTags(page)
            .flatMap {
                if (it.isEmpty())
                    tagsRemoteRepo.getTags(page)
                        .doOnSuccess { remoteDataList ->
                            if (remoteDataList.isEmpty()) {
                                if (page == 1)
                                    throw NoDataAvailableThrowable()
                            } else
                                tagsLocalRepo.insert(remoteDataList).subscribe()
                        }
                else
                    Single.just(it)
            }.doOnSuccess {
                if (it.isNotEmpty()) page += 1
            }
}