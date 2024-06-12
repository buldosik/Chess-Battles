package com.example.chessgo.backend

import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

class GameResult (
    val rid : String = "",
    val gid : String = "",
    val date : Date,
    val host : String = "",
    val enemy : String = "",
    val status : String = "",
    var resultByHost : String = "",
    var imageByHost : String = "",
    var resultByEnemy : String = "",
    var imageByEnemy : String = "",
    val moderationNeeded : Boolean = false,
    val approvesNum : Long = 0
) {
    companion object {
        fun toResult(document: DocumentSnapshot) : GameResult {
            val rid = document.id
            val gid = document.get("gid") as String
            val date = document.getDate("date")
            val host = document.get("host") as String
            val enemy = document.get("enemy") as String
            val status = document.get("status") as String
            val resultByHost = document.get("resultByHost") as String
            val imageByHost = document.get("imageByHost") as String
            val resultByEnemy = document.get("resultByEnemy") as String
            val imageByEnemy = document.get("imageByEnemy") as String
            val moderationNeeded = document.get("moderationNeeded") as Boolean
            val approvesNum = document.get("approvesNum") as Long

            return GameResult(
                rid = rid,
                gid = gid,
                date = date!!,
                host = host,
                enemy = enemy,
                status = status,
                resultByHost =resultByHost,
                imageByHost = imageByHost,
                resultByEnemy = resultByEnemy,
                imageByEnemy = imageByEnemy,
                moderationNeeded = moderationNeeded,
                approvesNum = approvesNum
            )
        }
    }
}