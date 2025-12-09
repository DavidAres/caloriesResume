package com.caloriesresume.app.ui.camera

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onImageCaptured: (Uri) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(Manifest.permission.CAMERA)
    )

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    val hasCameraPermission = cameraPermissionState.allPermissionsGranted

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionState.launchMultiplePermissionRequest()
        }
    }
    
    LaunchedEffect(hasCameraPermission) {
        if (!hasCameraPermission) {
            imageCapture = null
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            imageCapture = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (hasCameraPermission) {
            key(hasCameraPermission) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        
                        val capture = ImageCapture.Builder()
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .build()
                        
                        cameraProviderFuture.addListener({
                            try {
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = androidx.camera.core.Preview.Builder().build()
                                preview.setSurfaceProvider(previewView.surfaceProvider)
                                
                                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                                
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    capture
                                )
                                
                                android.util.Log.d("CameraScreen", "Cámara inicializada correctamente")
                                imageCapture = capture
                            } catch (e: Exception) {
                                android.util.Log.e("CameraScreen", "Error al inicializar cámara", e)
                                e.printStackTrace()
                                imageCapture = null
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        
                        previewView
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Se necesita permiso de cámara")
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        cameraPermissionState.launchMultiplePermissionRequest()
                    }
                ) {
                    Text("Solicitar permiso de cámara")
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    imageCapture?.let { capture ->
                        captureImage(context, capture) { uri ->
                            onImageCaptured(uri)
                        }
                    } ?: run {
                        android.util.Log.w("CameraScreen", "imageCapture es null al intentar capturar")
                    }
                },
                enabled = hasCameraPermission && imageCapture != null,
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text("Capturar")
            }
        }
    }
}

private fun captureImage(
    context: Context,
    imageCapture: ImageCapture,
    onImageSaved: (Uri) -> Unit
) {
    val photoFile = File(
        context.getExternalFilesDir(null),
        SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                onImageSaved(Uri.fromFile(photoFile))
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
            }
        }
    )
}

