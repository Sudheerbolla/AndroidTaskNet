package com.imageloader.utils.wsutils

import com.imageloader.BuildConfig
import com.imageloader.models.ImageGroup
import com.imageloader.models.ImageModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

/**
 * Building Retrofit client and all apis needed
 */
interface RetrofitService {

    @GET("/manifest")
    fun getImageGroups(): Call<ImageGroup>

    @GET("/image/{image_identifier}")
    fun getImageDetails(
        @Path(
            value = "image_identifier",
            encoded = true
        ) imageIdentifier: String?
    ): Call<ImageModel>

    companion object {

        var retrofitService: RetrofitService? = null

        fun getInstance(): RetrofitService {
            if (retrofitService == null) {
                val httpClient = OkHttpClient.Builder()
                httpClient.readTimeout(10, TimeUnit.SECONDS)
                httpClient.connectTimeout(5, TimeUnit.SECONDS)
//              In debug/development version, for logging purposes
                if (BuildConfig.DEBUG) {
                    val interceptor = HttpLoggingInterceptor()
                    interceptor.level = HttpLoggingInterceptor.Level.BASIC
                    httpClient.addInterceptor(interceptor)
                }
//              Adding header for token
                httpClient.addInterceptor { chain ->
                    val request: Request = chain.request().newBuilder().addHeader(
                        BuildConfig.REQUEST_HEADER_KEY,
                        BuildConfig.REQUEST_HEADER_TOKEN
                    ).build()
                    chain.proceed(request)
                }
//              Building retrofit client
                val retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_HEROKU_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }

    }

}