package tw.app.chatgpt.adapter.model

import java.util.UUID

open class ChatItem(
    open var id: String = UUID.randomUUID().toString(),
    open var senderType: SENDER = SENDER.USER,
    open var message: String = "",
    open var sentAt: Long = 0
)

sealed class DataType {
    abstract val id: String
    abstract val senderType: SENDER
    abstract val message: String
    abstract val sentAt: Long

    data class HorizontalClass(val yourData: ChatItem): ChatItem(
        yourData.id,
        yourData.senderType,
        yourData.message,
        yourData.sentAt
    ) {
        override var id = yourData.id
        override var senderType = SENDER.ASSISTANT
        override var message = yourData.message
        override var sentAt: Long = yourData.sentAt
    }

    data class VerticalClass(val yourData: ChatItem) : ChatItem() {
        override var id = yourData.id
        override var senderType = SENDER.USER
        override var message = yourData.message
        override var sentAt: Long = yourData.sentAt
    }
}

enum class SENDER {
    ASSISTANT,
    USER
}