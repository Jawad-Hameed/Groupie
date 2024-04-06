package com.jawadkhansahil.groupie

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.jawadkhansahil.groupie.models.UserModel
import com.jawadkhansahil.groupie.utils.AndroidUtil
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallFragment

class CallActivity : AppCompatActivity() {

    lateinit var model: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        model = AndroidUtil.getUserModelAsIntent(intent)

        addFragment()
    }

    private fun addFragment() {
        val appID:Long = 208737475
        val appSign = "f598fcb564eb809c2d02815d0808de082801168d7d018ef790f80afa255022feYour app sign"
        val callID = model.userId
        val userID = model.phoneNumber
        val userName = userID + "_Name";
        val config = ZegoUIKitPrebuiltCallConfig.oneOnOneVoiceCall();
        val fragment = ZegoUIKitPrebuiltCallFragment.newInstance(appID, appSign,
        userID, userName, callID, config);
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commitNow();
    }
}