package com.jawadkhansahil.groupie.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jawadkhansahil.groupie.ChatActivity
import com.jawadkhansahil.groupie.R
import com.jawadkhansahil.groupie.models.UserModel
import com.jawadkhansahil.groupie.utils.AndroidUtil
import com.jawadkhansahil.groupie.utils.FirebaseUtil
import de.hdodenhof.circleimageview.CircleImageView

class SearchUserAdapter(private val options: FirestoreRecyclerOptions<UserModel>, val context: Activity) : FirestoreRecyclerAdapter<UserModel, SearchUserAdapter.SearchViewHolder>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
       val view  = LayoutInflater.from(parent.context).inflate(R.layout.search_list_item, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int, model: UserModel) {
        holder.userName.setText(model.userName)
        holder.phoneNumber.setText(model.phoneNumber)
        Glide.with(context).load(model.profilePhoto).placeholder(R.drawable.person).into(holder.profilePhoto)

        if (model.userId == FirebaseUtil.currentUserID()){
            holder.userName.setText("${model.userName} (Me)")
        }

        holder.itemView.setOnClickListener {
            closeKeyboard()
            val intent = Intent(context, ChatActivity::class.java)
            AndroidUtil.passUserModelAsIntent(intent, model)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            context.finish()
        }
    }


    fun closeKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    inner class SearchViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var userName: TextView
        var phoneNumber: TextView
        var profilePhoto: CircleImageView
        init {
            userName = itemView.findViewById(R.id.userNameText)
            phoneNumber = itemView.findViewById(R.id.phoneNumberText)
            profilePhoto = itemView.findViewById(R.id.circleImageView)
        }
    }
}