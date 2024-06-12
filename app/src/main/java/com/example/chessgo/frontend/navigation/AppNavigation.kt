package com.example.chessgo.frontend.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.chessgo.frontend.irlMenu.IRLMenuScreen
import com.example.chessgo.frontend.irlMenu.creating.CreatingScreen
import com.example.chessgo.frontend.irlMenu.moderation.ModerationScreen
import com.example.chessgo.frontend.irlMenu.myevents.MyEventsScreen
import com.example.chessgo.frontend.irlMenu.result.ResultScreen
import com.example.chessgo.frontend.irlMenu.searching.SearchingScreen
import com.example.chessgo.frontend.loading.LoadingScreen
import com.example.chessgo.frontend.mainmenu.MainMenuScreen
import com.example.chessgo.frontend.onlinegame.OnlineMenuScreen
import com.example.chessgo.frontend.privacypolicy.PrivacyPolicy
import com.example.chessgo.frontend.registration.enterApp.GreetingScreen
import com.example.chessgo.frontend.registration.forgotPassword.ForgotPasswordScreen
import com.example.chessgo.frontend.registration.sign_in.SignInScreen
import com.example.chessgo.frontend.registration.sign_up.SignUpScreen

/*
 !!! IMPORTANT !!!
 if you adding new screen you need
 1) add object in Screen
 2) add it to list screens
 3) add handle for that screen
 4) add function to navigate to that screen
 */

sealed class Screen(val route: String) {

    object EnteringScene : Screen("EnteringScreen")
    object SignInScene : Screen("SignInScreen")
    object SignUpScene : Screen("SignUpScreen")
    object ForgotPassword : Screen("ForgotPasswordScreen")
    object Loading : Screen("LoadingScreen")
    object MainMenu : Screen("MainMenuScreen")
    object IrlMenu : Screen("IrlMenuScreen")
    object CreatingMenu : Screen("CreatingMenuScreen")
    object SearchingMenu : Screen("SearchingScreen")
    object MyEventsMenu : Screen("MyEventsScreen")
    object OnlineMenu : Screen("OnlineMenuScreen")
    object PrivacyPolicy: Screen("PrivacyPolicyScreen")
    object ResultMenu: Screen("ResultScreen")
    object ModerationMenu: Screen("ModerationScreen")

}

val screens = listOf(
    Screen.EnteringScene,
    Screen.SignInScene,
    Screen.SignUpScene,
    Screen.ForgotPassword,
    Screen.Loading,
    Screen.MainMenu,
    Screen.IrlMenu,
    Screen.CreatingMenu,
    Screen.SearchingMenu,
    Screen.MyEventsMenu,
    Screen.OnlineMenu,
    Screen.PrivacyPolicy,
    Screen.ResultMenu,
    Screen.ModerationMenu
)

@Composable
fun HandleScreen(screen: Screen, navController: NavHostController) {
    when (screen) {
        is Screen.EnteringScene -> GreetingScreen(navController = navController)
        is Screen.SignInScene -> SignInScreen(navController = navController)
        is Screen.SignUpScene -> SignUpScreen(navController = navController)
        is Screen.ForgotPassword -> ForgotPasswordScreen(navController = navController)

        is Screen.Loading -> LoadingScreen(navController = navController)

        is Screen.MainMenu -> MainMenuScreen(navController = navController)
        //
        is Screen.IrlMenu -> IRLMenuScreen(navController = navController)
        is Screen.CreatingMenu -> CreatingScreen(navController = navController)
        is Screen.SearchingMenu -> SearchingScreen(navController = navController)
        // ToDo Update compose
        is Screen.MyEventsMenu -> MyEventsScreen(navController = navController)

        is Screen.OnlineMenu -> OnlineMenuScreen(navController = navController)
        //added
//        is Screen.CameraTesting -> CameraScreen(navController = navController)

        is Screen.PrivacyPolicy -> PrivacyPolicy(navController = navController)

        is Screen.ResultMenu -> ResultScreen(navController = navController)
        is Screen.ModerationMenu -> ModerationScreen(navController = navController)

    }
}



fun NavController.navigateToEnteringScreen() {
    navigate("EnteringScreen")
}
fun NavController.navigateToSignIn() {
    navigate("SignInScreen")
}
fun NavController.navigateToSignUp() {
    navigate("SignUpScreen")
}
fun NavController.navigateToForgotPassword() {
    navigate("ForgotPasswordScreen")
}

fun NavController.navigateToLoading() {
    navigate("LoadingScreen")
}

fun NavController.navigateToMainMenu() {
    navigate("MainMenuScreen")
}

fun NavController.navigateToCreatingMenu() {
    navigate("CreatingMenuScreen")
}
fun NavController.navigateToSearchingMenu() {
    navigate("SearchingScreen")
}
fun NavController.navigateToMyEventsMenu() {
    navigate("MyEventsScreen")
}

fun NavController.navigateToOnlineMenu() {
    navigate("OnlineMenuScreen")
}

fun NavController.navigateToPrivacyPolicyScreen() {
    navigate("PrivacyPolicyScreen")
}

fun NavController.navigateToResultScreen() {
    navigate("ResultScreen")
}

fun NavController.navigateToModerationScreen() {
    navigate("ModerationScreen")
}
