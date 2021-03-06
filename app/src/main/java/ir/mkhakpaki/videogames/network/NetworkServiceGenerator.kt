package ir.mkhakpaki.videogames.network

import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import ir.mkhakpaki.videogames.BuildConfig
import ir.mkhakpaki.videogames.util.API_PATH
import ir.mkhakpaki.videogames.util.Constants
import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkServiceGenerator {

    private var retrofit: Retrofit? = null

    fun makeLogLevel(): HttpLoggingInterceptor.Level {
        return if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    fun makeLoggingInterceptor(logLevel: HttpLoggingInterceptor.Level): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(logLevel)
    }

    fun makeInterceptor(): CustomInterceptor {
        return CustomInterceptor()
    }

    fun makeOkHttp(
        customInterceptor: CustomInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.addInterceptor(customInterceptor)
        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }
        okHttpClientBuilder.connectTimeout(Constants.CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(Constants.READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.writeTimeout(Constants.WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.connectionPool(
            ConnectionPool(
                Constants.MAX_IDLE_CONNECTIONS,
                Constants.KEEP_ALIVE_TIME.toLong(),
                TimeUnit.SECONDS
            )
        )
        return okHttpClientBuilder.build()
    }

    fun makeGson(): Gson {
        return Gson()
    }

    fun makeConverterFactory(gson: Gson): Converter.Factory {
        return GsonConverterFactory.create(gson)
    }

    fun makeCallAdapterFactory(): CallAdapter.Factory {
        return CoroutineCallAdapterFactory.invoke()
    }

    fun getRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory,
        callAdapterFactory: CallAdapter.Factory
    ): Retrofit {
        if (retrofit == null) {
            makeRetrofit(okHttpClient, converterFactory, callAdapterFactory)
        }
        return retrofit!!
    }

    @Synchronized
    private fun makeRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory,
        callAdapterFactory: CallAdapter.Factory
    ) {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .addConverterFactory(converterFactory)
                    .addCallAdapterFactory(callAdapterFactory)
                    .baseUrl(API_PATH.BASE_URL)
                    .build()
        }
    }

    fun <S> createService(retrofit: Retrofit, serviceClass: Class<S>): S {
        return retrofit.create(serviceClass)
    }

    class CustomInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = makeRequestWithDefaultHeaders(chain.request())
            return chain.proceed(request.build())
        }

        private fun makeRequestWithDefaultHeaders(request: Request): Request.Builder {
            return request.newBuilder().apply {
                addHeader("Accept", "Application/JSON")
                addHeader("x-rapidapi-key", "31cc6dad7fmsh806cf13ebb60734p1688ddjsnac373e8bc9ce")
                addHeader("x-rapidapi-host", "rawg-video-games-database.p.rapidapi.com")
            }
        }
    }
}
