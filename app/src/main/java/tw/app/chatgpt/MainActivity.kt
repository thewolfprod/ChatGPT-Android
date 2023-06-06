package tw.app.chatgpt

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
import tw.app.chatgpt.dialog.CredentialsDialog
import tw.app.chatgpt.dialog.CredentialsDialogListener
import tw.app.chatgpt.R.string as AppString

class MainActivity : BaseActivity() {

    private var _credentialsDialog: CredentialsDialog? = null
    private val credentialsDialog get() = _credentialsDialog!!

    private var _credentialsDialogListener: CredentialsDialogListener? = null
    private val credentialsDialogListener get() = _credentialsDialogListener!!

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var _apiCall: ApiCall? = null
    private val apiCall get() = _apiCall!!

    private var _apiCallListener: OnApiCallResponse? = null
    private val apiCallListener get() = _apiCallListener!!

    private var _adapter: ChatAdapter? = null
    private val adapter get() = _adapter!!

    private var _settings: Settings? = null
    private val settings get() = _settings!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)

        _settings = Settings(getSharedPreferences("app_settings", MODE_PRIVATE))

        _credentialsDialogListener = object : CredentialsDialogListener {
            override fun onPositiveButtonClicked(apiKey: String) {
                settings.setApiKey(apiKey)
                start()
            }
        }

        _credentialsDialog = CredentialsDialog(
            this@MainActivity,
            credentialsDialogListener
        )

        if (settings.getApiKey() == null && getString(AppString.APIKey).isBlank()) {
            credentialsDialog.show()
        } else {
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
        _adapter = null
        _apiCall = null
        _apiCallListener = null
    }

    private fun start() {
        setContentView(binding.root)
        setup()
        init()
    }

    private fun setup() {
        _adapter = ChatAdapter(arrayListOf())
        binding.chatRecyclerView.adapter = adapter

        _apiCallListener = object : OnApiCallResponse {
            override fun onSuccess(response: GPTResponse) {
                runOnUiThread {
                    binding.userMessageEditText.isEnabled = true
                    binding.sendButton.isEnabled = true

                    if (binding.userMessageEditText.text.isNotBlank())
                        binding.userMessageEditText.setText("")

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
                            message = "Error Occurred!\n${error.message ?: "Unknown Error!"}",
                            sentAt = 0
                        )
                    )

                    addMessageToRecyclerView(responseItem)
                }
            }
        }

        _apiCall = ApiCall(
            apiKey = settings.getApiKey()!!,
            apiHost = getString(AppString.APIHost),
            settings = settings,
            listener = apiCallListener
        )
    }

    private fun init() {
        binding.sendButton.setOnClickListener {
            if (binding.userMessageEditText.text.toString().isNotBlank()) {
                binding.userMessageEditText.isEnabled = false
                binding.sendButton.isEnabled = false

                val chatItem = DataType.VerticalClass(
                    ChatItem(
                        senderType = SENDER.USER,
                        message = binding.userMessageEditText.text.toString(),
                        sentAt = 0
                    )
                )

                addMessageToRecyclerView(chatItem)

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

    private fun addMessageToRecyclerView(item: ChatItem) {
        adapter.addMessage(item)
        binding.chatRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)
    }
}