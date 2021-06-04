package com.quickpoint.snookerboard.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "http://api.snooker.org/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val retrofit = Retrofit.Builder()
//    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface RankingsApiService {
    @GET("/")
    suspend fun getRankings(
        @Query("rt") rankingType: String,
        @Query("s") season: String
    ): List<NetworkRanking>

    @GET("/")
    suspend fun getPlayers(
        @Query("t") type: String,
        @Query("st") statisticType: String,
        @Query("s") season: String
    ): List<NetworkPlayer>
}

object RankingsApi {
    val retrofitService: RankingsApiService by lazy {
        retrofit.create(RankingsApiService::class.java)
    }
}