package com.example.chessgo.frontend.irlMenu.result

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.chessgo.backend.global.ClientManager
import com.example.chessgo.backend.global.PhotoPickerManager
import com.example.chessgo.backend.irl.CreatingResultManager
import com.example.chessgo.backend.registration.Results
import com.example.chessgo.frontend.camera.CameraScreen
import com.example.chessgo.frontend.navigation.navigateToMyEventsMenu

private const val TAG = "ResultEvents"


@Composable
fun ResultScreen(navController: NavController = rememberNavController()) {
    val manager = remember { CreatingResultManager() }
    val context = LocalContext.current
    val resultForGame = remember { mutableStateOf("") }
    val status = remember { mutableStateOf("") }
    var isResultExist = false
    var isCameraVisible by remember {
        mutableStateOf(false)
    }
    val toggleCameraVisible: () -> Unit = {
        isCameraVisible = !isCameraVisible
    }

    val changeResult: (value : String) -> Unit = {
        resultForGame.value = it
    }

    val changeStatus: (value : String) -> Unit = {
        status.value = it
    }

    LaunchedEffect(Unit) { isResultExist = manager.isResultExist() }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 15.dp, bottom = 25.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (ClientManager.getClient().isModerator) {
                Text(text = "Result Compose", color = MaterialTheme.colorScheme.onBackground)
            }
            Text(text = "Result", color = MaterialTheme.colorScheme.onBackground)

            ButtonsInLine(true, changeResult)
            ButtonsInLine(false, changeStatus)

            Button(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colorScheme.primary
                ),
                onClick = {
                    toggleCameraVisible()
                }) {
                Text(
                    text = "Add image",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            if (ClientManager.pictureUri == null)
                LaunchedEffect(Unit) { manager.getImage() }
            if (ClientManager.pictureUri != null) {
                Text(text = "Your image:")
                Image(painter = rememberImagePainter(
                    data = Uri.parse(ClientManager.pictureUri.toString())),
                    contentDescription = "",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(15.dp)
                )
            }
            Button(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colorScheme.primary
                ),
                onClick = {
                    if (!isResultExist && (resultForGame.value == "" || status.value == "" || !ClientManager.isPictureChanged)) {
                        Toast.makeText(context, "Pick status of game, game status and image", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    Log.d(TAG, "${ClientManager.pictureUri}")
                    PhotoPickerManager.uploadImageToStorage(ClientManager.pictureUri, context) { result ->
                        when (result) {
                            is Results.Success -> {
                                Log.d(TAG, "${ClientManager.pictureUri}")
                                manager.addResultToFirestore(resultForGame.value, status.value)
                                ClientManager.pictureUri = null
                                ClientManager.isPictureChanged = false
                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                            }
                            is Results.Failure -> {
                                val exception = result.exception
                                Log.w(TAG, "safeUserToStore:failure", exception)
                                Toast.makeText(context, "Failed to safe image", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Log.d(TAG, "PHOTO PICKER ERROR")
                            }
                        }
                    }
                    navController.navigateToMyEventsMenu()
                }) {
                Text(
                    text = "Submit",
                    color = MaterialTheme.colorScheme.onPrimary
                )
                BackHandler {
                    ClientManager.pictureUri = null
                    navController.navigateToMyEventsMenu()
                }
            }
        }
        if (isCameraVisible) {
            CameraScreen(toggleCameraVisible)
        }
    }
}

@Composable
fun ButtonsInLine(isFirstRow: Boolean, changeResult: (value: String) -> Unit) {
    var isButton1Pressed by remember { mutableStateOf(false) }
    var isButton2Pressed by remember { mutableStateOf(false) }
    var isButton3Pressed by remember { mutableStateOf(false) }

    val title1 = if (isFirstRow) "Canceled" else "You win"
    val title2 = if (isFirstRow) "Finished" else "Draw"
    val title3 = if (isFirstRow) "No Enemy" else "Enemy win"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Button(
            onClick = {
                isButton1Pressed = true
                isButton2Pressed = false
                isButton3Pressed = false
                changeResult(title1)
            },
            modifier = Modifier
                .height(40.dp)
                .weight(1f)
                .background(Color.Transparent),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent
            )
        ) {
            Text(
                title1,
                textDecoration = if (isButton1Pressed) TextDecoration.Underline else TextDecoration.None
            )
        }

        Button(
            onClick = {
                isButton1Pressed = false
                isButton2Pressed = true
                isButton3Pressed = false
                changeResult(title2)
            },
            modifier = Modifier
                .height(40.dp)
                .weight(1f)
                .background(Color.Transparent),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent
            )
        ) {
            Text(
                title2,
                textDecoration = if (isButton2Pressed) TextDecoration.Underline else TextDecoration.None
            )
        }

        Button(
            onClick = {
                isButton1Pressed = false
                isButton2Pressed = false
                isButton3Pressed = true
                changeResult(title3)
            },
            modifier = Modifier
                .height(40.dp)
                .weight(1f)
                .background(Color.Transparent),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent
            )
        ) {
            Text(
                title3,
                textDecoration = if (isButton3Pressed) TextDecoration.Underline else TextDecoration.None
            )
        }
    }
}

