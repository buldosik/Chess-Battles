package com.example.chessgo.frontend.irlMenu.moderation

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.chessgo.backend.GameResult
import com.example.chessgo.frontend.irlMenu.myevents.EmptyItem
import com.example.chessgo.frontend.navigation.navigateToMainMenu
import kotlinx.coroutines.launch

private const val TAG = "ModerationCompose"

@Composable
fun ModerationScreen(navController: NavHostController = rememberNavController()) {
    val viewModel = remember { ModerationViewModel() }
    LaunchedEffect(Unit) {
        viewModel.getResults()
    }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()


    val onResultClick: (index: Int) -> Unit = {
        coroutineScope.launch {
            val firstItem = listState.firstVisibleItemIndex
            if (firstItem == it - 2) {
                listState.animateScrollToItem(it - 1)
            }
            else if (
                (firstItem != it && firstItem + 1 != it) || it == viewModel.results.size - 2) {
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
                text = "Results:",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Start
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                state = listState
            ) {
                itemsIndexed(viewModel.results) {index, item ->
                    if (index != viewModel.results.size - 1)
                        ResultItem(navController, item, index, viewModel, onResultClick)
                    else
                        EmptyItem()
                }
            }
        }
    }
}

@Composable
fun ResultItem(
    navController: NavHostController,
    gameResult: GameResult,
    index: Int,
    viewModel: ModerationViewModel,
    onResultClick: (index: Int) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isFirstImage by remember { mutableStateOf(false) }
    var isSecondImage by remember { mutableStateOf(false) }
    val firstImageSize = if (isFirstImage) 400.dp else if(isSecondImage) 0.dp else 150.dp
    val secondImageSize = if (isSecondImage) 400.dp else 150.dp

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
                if (!isExpanded) {
                    onResultClick(index)
                    viewModel.getImages(gameResult)
                }
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
                text = "Result id: " + gameResult.rid,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
            // TODO Add date of report???
//            val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm")
//            Text(
//                text = sdf.format(gameIRL.date),
//                color = MaterialTheme.colorScheme.onBackground,
//                style = MaterialTheme.typography.titleMedium,
//                textAlign = TextAlign.End
//            )
            AnimatedVisibility(visible = isExpanded) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(30.dp))
                    if (!isFirstImage && !isSecondImage) {
                        Text(
                            text = "Host result: " + viewModel.convertResult(gameResult.resultByHost),
                            modifier = Modifier.padding(bottom = 10.dp),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "Enemy result: " + viewModel.convertResult(gameResult.resultByEnemy),
                            modifier = Modifier.padding(bottom = 10.dp),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column (
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            if (!isSecondImage) {
                                Text(
                                    text = "Host image:",
                                    modifier = Modifier.padding(bottom = 10.dp),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                if (viewModel.imageHost != null) {
                                    Image(painter = rememberImagePainter(
                                        data = Uri.parse(viewModel.imageHost.toString())),
                                        contentDescription = "",
                                        modifier = Modifier
                                            .size(firstImageSize)
                                            .padding(bottom = 15.dp)
                                            .clickable { isFirstImage = !isFirstImage }
                                    )
                                    if (!isFirstImage && !isSecondImage) {
                                        Button(
                                            modifier = Modifier.padding(top = 10.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                backgroundColor = MaterialTheme.colorScheme.primary
                                            ),
                                            onClick = {
                                                viewModel.updateResult("You win", "Enemy win", gameResult)
                                                viewModel.results.remove(gameResult)
                                                isExpanded = !isExpanded
                                            }) {
                                            Text(text = "Accept", color = MaterialTheme.colorScheme.onPrimary)
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "Not added yet:",
                                        modifier = Modifier.padding(bottom = 10.dp),
                                        color = MaterialTheme.colorScheme.onBackground,
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column (
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "Enemy image:",
                                modifier = Modifier.padding(bottom = 10.dp),
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            if (viewModel.imageEnemy != null) {
                                Image(painter = rememberImagePainter(
                                    data = Uri.parse(viewModel.imageEnemy.toString())),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(secondImageSize)
                                        .padding(bottom = 15.dp)
                                        .clickable { isSecondImage = !isSecondImage }
                                )
                                if (!isFirstImage && !isSecondImage) {
                                    Button(
                                        modifier = Modifier.padding(top = 10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = MaterialTheme.colorScheme.primary
                                        ),
                                        onClick = {
                                            viewModel.updateResult("Enemy win", "You win", gameResult)
                                            viewModel.results.remove(gameResult)
                                            isExpanded = !isExpanded
                                        }) {
                                        Text(text = "Accept", color = MaterialTheme.colorScheme.onPrimary)
                                    }
                                }
                            } else {
                                Text(
                                    text = "Not added yet:",
                                    modifier = Modifier.padding(bottom = 10.dp),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    BackHandler {
        navController.navigateToMainMenu()
    }
}
