package com.milk.funcall.common.imageLoad

import android.app.Application
import android.content.Context
import coil.Coil
import coil.ImageLoader
import coil.memory.MemoryCache
import coil.request.CachePolicy
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File

object LoaderManager {
    private lateinit var current: Application
    private const val MAX_FACTORY_CACHE = 10 * 1024 * 1024L

    private val memoryCache by lazy {
        MemoryCache
            .Builder(current)
            .maxSizePercent(0.2)
            .weakReferencesEnabled(true)
            .build()
    }

    fun initialize(application: Application) {
        Coil.setImageLoader(
            ImageLoader.Builder(application)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .memoryCache(memoryCache)
                .callFactory(createOkHttp(application))
                .build()
        )
    }

    private fun createOkHttp(application: Application): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(createDefaultCache(application))
            .build()
    }

    private fun createDefaultCache(context: Context): Cache {
        val cacheDirectory = getDefaultCacheDirectory(context)
        return Cache(cacheDirectory, MAX_FACTORY_CACHE)
    }

    private fun getDefaultCacheDirectory(context: Context): File {
        return File(context.cacheDir, "image_cache").apply { mkdirs() }
    }
}