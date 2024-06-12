package com.example.chessgo.frontend.irlMenu.myevents

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.chessgo.backend.GameIRL
import com.example.chessgo.backend.global.ClientManager
import com.example.chessgo.backend.global.LoadDataCallback
import com.example.chessgo.frontend.navigation.navigateToMainMenu
import com.example.chessgo.frontend.navigation.navigateToResultScreen
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

private const val TAG = "MyEventsMenuCompose"

@Composable
fun MyEventsScreen(navController: NavHostController = rememberNavController()) {
    val viewModel = remember {ListingViewModel()}
    LaunchedEffect(Unit) {
        viewModel.getGames()
    }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val onGameClick: (index: Int) -> Unit = {
        coroutineScope.launch {
            val firstItem = listState.firstVisibleItemIndex
            if (firstItem == it - 2) {
                listState.animateScrollToItem(it - 1)
            }
            else if (
                (firstItem != it && firstItem + 1 != it) || it == viewModel.games.size - 2) {
                listState.animateScrollToItem(index = it + 1)
            }
            listState.animateScrollToItem(index = it)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Your games:",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Start
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                state = listState
            ) {
                itemsIndexed(viewModel.games) {index, item ->
                    if (index != viewModel.games.size - 1)
                        GameItem(navController, item, index, viewModel, onGameClick)
                    else
                        EmptyItem()
                }
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun GameItem(
    navController: NavHostController,
    gameIRL: GameIRL,
    index: Int,
    viewModel: ListingViewModel,
    onGameClick: (Int) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 600, // Adjust the duration as needed
                    easing = LinearOutSlowInEasing
                )
            )
            .fillMaxWidth()
            .padding(16.dp)
            .heightIn(min = 120.dp, max = if (isExpanded) 540.dp else 120.dp)
            .clickable {
                viewModel.currentGame = gameIRL
                if (!isExpanded)
                    onGameClick(index)
                isExpanded = !isExpanded
            },
        color = MaterialTheme.colorScheme.background,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
        shadowElevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = gameIRL.description,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )

            val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm")
            Text(
                text = sdf.format(gameIRL.date),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.End
            )
            AnimatedVisibility(visible = isExpanded) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(30.dp))

                    // Loading address
                    var isLoading by remember {
                        mutableStateOf(true)
                    }
                    var address by remember {
                        mutableStateOf("")
                    }

                    val loadDataCallback = object: LoadDataCallback<String> {
                        override fun onDataLoaded(response: String) {
                            address = response
                            isLoading = false
                        }

                        override fun onDataNotAvailable(errorCode: Int, reasonMsg: String) {
                            Log.d(TAG, "Error code: $errorCode, Message : $reasonMsg")
                        }
                    }
                    LaunchedEffect(key1 = viewModel.currentGame) {
                        viewModel.getAddress(context, loadDataCallback)
                    }
                    if (!isLoading) {
                        Text(
                            text = "Address: $address",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                    else {
                        CircularProgressIndicator()
                    }

                    PlaceOnMap(viewModel = viewModel)

                    // Result button
                    Button(
                        modifier = Modifier.padding(top = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colorScheme.primary
                        ),
                        onClick = {
                            ClientManager.userGameIRL = gameIRL
                            navController.navigateToResultScreen()
                        }) {
                        Text(text = "Result", color = MaterialTheme.colorScheme.onPrimary)
                    }

                    // SignOff button
                    Button(
                        modifier = Modifier.padding(top = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colorScheme.primary
                        ),
                        onClick = {
                            viewModel.removeCurrentGame()
                            isExpanded = !isExpanded
                            Log.d(TAG, viewModel.games.toString())
                    }) {
                        Text(text = "Sign off", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
    BackHandler {
        navController.navigateToMainMenu()
    }
}

@Composable
fun PlaceOnMap(viewModel: ListingViewModel) {
    val center = viewModel.currentGame.position
    val defaultCameraPosition = CameraPosition.fromLatLngZoom(center, 14f)
    // Observing and controlling the camera's state can be done with a CameraPositionState
    val cameraPositionState = rememberCameraPositionState {
        position = defaultCameraPosition
    }
    val mapProperties by remember { mutableStateOf(MapProperties(mapType = MapType.NORMAL)) }
    Column {
        Box(modifier = Modifier
            .size(250.dp)
            .padding(top = 10.dp)
            .align(Alignment.CenterHorizontally)
        ){
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                googleMapOptionsFactory = {
                    GoogleMapOptions().mapId("DEMO_MAP_ID")
                },
                cameraPositionState = cameraPositionState,
                properties = mapProperties,
                onPOIClick = {},
                onMapClick = {}
            ) {
                Marker(
                    state = MarkerState(position = viewModel.currentGame.position),
                )
            }
        }
    }
}

@Composable
fun EmptyItem() {
    Box(modifier = Modifier.height(300.dp))
}