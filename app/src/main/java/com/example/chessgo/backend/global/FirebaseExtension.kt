package com.example.chessgo.backend.global

import com.example.chessgo.backend.User
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

fun FirebaseUser.toUser(): Task<User> {
    val userId = uid
    val firestore = Firebase.firestore
    val docRef = firestore.collection("users").document(userId)

    return docRef.get().continueWithTask { task ->
        if (task.isSuccessful) {
            val userData = task.result?.data

            val username = userData?.get("userName") as? String ?: ""
            val email = userData?.get("email") as? String ?: ""
            val isModerator = userData?.get("moderator") as? Boolean ?: false

            return@continueWithTask Tasks.forResult(User(uid = userId, username = username, email = email, isModerator = isModerator))
        } else {
            throw task.exception ?: Exception("Error fetching user data")
        }
    }
}