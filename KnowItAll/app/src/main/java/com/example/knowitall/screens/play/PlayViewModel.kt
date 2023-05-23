package com.example.knowitall.screens.play

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class PlayViewModel : ViewModel() {
    private lateinit var retrofit: Retrofit
    private lateinit var wikiApi: WikiApiService

    private val _randomContent = MutableLiveData<Event<PlayFragment.TitleAndContent>>()
    val randomContent: LiveData<Event<PlayFragment.TitleAndContent>> = _randomContent
    private val _navigateToErrorFragment = MutableLiveData<Event<Unit>>()
    val navigateToErrorFragment: LiveData<Event<Unit>> = _navigateToErrorFragment

    fun fetchRandomContent(context: Context) {
        if (isConnectedToWifi(context)) {
            retrofit = Retrofit.Builder()
                .baseUrl("https://en.wikipedia.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            wikiApi = retrofit.create(WikiApiService::class.java)

            wikiApi.fetchRandomContent().enqueue(object : Callback<RandomContentResponse> {
                override fun onFailure(call: Call<RandomContentResponse>, t: Throwable) {
                    val emptyTitleAndContent = PlayFragment.TitleAndContent("", "")
                    _randomContent.postValue(Event(emptyTitleAndContent))
                    _navigateToErrorFragment.postValue(Event(Unit)) // Trigger navigation to ErrorFragment
                }

                override fun onResponse(
                    call: Call<RandomContentResponse>,
                    response: Response<RandomContentResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseData = response.body()?.query?.pages?.values?.first()
                        val titleAndContent = responseData?.let { parseResponse(it) }
                        if (titleAndContent != null) {
                            _randomContent.postValue(Event(titleAndContent))
                        } else {
                            onFailure(call, Throwable("Failed to parse response"))
                        }
                    } else {
                        onFailure(call, Throwable("Unsuccessful response"))
                    }
                }
            })
        } else {
            val emptyTitleAndContent = PlayFragment.TitleAndContent("", "")
            _randomContent.postValue(Event(emptyTitleAndContent))
            _navigateToErrorFragment.postValue(Event(Unit)) // Trigger navigation to ErrorFragment
        }
    }

    private fun parseResponse(responseData: RandomContentResponseData): PlayFragment.TitleAndContent? {
        val title = responseData.title
        val content = responseData.extract
        return if (title != null && content != null) {
            PlayFragment.TitleAndContent(title, content)
        } else {
            null
        }
    }

    private fun isConnectedToWifi(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities =
                connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities)
                    ?: return false
            return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI
        }
    }
}

// Retrofit interfaces and data classes
interface WikiApiService {
    @GET("/w/api.php?action=query&format=json&generator=random&grnnamespace=0&grnlimit=1&prop=extracts&exintro=1&explaintext=1")
    fun fetchRandomContent(): Call<RandomContentResponse>
}

data class RandomContentResponse(
    val query: RandomContentResponseQuery
)

data class RandomContentResponseQuery(
    val pages: Map<String, RandomContentResponseData>
)

data class RandomContentResponseData(
    val title: String,
    val extract: String
)
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}