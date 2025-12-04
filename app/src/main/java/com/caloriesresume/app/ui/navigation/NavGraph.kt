package com.caloriesresume.app.ui.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.caloriesresume.app.ui.advice.AdviceScreen
import com.caloriesresume.app.ui.analysis.AnalysisScreen
import com.caloriesresume.app.ui.camera.CameraScreen
import com.caloriesresume.app.ui.gallery.rememberImagePicker
import com.caloriesresume.app.ui.history.HistoryScreen
import com.caloriesresume.app.ui.reports.ReportsScreen
import com.caloriesresume.app.ui.reports.ReportsViewModel

sealed class Screen(val route: String) {
    object Camera : Screen("camera")
    object Analysis : Screen("analysis")
    object History : Screen("history")
    object Reports : Screen("reports")
    object Advice : Screen("advice")
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    foodApiKey: String,
    openAIApiKey: String
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val imagePicker = rememberImagePicker { uri ->
        navController.currentBackStackEntry?.savedStateHandle?.set("imageUri", uri.toString())
        navController.navigate(Screen.Analysis.route)
    }

    Scaffold(
        bottomBar = {
            if (currentRoute != null && 
                !currentRoute.startsWith("analysis") && 
                currentRoute != Screen.Advice.route) {
                BottomNavigationBar(navController, currentRoute)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Camera.route,
            modifier = Modifier.padding(padding)
        ) {
        composable(Screen.Camera.route) {
            CameraScreen(
                onImageCaptured = { uri ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("imageUri", uri.toString())
                    navController.navigate(Screen.Analysis.route)
                },
                onGallerySelected = {
                    imagePicker()
                }
            )
        }

        composable(Screen.Analysis.route) {
            val uriString = navController.previousBackStackEntry?.savedStateHandle?.get<String>("imageUri")
                ?: navController.currentBackStackEntry?.savedStateHandle?.get<String>("imageUri")
                ?: ""
            val imageUri = if (uriString.isNotEmpty()) Uri.parse(uriString) else Uri.EMPTY
            
            if (imageUri != Uri.EMPTY) {
                AnalysisScreen(
                    imageUri = imageUri,
                    foodApiKey = foodApiKey,
                    onSave = {
                        navController.navigate(Screen.History.route) {
                            popUpTo(Screen.Camera.route) { inclusive = false }
                        }
                    }
                )
            }
        }

        composable(Screen.History.route) {
            HistoryScreen()
        }

        composable(Screen.Reports.route) {
            val reportsViewModel: ReportsViewModel = hiltViewModel()
            ReportsScreen(
                viewModel = reportsViewModel,
                onGetAdvice = { summary ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("summary", summary)
                    navController.navigate(Screen.Advice.route)
                }
            )
        }

        composable(Screen.Advice.route) {
            val summary = navController.previousBackStackEntry?.savedStateHandle?.get<String>("summary")
                ?: ""
            
            AdviceScreen(
                nutritionSummary = summary,
                openAIApiKey = openAIApiKey
            )
        }
        }
    }
}

