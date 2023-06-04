package tw.app.chatgpt.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import tw.app.chatgpt.adapter.model.ChatItem
import tw.app.chatgpt.adapter.model.DataType
import tw.app.chatgpt.adapter.model.SENDER
import tw.app.chatgpt.databinding.ItemQuestionBinding
import tw.app.chatgpt.databinding.ItemResponseBinding

class ChatAdapter(
    val messages: MutableList<ChatItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_VIEW_TYPE_HORIZONTAL = 123
    private val ITEM_VIEW_TYPE_VERTICAL = 456

    public fun addMessage(message: ChatItem) {
        messages.add(message)
        notifyItemInserted(messages.size)
    }

    /**
     * This Function will help you out in choosing whether you want vertical or horizontal VIEW TYPE
     */
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).senderType) {
            SENDER.ASSISTANT -> ITEM_VIEW_TYPE_HORIZONTAL
            SENDER.USER -> ITEM_VIEW_TYPE_VERTICAL
            else -> 0
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    /**
     * The View Type Selected above will help this function in choosing appropriate ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HORIZONTAL -> HorizontalViewHolder.from(parent)
            ITEM_VIEW_TYPE_VERTICAL -> VerticalViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    /**
     * The View Holder Created above are used here.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HorizontalViewHolder -> {
                val item = getItem(position) as DataType.HorizontalClass
                holder.bind(item.yourData)
            }
            is VerticalViewHolder -> {
                val item = getItem(position) as DataType.VerticalClass
                holder.bind(item.yourData)
            }
        }
    }

    private fun getItem(position: Int): ChatItem {
        return messages[position]
    }

    /**
     * Vertical View Holder Class
     */
    class VerticalViewHolder private constructor(val binding: ItemQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChatItem) {
            binding.messageText.text = item.message
        }

        companion object {
            fun from(parent: ViewGroup): VerticalViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view =  ItemQuestionBinding.inflate(layoutInflater, parent, false)
                return VerticalViewHolder(view)
            }
        }
    }

    /**
     * Horizontal View Holder
     */
    class HorizontalViewHolder private constructor(val binding: ItemResponseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChatItem) {
            binding.messageText.text = item.message
        }

        companion object {
            fun from(parent: ViewGroup): HorizontalViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =  ItemResponseBinding.inflate(layoutInflater, parent, false)
                return HorizontalViewHolder(binding)
            }
        }
    }
}

/*
class ListCheckDiffCallback : DiffUtil.ItemCallback<ChatItem>() {
    override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
        return oldItem == newItem
    }
}*/
