package com.jawadkhansahil.groupie

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.storage.storage
import com.jawadkhansahil.groupie.databinding.ActivityUsernameBinding
import com.jawadkhansahil.groupie.models.UserModel
import com.jawadkhansahil.groupie.utils.FirebaseUtil
import com.tencent.mmkv.MMKV
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService

class UsernameActivity : AppCompatActivity() {

    lateinit var binding: ActivityUsernameBinding
    private val storage = Firebase.storage
    private val imagesRef = storage.reference.child("images")
    var progressDialog: ProgressDialog? = null
    var userModel: UserModel? = null
    var profilePhoto: String = ""
    var phoneNumber: String? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                binding.profilePhoto.setImageURI(imageUri)
                uploadImageToFirebaseStorage(imageUri!!)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        binding = ActivityUsernameBinding.inflate(layoutInflater)
        setContentView(binding.root)


        phoneNumber = intent.getStringExtra("phoneNumber")

        binding.profilePhoto.setOnClickListener {
            openImageChooser()
        }

        getUserName()

        binding.saveUserButton.setOnClickListener {
            setUserName()
        }

    }

    private fun setUserName() {
        showProgressDialog("Initializing. . .")
        val userName = binding.userName.text.toString()
        if (userName.isEmpty() || userName.length < 3) {
            progressDialog!!.dismiss()
            binding.userName.setError("Username length should be at least 3 char")
        } else {

            if (userModel != null) {
                userModel!!.userName = userName
                userModel!!.profilePhoto = userModel!!.profilePhoto
            } else {
                userModel = UserModel(FirebaseUtil.currentUserID(), phoneNumber!!, userName, profilePhoto, Timestamp.now())
                userModel = UserModel(FirebaseUtil.currentUserID(), phoneNumber!!, userName, profilePhoto, Timestamp.now())
            }

            FirebaseUtil.currentUserDetails().set(userModel!!).addOnCompleteListener {
                progressDialog!!.dismiss()
                if (it.isSuccessful) {

                    val intent = Intent(this@UsernameActivity, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            }
        }

    }

    private fun getUserName() {
        showProgressDialog("Loading. . .")
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener {
            progressDialog!!.dismiss()
            if (it.isSuccessful) {
                userModel = it.result.toObject(UserModel::class.java)

                if (userModel != null) {
                    binding.userName.setText(userModel!!.userName)
                    Glide.with(this@UsernameActivity).load(userModel!!.profilePhoto).placeholder(R.drawable.person)
                        .into(binding.profilePhoto)
                }
            }
        }
    }

    private fun showProgressDialog(message: String) {
        progressDialog = ProgressDialog(this@UsernameActivity)
        progressDialog!!.setMessage(message)
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        pickImage.launch(intent)
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        showProgressDialog("Uploading Profile. . .")

        val imageRef = imagesRef.child("${System.currentTimeMillis()}_image")
        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            progressDialog!!.dismiss()
            if (task.isSuccessful) {
                val downloadUri = task.result
                profilePhoto = downloadUri.toString()
                Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }
    }
}