package com.test.weatherapp.utils

import android.content.Context
import com.androidnetworking.interceptors.HttpLoggingInterceptor
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.test.weatherapp.BuildConfig
import com.test.weatherapp.utils.cookie.PersistentCookieStore
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.*
import java.util.concurrent.TimeUnit
/**
 * Created by GATHAIYA on 02/08/2021.
 */
class RetrofitUtil(private val context: Context) {
    private var retrofit: Retrofit? = null
    fun retrofit(URL: String): Retrofit? {
        if (retrofit == null) {
            val cookieHandler: CookieHandler = CookieManager(
                PersistentCookieStore(
                    context
                ), CookiePolicy.ACCEPT_ALL
            )
            val logging = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                logging.level = HttpLoggingInterceptor.Level.BODY
            } else {
                logging.level = HttpLoggingInterceptor.Level.NONE
            }
            val timeOut = 120
            val httpClient = OkHttpClient.Builder()
                .cookieJar(JavaNetCookieJar(cookieHandler))
                .addInterceptor(logging)
                .readTimeout(timeOut.toLong(), TimeUnit.SECONDS)
                .connectTimeout(timeOut.toLong(), TimeUnit.SECONDS)
                .build()
            val gson = GsonBuilder().setLenient().excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(
                    Date::class.java,
                    JsonDeserializer { json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext? ->
                        if (json == null) null else Date(json.asLong)
                    }
                ).create()
            retrofit = Retrofit.Builder().baseUrl("$URL/").client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson)).build()
        }
        return retrofit
    }

}