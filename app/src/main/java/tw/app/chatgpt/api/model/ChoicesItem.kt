package tw.app.chatgpt.api.model

import com.google.gson.annotations.SerializedName

data class ChoicesItem(@SerializedName("finish_reason")
                       val finishReason: String = "",
                       @SerializedName("index")
                       val index: Int = 0,
                       @SerializedName("message")
                       val message: Message
)