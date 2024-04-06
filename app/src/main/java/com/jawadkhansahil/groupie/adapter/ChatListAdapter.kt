package com.jawadkhansahil.groupie.adapter

import android.app.Activity
import android.content.Context
import android.os.Message
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.jawadkhansahil.groupie.R
import com.jawadkhansahil.groupie.models.ChatMessageModel
import com.jawadkhansahil.groupie.utils.FirebaseUtil
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Locale

class ChatListAdapter(private val options: FirestoreRecyclerOptions<ChatMessageModel>, val context: Activity) : FirestoreRecyclerAdapter<ChatMessageModel, ChatListAdapter.ChatViewHolder>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.chat_message_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: ChatMessageModel) {
        if (model.senderId.equals(FirebaseUtil.currentUserID())){
            holder.linearLayoutOther.visibility = View.GONE
            holder.linearLayoutMy.visibility = View.VISIBLE
            holder.myMessage.setText(model.message)
            holder.myMessageTime.setText(firebaseTimestampToTime(model.timestamp))
            setWidth(holder.myMessage)

        }else{
            holder.linearLayoutMy.visibility = View.GONE
            holder.linearLayoutOther.visibility = View.VISIBLE
            holder.otherMessage.setText(model.message)
            holder.otherUserMessageTime.setText(firebaseTimestampToTime(model.timestamp))
            setWidth(holder.otherMessage)
        }
    }

    private fun setWidth(message: TextView){
        val displayMetrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val maxPercentageWidth = 0.7 // Set your desired percentage here (e.g., 80%)

        val maxWidth = (screenWidth * maxPercentageWidth).toInt()
        message.maxWidth = maxWidth
    }
    private fun firebaseTimestampToTime(timestamp: Timestamp): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = timestamp.toDate()
        return sdf.format(date)
    }

    inner class ChatViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var linearLayoutOther: LinearLayout
        var linearLayoutMy: LinearLayout
        var myMessage: TextView
        var otherMessage: TextView
        var myMessageTime: TextView
        var otherUserMessageTime: TextView

        init {
            linearLayoutOther = itemView.findViewById(R.id.linearLayoutOther)
            linearLayoutMy = itemView.findViewById(R.id.linearLayoutMy)
            myMessage = itemView.findViewById(R.id.myMessage)
            otherMessage = itemView.findViewById(R.id.otherUserMessage)
            myMessageTime = itemView.findViewById(R.id.myMessageTime)
            otherUserMessageTime = itemView.findViewById(R.id.otherUserMessageTime)
        }
    }
}