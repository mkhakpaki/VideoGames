package ir.mkhakpaki.videogames.di

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import ir.mkhakpaki.videogames.network.NetworkServiceGenerator
import ir.mkhakpaki.videogames.network.RestApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
object NetworkModule {

    @Provides
    @JvmStatic
    fun provideLoggingLevelInterceptor(): HttpLoggingInterceptor.Level {
        return NetworkServiceGenerator.makeLogLevel()
    }

    @Provides
    @JvmStatic
    fun provideLoggingInterceptor(logLevel: HttpLoggingInterceptor.Level): HttpLoggingInterceptor {
        return NetworkServiceGenerator.makeLoggingInterceptor(logLevel)
    }

    @Provides
    @JvmStatic
    fun provideCustomInterceptor(): NetworkServiceGenerator.CustomInterceptor {
        return NetworkServiceGenerator.makeInterceptor()
    }

    @Provides
    @Singleton
    @JvmStatic
    fun provideOkHttpClient(
        customInterceptor: NetworkServiceGenerator.CustomInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return NetworkServiceGenerator.makeOkHttp(customInterceptor, loggingInterceptor)
    }

    @Provides
    @Singleton
    @JvmStatic
    fun provideGson(): Gson {
        return NetworkServiceGenerator.makeGson()
    }

    @Provides
    @Singleton
    @JvmStatic
    fun provideConverterFactory(gson: Gson): Converter.Factory {
        return NetworkServiceGenerator.makeConverterFactory(gson)
    }

    @Provides
    @Singleton
    @JvmStatic
    fun provideCallAdapterFactory(): CallAdapter.Factory {
        return NetworkServiceGenerator.makeCallAdapterFactory()
    }

    @Provides
    @Singleton
    @JvmStatic
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory,
        callAdapterFactory: CallAdapter.Factory
    ): Retrofit {
        return NetworkServiceGenerator.getRetrofit(
            okHttpClient, converterFactory, callAdapterFactory)
    }

    @Provides
    @Singleton
    @JvmStatic
    fun provideRestApi(retrofit: Retrofit): RestApi {
        return NetworkServiceGenerator.createService(retrofit, RestApi::class.java)
    }
}