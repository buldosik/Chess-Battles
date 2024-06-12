package com.example.chessgo.backend.moderation

import android.net.Uri
import android.util.Log
import com.example.chessgo.backend.GameResult
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

private const val TAG = "ModerationEventsManager"

class ModerationEventsManager {
    private val firestore = Firebase.firestore
    private val storage = Firebase.storage

    fun getAllResults(callback: (MutableList<GameResult>) -> Unit) {
        firestore.collection("results")
            .get()
            .addOnSuccessListener { documents ->
                val gamesResultList = mutableListOf<GameResult>()

                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    if (document.id.toString() == "MRAJRIaqX7nZ2wwuKb6b")
                        continue
                    val result = GameResult.toResult(document)
//                    Log.d(TAG, "ID: ${result.rid}, host: ${result.host}, enemy: ${result.enemy}")
                    if (result.moderationNeeded) {
                        gamesResultList.add(result)
                    }
                }
                callback(gamesResultList)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                callback(mutableListOf<GameResult>())
            }
    }

    fun getImageFromPath(path: String, callback: (Uri?) -> Unit) {
        if (path == "") {
            callback(null)
        } else {
            val imageRef = storage.reference.child(path)
            imageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.d(TAG, "SUCCESS: loading host image")
                    callback(uri)
                }
                .addOnFailureListener { exception ->
                    exception.message?.let { message -> Log.d(TAG, message) }
                    Log.d(TAG, "FAILURE: loading host image")
                    callback(null)
                }
        }
    }

    fun updateResult(resultByHost: String, resultByEnemy: String, gameResult: GameResult) {
        val document = firestore.collection("results").document(gameResult.rid)
        document.get()
            .addOnSuccessListener {
                document.update("resultByHost", resultByHost)
                document.update("resultByEnemy", resultByEnemy)
                document.update("approvesNum", gameResult.approvesNum + 1)
            }
            .addOnFailureListener() {
                // TODO
            }
    }
}