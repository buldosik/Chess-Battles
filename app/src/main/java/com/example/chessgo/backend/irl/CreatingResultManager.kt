package com.example.chessgo.backend.irl

import android.util.Log
import com.example.chessgo.backend.GameResult
import com.example.chessgo.backend.global.ClientManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

private const val TAG = "CreatingResultManager"

class CreatingResultManager {
    private val firestore = Firebase.firestore
    private val uid = ClientManager.getClient().uid

    private fun getResult(callback: (GameResult?) -> Unit) {
        firestore.collection("results")
            .get()
            .addOnSuccessListener { documents ->
                val uid = ClientManager.getClient().uid
                val gameIRL = ClientManager.userGameIRL
                for (document in documents) {
                    if (document.id.toString() == "MRAJRIaqX7nZ2wwuKb6b")
                        continue
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val result = GameResult.toResult(document)
                    Log.d(TAG, "ID: ${result.rid}, host: ${result.host}, enemy: ${result.enemy}")
                    if ((result.host == uid || result.enemy == uid) && result.gid == gameIRL.gid) {
                        callback(result)
                    }
                }
                callback(null)
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                callback(null)
            }
    }

    fun getImage() {
        getResult {
            if (it != null) {
                val storage = Firebase.storage
                val storageRef : StorageReference = if (it.host == uid) {
                    storage.reference.child(it.imageByHost)
                } else {
                    storage.reference.child(it.imageByEnemy)
                }

                storageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        ClientManager.pictureUri = uri
                    }
                    .addOnFailureListener { exception ->
                        exception.message?.let { message -> Log.d(TAG, message) }
                    }
            }
        }
    }

    fun isResultExist() : Boolean {
        var bool = false
        getResult {
            bool = it != null
            Log.d(TAG, "Bool: $bool")
        }
        return bool
    }

    fun addResultToFirestore(result: String, status: String) {
        var gameResult: GameResult
        val gameIRL = ClientManager.userGameIRL
        val image = ClientManager.currentPicture
        getResult {
            if (it == null) {
                gameResult = GameResult (
                    gid = gameIRL.gid,
                    date = gameIRL.date,
                    host = gameIRL.host,
                    enemy = gameIRL.enemy,
                    status = status
                )
                if (gameIRL.host == uid) {
                    gameResult.resultByHost = result
                    gameResult.imageByHost = image
                }
                else {
                    gameResult.resultByEnemy = result
                    gameResult.imageByEnemy = image
                }
                firestore.collection("results")
                    .add(gameResult)
                    .addOnSuccessListener { documentRef ->
                        Log.d(TAG, "DocumentSnapshot written with ID: ${documentRef.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                        // ToDo throw error
                    }
            }
            else {
                val document = firestore.collection("results").document(it.rid)
                val currentResult = it
                document.get()
                    .addOnSuccessListener {
                        if (currentResult.host == uid) {
                            document.update("imageByHost", image)
                        } else {
                            document.update("imageByEnemy", image)
                        }
                    }
                    .addOnFailureListener() {
                        // TODO
                    }
            }
        }

    }
}