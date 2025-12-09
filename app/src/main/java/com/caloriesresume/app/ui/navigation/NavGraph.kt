package com.caloriesresume.app.ui.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.caloriesresume.app.ui.splash.SplashScreen
import com.caloriesresume.app.ui.home.HomeScreen
import com.caloriesresume.app.ui.history.FoodEntryDetailScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Camera : Screen("camera")
    object Analysis : Screen("analysis")
    object History : Screen("history")
    object FoodEntryDetail : Screen("food_entry_detail/{entryId}") {
        fun createRoute(entryId: Long) = "food_entry_detail/$entryId"
    }
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
                !currentRoute.startsWith("food_entry_detail") &&
                currentRoute != Screen.Advice.route &&
                currentRoute != Screen.Splash.route &&
                currentRoute != Screen.Camera.route) {
                BottomNavigationBar(navController, currentRoute)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(padding)
        ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onCameraClick = {
                    navController.navigate(Screen.Camera.route)
                },
                onGalleryClick = {
                    imagePicker()
                }
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen(
                onImageCaptured = { uri ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("imageUri", uri.toString())
                    navController.navigate(Screen.Analysis.route)
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
            HistoryScreen(
                onEntryClick = { entryId ->
                    navController.navigate(Screen.FoodEntryDetail.createRoute(entryId))
                }
            )
        }

        composable(
            route = Screen.FoodEntryDetail.route,
            arguments = listOf(
                navArgument("entryId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val entryId = backStackEntry.arguments?.getLong("entryId") ?: 0L
            val viewModel: com.caloriesresume.app.ui.history.FoodEntryDetailViewModel = hiltViewModel()
            
            LaunchedEffect(entryId) {
                if (entryId > 0) {
                    viewModel.loadFoodEntry(entryId)
                }
            }
            
            val uiState by viewModel.uiState.collectAsState()
            
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(uiState.error ?: "Error desconocido")
                    }
                }
                uiState.foodEntry != null -> {
                    FoodEntryDetailScreen(
                        entry = uiState.foodEntry!!,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
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

