package com.msa.eshop.data.remote.utills

object Constant {

    const val EMPTY_STRING = ""

    /*
     * مقدار فعلی پروژه از قبل OpenWeather بوده.
     * اگر API اصلی زرمارکت آدرس دیگری دارد، فقط همین مقدار را تغییر بده.
     * فرمت baseUrl در Retrofit باید با / تمام شود.
     */
    const val BASE_URL = "http://10.252.112.98:8282/"

    const val ICON_URL = "https://openweathermap.org/img/wn/"
    const val WEATHER_RESPONSE = "WEATHER_RESPONSE"
    const val LOCATION_COROUTINE_WORKER = "LOCATION_COROUTINE_WORKER"

    const val AUTHORIZATION_HEADER = "Authorization"
    const val BEARER_PREFIX = "Bearer"
    const val ACCEPT_HEADER = "Accept"
    const val CONTENT_TYPE_HEADER = "Content-Type"
    const val JSON_MEDIA_TYPE = "application/json"

    const val CONNECT_TIMEOUT_SECONDS = 60L
    const val READ_TIMEOUT_SECONDS = 60L
    const val WRITE_TIMEOUT_SECONDS = 60L
    const val CALL_TIMEOUT_SECONDS = 90L

    const val MAX_ERROR_LOG_LENGTH = 1_000
    const val NETWORK_LOG_TAG = "NetworkUtils"
}