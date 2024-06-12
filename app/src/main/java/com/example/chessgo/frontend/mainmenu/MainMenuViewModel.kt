package com.example.chessgo.frontend.mainmenu

import androidx.lifecycle.ViewModel
import com.example.chessgo.backend.global.ClientManager
import com.google.firebase.auth.FirebaseAuth

class MainMenuViewModel : ViewModel() {
    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    fun getModerationPermissions(): Boolean {
        return ClientManager.getClient().isModerator
//        return true // REPLACE this flag after FOR DEBUG ONLY
    }
}