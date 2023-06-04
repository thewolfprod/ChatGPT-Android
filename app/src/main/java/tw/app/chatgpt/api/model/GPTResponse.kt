package tw.app.chatgpt.api.model

import com.google.gson.annotations.SerializedName

data class GPTResponse(@SerializedName("created")
                       val created: Int = 0,
                       @SerializedName("usage")
                       val usage: Usage,
                       @SerializedName("model")
                       val model: String = "",
                       @SerializedName("id")
                       val id: String = "",
                       @SerializedName("choices")
                       val choices: List<ChoicesItem>?,
                       @SerializedName("object")
                       val objectValue: String = "")