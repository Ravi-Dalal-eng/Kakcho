package com.messaging.kakchotask.utils

import com.messaging.kakchotask.model.OurApiResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface OurService {


    @GET(ICONS_URL)
    suspend fun getIcons(@QueryMap params: Map<String, String>) : Response<OurApiResponse>



    @GET
    @Streaming
    fun downloadFile(@Url url: String): Call<ResponseBody>
}