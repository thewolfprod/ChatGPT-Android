package tw.app.chatgpt.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import ru.gildor.coroutines.okhttp.await
import tw.app.chatgpt.api.model.GPTResponse
import java.lang.Exception
import java.util.concurrent.TimeUnit


class ApiCall(
    private val apiKey: String,
    private val apiHost: String,
    private val listener: OnApiCallResponse
) {
    private val client: OkHttpClient
    private val mediaType = "application/json".toMediaTypeOrNull()!!

    private var latestQuestion = ""

    init {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        client = builder.build();
    }

    public suspend fun Call(question: String) {
        latestQuestion = question
        var isLastActionSuccessful = true

        val body = RequestBody.create(
            mediaType,
            "{\r\n    \"model\": \"gpt-3.5-turbo\",\r\n    \"messages\": [\r\n        {\r\n            \"role\": \"user\",\r\n            \"content\": \"$question\"\r\n        }\r\n    ]\r\n}"
        )

        val request: Request = Request.Builder()
            .url("https://openai80.p.rapidapi.com/chat/completions")
            .post(body)
            .addHeader("content-type", "application/json")
            .addHeader("X-RapidAPI-Key", apiKey)
            .addHeader("X-RapidAPI-Host", apiHost)
            .build()

        val response = client.newCall(request).await()

        if (!response.isSuccessful) {
            isLastActionSuccessful = false
            listener.onError(Exception("Cannot receive response from API!"))
        }

        if (isLastActionSuccessful) {
            var responseString = response.body!!.string()
            Log.d("ResponseString", "Call: $responseString")
            val gptResponse = Gson().fromJson<GPTResponse>(responseString)

            if (gptResponse != null) {
                listener.onSuccess(gptResponse)
            } else {
                listener.onError(Exception("Cannot convert response to object!"))
            }
        }
    }

    public suspend fun TryCallAgain() {
        if (latestQuestion.isNotBlank())
            Call(latestQuestion)
    }
}

interface OnApiCallResponse {
    fun onSuccess(response: GPTResponse)

    fun onError(error: Exception)
}

inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)