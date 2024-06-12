package com.example.android.codelabs.paging.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import kotlin.math.max

private val firstArticleCreatedTime = LocalDateTime.now()
private const val LOAD_DELAY = 1000L
private const val STARTING_KEY = 0

class ArticlePagingSource: PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val start = params.key ?: STARTING_KEY // 0
        val range = start.until(start + params.loadSize)  // 0 .. 20  (loadsize is 20)

        if (start != STARTING_KEY) delay(LOAD_DELAY)
        val x = when (start){
            STARTING_KEY -> null  // pirvelad shemova ak
            else -> ensureValidKey(key = range.first - params.loadSize)
        }
        val y = range.last + 1

        Log.d("paging", "load prevkey  $x   nextkey  $y  start  $start  range  $range  params.key  ${params.key}  loadSize  ${params.loadSize}")

        return LoadResult.Page(
            data = range.map {
                Article(
                    id = it,
                    title = "Article $it",
                    description = "This describes article $it",
                    created = firstArticleCreatedTime.plusDays(it.toLong())
                )
            },

            prevKey = when (start){
                STARTING_KEY -> null  // pirvelad shemova ak
                else -> ensureValidKey(key = range.first - params.loadSize)
            },

            nextKey = range.last + 1   // id of first item that must be loaded () , it defines the next page first item id
        )

        Log.d("paging 2", "prevkey  $x   nextkey  $y  start  $start  range  $range")

    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {  // return the  index that must be use nest load request
        val anchorPosition = state.anchorPosition ?: return null // last index that successfully fetched data when read
        val article = state.closestItemToPosition(anchorPosition) ?: return null
        val x = ensureValidKey(key = article.id - (state.config.pageSize / 2))
        Log.d("paging", "getRefreshKey  $x  article.id ${article.id }  state.config.pageSize ${state.config.pageSize}")
        return x
    }

    private fun ensureValidKey(key: Int) = max(STARTING_KEY, key)

}