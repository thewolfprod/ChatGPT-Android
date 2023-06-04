package tw.app.chatgpt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import tw.app.chatgpt.activity.BaseActivity
import tw.app.chatgpt.adapter.ChatAdapter
import tw.app.chatgpt.adapter.model.ChatItem
import tw.app.chatgpt.adapter.model.DataType
import tw.app.chatgpt.adapter.model.SENDER
import tw.app.chatgpt.api.ApiCall
import tw.app.chatgpt.api.OnApiCallResponse
import tw.app.chatgpt.api.model.GPTResponse
import tw.app.chatgpt.databinding.ActivityMainBinding
import tw.app.chatgpt.R.string as AppString
import java.lang.Exception

class MainActivity : BaseActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var _apiCall: ApiCall? = null
    private val apiCall get() = _apiCall!!

    private var _apiCallListener: OnApiCallResponse? = null
    private val apiCallListener get() = _apiCallListener!!

    private var _adapter: ChatAdapter? = null
    private val adapter get() = _adapter!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setup()
        init()
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
        _adapter = null
        _apiCall = null
        _apiCallListener = null
    }

    private fun setup() {
        _adapter = ChatAdapter(arrayListOf())
        binding.chatRecyclerView.adapter = adapter

        _apiCallListener = object : OnApiCallResponse {
            override fun onSuccess(response: GPTResponse) {
                runOnUiThread {
                    binding.userMessageEditText.isEnabled = true
                    binding.sendButton.isEnabled = true

                    val responseItem = DataType.HorizontalClass(
                        ChatItem(
                            senderType = SENDER.ASSISTANT,
                            message = response.choices?.get(0)?.message?.content
                                ?: "Error Occurred!",
                            sentAt = response.created.toLong()
                        )
                    )

                    addMessageToRecyclerView(responseItem)
                }
            }

            override fun onError(error: Exception) {
                runOnUiThread {
                    binding.userMessageEditText.isEnabled = true
                    binding.sendButton.isEnabled = true

                    val responseItem = DataType.HorizontalClass(
                        ChatItem(
                            senderType = SENDER.ASSISTANT,
                            message = "Error Occurred!\n${error.message?.toString() ?: "Unknown Error!"}",
                            sentAt = 0
                        )
                    )

                    addMessageToRecyclerView(responseItem)
                }
            }
        }

        _apiCall = ApiCall(
            apiKey = getString(AppString.APIKey),
            apiHost = getString(AppString.APIHost),
            listener = apiCallListener
        )
    }

    private fun init() {
        binding.sendButton.setOnClickListener {
            if (binding.userMessageEditText.text.toString().isNotBlank()) {

                val chatItem = DataType.VerticalClass(
                    ChatItem(
                        senderType = SENDER.USER,
                        message = binding.userMessageEditText.text.toString(),
                        sentAt = 0
                    )
                )

                addMessageToRecyclerView(chatItem)

                binding.userMessageEditText.isEnabled = false
                binding.sendButton.isEnabled = false

                launch {
                    apiCall.Call(binding.userMessageEditText.text.toString())
                }
            } else {
                Snackbar.make(
                    binding.root,
                    "Message cannot be empty!",
                    Snackbar.LENGTH_SHORT
                ).setAnchorView(binding.bottomPanel)
                    .show()
            }
        }
    }

    /**
     * Adds message to RecyclerView and scrolls to bottom
     *
     * Also clears EditText if text is here... bcs why not
     */
    private fun addMessageToRecyclerView(item: ChatItem) {
        adapter.addMessage(item)
        binding.chatRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)

        if (binding.userMessageEditText.text.isNotBlank())
            binding.userMessageEditText.setText("")
    }
}