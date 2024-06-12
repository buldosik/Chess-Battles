package com.example.chessgo.frontend.camera

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.chessgo.backend.global.ClientManager
import com.example.chessgo.backend.global.PhotoPickerManager

private const val TAG = "SignUpViewModel"
/**
 * ViewModel for managing camera-related functionality in the CameraScreen.
 *
 * @param navController The NavController for handling navigation.
 */
class CameraViewModel(val togglePlacePicker: () -> Unit) : ViewModel() {

    /**
     * Handles the click event when the user confirms a photo.
     *
     * @param uri The Uri of the selected photo.
     * @param context The [Context] used for operations like displaying toasts.
     */
    fun onConfirmPhotoClick(uri: Uri?, context: Context) {

        if (uri != null) {
            ClientManager.pictureUri = uri
            ClientManager.isPictureChanged = true
            togglePlacePicker()
        } else{
            makeToast("Take new photo first", context)
        }
    }
    /*
    * Checks if the specific permission was granted.
    * If granted, returns true.
    * If not, false
    *
    * @param context The [Context] used to check and request permissions.
    * @param permission The permission to be checked and requested.
    * @return Returns `true` if the permission is granted, otherwise `false`.
    */
    fun checkCameraPermission(
        context: Context,
        permission: String
    ) :Boolean{
        val permissionCheckResult = ContextCompat.checkSelfPermission(context, permission)
        if (permissionCheckResult != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }
    /**
     * Gets the Uri of the last selected image.
     *
     * @return The Uri of the last selected image, or null if none.
     */
    fun getLastImageUri(): Uri? {
        return PhotoPickerManager.getLastImageUri()
    }
    /**
     * Gets the Uri for capturing a new image.
     *
     * @param context The [Context] used for operations like fetching URIs.
     * @return The Uri for capturing a new image.
     */
    fun getImageUri(context: Context): Uri? {
        return PhotoPickerManager.getImageUri(context)
    }
    /**
     * Converts a Uri to a Bitmap.
     *
     * @param context The [Context] used for operations like opening input streams.
     * @param uri The Uri to be converted to a Bitmap.
     * @return The Bitmap representation of the Uri, or null if conversion fails.
     */
    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = uri.let { context.contentResolver.openInputStream(it) }
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Checks if a Bitmap is empty.
     *
     * @param bitmap The Bitmap to be checked.
     * @return `true` if the Bitmap is empty (width or height is 0), otherwise `false`.
     */
    fun isBitmapEmpty(bitmap: Bitmap): Boolean {
        return bitmap.width == 0 || bitmap.height == 0
    }
    /**
     * Displays a short-duration toast message.
     *
     * @param text The text message to be displayed in the toast.
     * @param context The [Context] used for displaying the toast.
     */
    fun makeToast(text: String, context: Context){
        Toast.makeText(
            context,
            text,
            Toast.LENGTH_SHORT,
        ).show()
    }
}