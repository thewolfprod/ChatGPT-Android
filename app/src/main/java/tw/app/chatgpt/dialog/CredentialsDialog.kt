package tw.app.chatgpt.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tw.app.chatgpt.databinding.DialogCredentialsBinding
import kotlin.system.exitProcess


class CredentialsDialog(
    context: Context,
    private val listener: CredentialsDialogListener
) {
    private var dialog: AlertDialog
    private var binding: DialogCredentialsBinding

    init {
        binding = DialogCredentialsBinding.inflate(LayoutInflater.from(context))

        val materialDialog = MaterialAlertDialogBuilder(context)
        materialDialog.setTitle("API")
        materialDialog.setMessage(
            "Enter your API key to use the app! A guide on how to get your API key can be found on the app's GitHub page."
        )
        materialDialog.setView(binding.root)
        materialDialog.setPositiveButton("Save", null)
            .setNegativeButton(
                "Quit"
            ) { _, _ ->
                listener.onDismiss()
            }

        dialog = materialDialog.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val apiKey = binding.apiKeyEditText.text.toString()
                if (apiKey.length != 50) {
                    binding.apiKeyInputLayout.error = "API Key is 50 char long!"
                    binding.apiKeyInputLayout.isErrorEnabled = true
                } else {
                    listener.onPositiveButtonClicked(apiKey)
                    dialog.dismiss()
                }
            }
        }
    }

    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    fun show() {
        if (!dialog.isShowing) {
            dialog.show()
        }
    }
}

interface CredentialsDialogListener {
    fun onPositiveButtonClicked(apiKey: String)

    fun onDismiss() {
        exitProcess(0)
    }
}