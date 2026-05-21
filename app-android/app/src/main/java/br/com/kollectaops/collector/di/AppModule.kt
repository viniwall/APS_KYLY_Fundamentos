package br.com.kollectaops.collector.di

import android.content.Context
import androidx.room.Room
import br.com.kollectaops.collector.BuildConfig
import br.com.kollectaops.collector.data.local.AppDatabase
import br.com.kollectaops.collector.data.remote.interceptor.AuthInterceptor
import br.com.kollectaops.collector.data.remote.service.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "kollectaops.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideCaixaDao(db: AppDatabase) = db.caixaDao()
    @Provides fun provideItemCaixaDao(db: AppDatabase) = db.itemCaixaDao()
    @Provides fun provideEventoDao(db: AppDatabase) = db.eventoPickingDao()
    @Provides fun provideBemDao(db: AppDatabase) = db.bemDao()
    @Provides fun provideSyncLogDao(db: AppDatabase) = db.syncLogDao()

    @Provides @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                        else HttpLoggingInterceptor.Level.NONE
            })
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}
