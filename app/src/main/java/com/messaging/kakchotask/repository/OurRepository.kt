package com.messaging.kakchotask.repository

import androidx.lifecycle.MutableLiveData
import com.messaging.kakchotask.BuildConfig
import com.messaging.kakchotask.R
import com.messaging.kakchotask.model.Icon
import com.messaging.kakchotask.model.OurApiResponse
import com.messaging.kakchotask.utils.*
import com.messaging.kakchotask.utils.RetrofitHelper.Companion.isLoading
import com.messaging.kakchotask.utils.RetrofitHelper.Companion.retrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

object Repository {

    private val iconsLiveData = MutableLiveData<List<Icon>>()

    fun getIcons(query: String, count: Int, index: Int): MutableLiveData<List<Icon>> {
        isLoading = true
        val job = CoroutineScope(Dispatchers.IO).launch {
            val request: Response<OurApiResponse> = retrofitClient.getIcons(params(query, count, index))

            if (request.isSuccessful) {
                isLoading = false
                iconsLiveData.postValue(request.body()?.icons)
            } else {
                isLoading = false
            }
        }

        return iconsLiveData
    }

    private fun params(query: String, count: Int, index: Int): Map<String,String> =
        mapOf(
            QUERY to query,
            COUNT to count.toString(),
            START_INDEX to index.toString(),
            CLIENT_ID to MY_ID,
            CLIENT_SECRET to MY_SECRET
        )
}