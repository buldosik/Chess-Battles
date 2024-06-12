package com.example.chessgo.frontend.camera


import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.chessgo.R

private const val cameraPermission = Manifest.permission.CAMERA
/**
 * Composable function for the Camera Screen.
 *
 * @param navController The NavController for handling navigation.
 */
@Composable
fun CameraScreen(togglePlacePicker: () -> Unit) {
    val context = LocalContext.current
    val cameraViewModel = CameraViewModel(togglePlacePicker)
    val rejectPermissionText = stringResource(id = R.string.rejected_permission_info)
    //defines button's color
    val buttonState = remember {
        mutableStateOf(false)
    }
    //holds current picture uri
    val currentUri = remember {
        mutableStateOf<Uri?>(Uri.EMPTY)
    }
    //holds last successfully taken picture uri
    val lastUri = remember {
        mutableStateOf<Uri?>(Uri.EMPTY)
    }
    //launches inbuild camera's activity
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()){
            currentUri.value = cameraViewModel.getLastImageUri()
        }
    //launches permission's window
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {isGranted->
        if (isGranted){
            val uri = cameraViewModel.getImageUri(context)
            cameraLauncher.launch(uri)
        }else{
            cameraViewModel.makeToast(text  = rejectPermissionText, context)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
        ) {
            //image is pinned to box layout
            Box( modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                ImageSection(
                    currentUri = currentUri,
                    lastUri = lastUri,
                    cameraViewModel = cameraViewModel,
                    context = context,
                    buttonState = buttonState
                )
            }
            ButtonFunction(
                onClick =
                    onTakePhotoClick(
                        cameraViewModel = cameraViewModel,
                        context = context,
                        cameraLauncher = cameraLauncher,
                        cameraPermissionLauncher = cameraPermissionLauncher
                    )
                , text = stringResource(id = R.string.take_photo),
                color = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                borderColor = BorderStroke(4.dp, MaterialTheme.colorScheme.onPrimary)
            )
            ButtonFunction(
                onClick = onSubmitClick(cameraViewModel = cameraViewModel, context = context)
            , text = "Submit",
                color = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    disabledContainerColor = MaterialTheme.colorScheme.secondary,
                    disabledContentColor = MaterialTheme.colorScheme.onSecondary
                ),
                borderColor = BorderStroke(4.dp, MaterialTheme.colorScheme.onPrimary)
            )

        }
    }
}
/**
 * Composable function for the Image Section.
 *
 * @param currentUri The current Uri of the selected image.
 * @param lastUri The last successfully taken image Uri.
 * @param cameraViewModel The CameraViewModel for camera-related operations.
 * @param context The [Context] used for operations like fetching URIs.
 * @param buttonState The state of the button.
 */
@Composable
fun ImageSection(currentUri: MutableState<Uri?>,
                 lastUri: MutableState<Uri?>,
                 cameraViewModel: CameraViewModel,
                 context: Context,
                 buttonState: MutableState<Boolean>
                 ){
    val image = R.drawable.empty_photo
    val defaultPicture = stringResource(id = R.string.default_picture)
    val selectedPicture = stringResource(id = R.string.default_picture)
    val lastTakenPicture = stringResource(id = R.string.default_picture)
    if (currentUri.value?.path?.isNotEmpty() == true) {
        val bitmap = currentUri.value?.let { cameraViewModel.uriToBitmap(context, it) }
        if (bitmap != null && !cameraViewModel.isBitmapEmpty(bitmap)) {
            buttonState.value = true
            lastUri.value = currentUri.value
            Image(currentUri.value,text = selectedPicture)
        } else{
            lastUri.value?.let {
                val lastUriBitmap = cameraViewModel.uriToBitmap(context, it)
                if(lastUriBitmap != null && !cameraViewModel.isBitmapEmpty(lastUriBitmap)){
                    Image(it, text = lastTakenPicture )
                    return
                }
            }
            Image(image, text = defaultPicture)
        }
    }else{
        Image(image, text = defaultPicture)
    }
}
/**
 * Composable function for a custom OutlinedButton.
 *
 * @param onClick The click event handler for the button.
 * @param text The text to be displayed on the button.
 * @param color The color configuration for the button.
 * @param borderColor The border configuration for the button.
 */
@Composable
fun ButtonFunction(
    onClick: () -> Unit,
    text: String?,
    color: ButtonColors,
    borderColor: BorderStroke
){
    OutlinedButton(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth(),
        onClick = {onClick()},
        colors = color,
        border = borderColor
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(fontSize = 16.sp)
                ) {
                    append(text)
                }
            }
        )
    }
}
/**
 * Composable function for displaying an image using the AsyncImage library.
 *
 * @param model The model representing the image. This can be of any type supported by AsyncImage.
 * @param text The content description text for the image.
 */
@Composable
fun Image(model: Any?, text: String?){
    AsyncImage(
        model = model,
        modifier = Modifier.fillMaxSize(),
        alignment = Alignment.CenterStart,
        contentDescription = text,
    )
}
/**
 * Returns a lambda function to be used as the click event handler for the Submit button.
 *
 * @param cameraViewModel The CameraViewModel for camera-related operations.
 * @param context The [Context] used for operations like displaying toasts.
 * @return Lambda function to handle the Submit button click event.
 */
@Composable
fun onSubmitClick(
    cameraViewModel: CameraViewModel,
    context: Context
): () -> Unit{
    val successText  = stringResource(id = R.string.added_img_success)
    val unableToSafeText  = stringResource(id = R.string.unable_to_safe)
    return{
        val uri = cameraViewModel.getLastImageUri()
        // it is done to avoid case when user
        // chooses to take a photo and then scrolls back without
        // confirming photo even though photo wasn't confirmed in camera
        val bitmap = uri?.let { cameraViewModel.uriToBitmap(context, it) }
        if (bitmap != null && !cameraViewModel.isBitmapEmpty(bitmap)) {
            cameraViewModel.onConfirmPhotoClick(uri, context)
            cameraViewModel.makeToast(text = successText, context)
        } else{
            cameraViewModel.makeToast(text = unableToSafeText, context)
        }
    }
    }
/**
 * Returns a lambda function to be used as the click event handler for the Take photo button.
 *
 * @param cameraViewModel The CameraViewModel for camera-related operations.
 * @param context The [Context] used for operations like displaying toasts.
 * @param cameraLauncher The launcher for the camera activity.
 * @param cameraPermissionLauncher The launcher for the camera permission request.
 * @return Lambda function to handle the Take photo button click event.
 */
@Composable
fun onTakePhotoClick(
    cameraViewModel: CameraViewModel,
    context: Context,
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    cameraPermissionLauncher: ManagedActivityResultLauncher<String, Boolean>
):() -> Unit{
    return {
        val isGranted = cameraViewModel.checkCameraPermission(context, cameraPermission)
        if (isGranted) {
            val uri = cameraViewModel.getImageUri(context)
            cameraLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(cameraPermission)
        }
    }
}