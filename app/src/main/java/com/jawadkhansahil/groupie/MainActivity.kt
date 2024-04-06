package com.jawadkhansahil.groupie

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.jawadkhansahil.groupie.adapter.RecentChatAdapter
import com.jawadkhansahil.groupie.databinding.ActivityMainBinding
import com.jawadkhansahil.groupie.models.ChatroomModel
import com.jawadkhansahil.groupie.models.UserModel
import com.jawadkhansahil.groupie.utils.FirebaseUtil
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var userModel: UserModel? = null
    private var adapter: RecentChatAdapter? = null
    private var chatroomList: ArrayList<ChatroomModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val appID: Long = 208737475
        val appSign = "f598fcb564eb809c2d02815d0808de082801168d7d018ef790f80afa255022fe"

        FirebaseFirestore.getInstance().collection("Users").document(FirebaseUtil.currentUserID())
            .addSnapshotListener { value, error ->
                if (error == null) {
                    userModel = value?.toObject(UserModel::class.java)
                    initCallInviteService(
                        appID,
                        appSign,
                        userModel?.phoneNumber,
                        "${userModel?.phoneNumber}_username"
                    )
                }
            }


        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchActivity::class.java))
        }

        adapter = RecentChatAdapter(emptyList(), this)
        binding.recentChatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.recentChatRecyclerView.adapter = adapter

        FirebaseFirestore.getInstance().collection("Chatrooms").addSnapshotListener { value, error ->

            // Clear the list before adding new data
            chatroomList.clear()

            // Loop through the documents in the snapshot
            value?.documents?.forEach { document ->
                val chatroom = document.toObject(ChatroomModel::class.java)
                chatroom?.let {
                    chatroomList.add(it)
                }
            }

            adapter?.chatroomList = chatroomList
            adapter?.updateData(chatroomList)
        }
    }


    private fun initCallInviteService(appID: Long, appSign: String?, userID: String?, userName: String?) {
        val application = application
        val callInvitationConfig = ZegoUIKitPrebuiltCallInvitationConfig()
        ZegoUIKitPrebuiltCallService.init(
            application,
            appID,
            appSign,
            userID,
            userName,
            callInvitationConfig
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        ZegoUIKitPrebuiltCallService.unInit()
    }
}
