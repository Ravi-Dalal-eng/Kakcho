package com.messaging.kakchotask.repository

import androidx.lifecycle.MutableLiveData
import com.messaging.kakchotask.model.Icon
import com.messaging.kakchotask.utils.*
import com.messaging.kakchotask.utils.RetrofitHelper.Companion.getInstance
import com.messaging.kakchotask.utils.RetrofitHelper.Companion.isLoading

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch


object Repository {

    private val iconsLiveData = MutableLiveData<List<Icon>>()

    fun getIcons(query: String, count: Int, index: Int): MutableLiveData<List<Icon>> {
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
        val ourService=getInstance().create(OurService::class.java)
        val ourResult=ourService.getIcons(params(query, count, index))

            if (ourResult.isSuccessful) {
                isLoading = false
                iconsLiveData.postValue(ourResult.body()?.icons)
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