package com.example.restaurant.menus.data.tags

import android.util.Log
import com.example.restaurant.menus.data.tags.errors.NoDataAvailableThrowable
import com.example.restaurant.menus.data.tags.errors.NoMoreOfflineDataThrowable
import com.example.restaurant.menus.data.tags.local.TagLocalRepo
import com.example.restaurant.menus.data.tags.remote.TagsRemoteRepo
import io.reactivex.Maybe
import io.reactivex.functions.Consumer
import java.io.IOException
import javax.inject.Inject

class TagsGeneralRepo @Inject constructor(private val dataRemoteRepo: TagsRemoteRepo,
                                          private val dataLocalRepo: TagLocalRepo
) {

    var page: Int = 1

    fun loadFromScratch() {
        page = 1
    }

    fun getData(): Maybe<List<Tag>> =
        dataLocalRepo.getTags(page)
            .doOnSubscribe { Log.d("PAGE_NUMBER", page.toString()) }
            .flatMap {
                if (it.isEmpty())
                    dataRemoteRepo.getTags(page)
                        .doOnSuccess { remoteDataList ->
                            if (remoteDataList.isEmpty() && page == 1)
                                throw NoDataAvailableThrowable()
                            dataLocalRepo.insert(remoteDataList).subscribe()
                        }
                        .onErrorReturn { throwable -> throw when (throwable) {
                            is IOException -> NoMoreOfflineDataThrowable()
                            else -> throwable
                        } }
                        .toMaybe()
                else
                    Maybe.just(it)
            }.doOnSuccess {
                if (it.isNotEmpty()) page += 1
                Log.d("PAGE_NUMBER +", page.toString())
            }
}