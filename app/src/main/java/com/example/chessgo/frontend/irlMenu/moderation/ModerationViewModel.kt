package com.example.chessgo.frontend.irlMenu.moderation

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.chessgo.backend.GameResult
import com.example.chessgo.backend.global.TimeConverter
import com.example.chessgo.backend.moderation.ModerationEventsManager
import java.time.LocalDateTime

private const val TAG = "ModerationViewModel"

class ModerationViewModel: ViewModel() {
    val moderationEventsManager = ModerationEventsManager()
    val results = mutableStateListOf<GameResult>()
    var imageHost: Uri? = null
    var imageEnemy: Uri? = null

    fun getResults() {
        Log.d(TAG, "GETTING RESULTS")
        results.clear()
        moderationEventsManager.getAllResults { gameResults : MutableList<GameResult> ->
            gameResults.forEach {
                Log.d(TAG, "${it.rid}: ADDED")
                results.add(it)
            }
            results.add(
                GameResult(
                    date = TimeConverter.localDateTimeToDate(LocalDateTime.now())
                )
            )
        }
    }

    fun getImages(result: GameResult) {
        Log.d(TAG, "GETTING IMAGES")
        moderationEventsManager.getImageFromPath(result.imageByHost) {
            imageHost = it
        }
        moderationEventsManager.getImageFromPath(result.imageByEnemy) {
            imageEnemy = it
        }
    }

    fun convertResult(input: String) : String {
        val output = when (input) {
            "You win" -> {
                "Win"
            }
            "Draw" -> {
                "Draw"
            }
            "Enemy win" -> {
                "Lose"
            }
            else -> {
                return "Unknown"
            }
        }

        return output
    }

    fun updateResult(resultByHost: String, resultByEnemy: String, gameResult: GameResult) {
        moderationEventsManager.updateResult(resultByHost, resultByEnemy, gameResult)
    }
}