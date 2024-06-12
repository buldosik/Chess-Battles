package com.example.chessgo.frontend.mainmenu

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.chessgo.R
import com.example.chessgo.frontend.navigation.*
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainMenuScreen(navController: NavHostController = rememberNavController() ) {

    val viewModel = remember { MainMenuViewModel() }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val toggleDrawerState: () -> Unit = {
        scope.launch {
            if (drawerState.isOpen)
                drawerState.close()
            else
                drawerState.open()
        }
    }

    BackHandler {
        if(drawerState.isOpen)
            toggleDrawerState()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideBar(navController, { viewModel.signOut() }, toggleDrawerState, viewModel.getModerationPermissions())
        },
        content = {
            Column {
                TopBar (toggleDrawerState)
                MainContent(navController = navController)
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TopBar(toggleDrawerState: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val iconPainter = painterResource(id = R.drawable.pawn_icon)
                    Image(
                        modifier = Modifier
                            .height(50.dp)
                            .padding(vertical = 2.dp),
                        painter = iconPainter,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(id = R.string.app_name),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = toggleDrawerState) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Toggle drawer",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },

        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        )
    )
}

@Composable
fun SideBar(navController: NavHostController, signOut: () -> Unit, toggleDrawerState: () -> Unit, isModerator: Boolean) {
    val itemsManager = SideMenuItemsManager(signOut)
    val filteredItemsList = if (isModerator) {
        itemsManager.itemsList
    } else {
        itemsManager.itemsList.filterNot {it is ModerationMenu}
    }
    ModalDrawerSheet {
        val imagePainter = painterResource(id = R.drawable.enter_screen_figures)
        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )
        Spacer(Modifier.height(12.dp))
        filteredItemsList.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground) },
                label = { Text(item.title) },
                selected = false,
                onClick = {
                    toggleDrawerState()
                    item.onClick(navController)
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}

@Composable
fun MainContent(navController: NavHostController){
    val itemManager = MainMenuItemsManager()
    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()

            ) {
            items(itemManager.itemsList.size) {item ->
                MainContentItem(navController = navController, item = itemManager.itemsList[item])
            }
        }
    }
}

@Composable
fun MainContentItem(navController: NavHostController, item: MainMenuItem) {
    ElevatedButton(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp),
        shape = MaterialTheme.shapes.small,
        onClick = { item.onClick(navController) },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            //disabledContainerColor = MaterialTheme.colorScheme.primary,
            //disabledContentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
        ) {
            val imagePainter = painterResource(id = item.imageID)
            Image(
                painter = imagePainter,
                contentDescription = null,
                modifier = Modifier
                    .height(150.dp)
                    .padding(end = 10.dp),
            )
            Column {
                Row {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    /*Icon(
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondary
                    )*/
                }
                Text(
                    modifier = Modifier.padding(top = 6.dp),
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MainMenuScreen()
}