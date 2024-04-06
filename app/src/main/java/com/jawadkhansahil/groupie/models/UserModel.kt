package com.jawadkhansahil.groupie.models

import com.google.firebase.Timestamp

class UserModel(
    val userId : String,
    val phoneNumber: String,
    var userName: String,
    var profilePhoto: String,
    val timeStamp: Timestamp
) {
    // Add a default (no-argument) constructor
    constructor() : this("", "", "","", Timestamp.now())
}

