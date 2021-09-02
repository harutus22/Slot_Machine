package com.luck.vullkleprikon.retrofit

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET


interface ApiClientMagicChecker {

    @GET("index.php")
    fun getResult() : Call<ResultModel>

    companion object {

        var BASE_URL = "http://luckleprikon.space/content/"

        fun create() : ApiClientMagicChecker {
            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(UserAgentInterceptor(System.getProperty("http.agent")))
            val client: OkHttpClient = httpClient.build()
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiClientMagicChecker::class.java)

        }

        private class UserAgentInterceptor(private val userAgent: String) : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val originalRequest: Request = chain.request()
                val requestWithUserAgent: Request = originalRequest.newBuilder()
                    .header("User-Agent", userAgent)
                    .build()
                return chain.proceed(requestWithUserAgent)
            }

        }
    }
}