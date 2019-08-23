package com.example.restaurant.menus.data.tags

import com.example.restaurant.menus.data.tags.errors.NoDataAvailableThrowable
import com.example.restaurant.menus.data.tags.local.TagLocalRepo
import com.example.restaurant.menus.data.tags.remote.TagsRemoteRepo
import io.reactivex.Maybe
import javax.inject.Inject

class TagsGeneralRepo @Inject constructor(private val dataRemoteRepo: TagsRemoteRepo,
                                          private val dataLocalRepo: TagLocalRepo) {

    var page: Int = 1

    fun rest() {
        page = 1
    }

    fun getData(): Maybe<List<Tag>> =
        dataLocalRepo.getTags(page)
            .flatMap {
                if (it.isEmpty())
                    dataRemoteRepo.getTags(page)
                        .doOnSuccess { remoteDataList ->
                            if (remoteDataList.isEmpty() && page == 1)
                                throw NoDataAvailableThrowable()
                            dataLocalRepo.insert(remoteDataList).subscribe()
                        }
                        .toMaybe()
                else
                    Maybe.just(it)
            }.doOnSuccess {
                if (it.isNotEmpty()) page += 1
            }
}