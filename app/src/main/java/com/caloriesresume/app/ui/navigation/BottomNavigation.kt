package com.caloriesresume.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Add, contentDescription = "Cámara") },
            label = { Text("Cámara") },
            selected = currentRoute == Screen.Camera.route,
            onClick = {
                navController.navigate(Screen.Camera.route) {
                    popUpTo(Screen.Camera.route) { inclusive = true }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Historial") },
            label = { Text("Historial") },
            selected = currentRoute == Screen.History.route,
            onClick = {
                navController.navigate(Screen.History.route) {
                    popUpTo(Screen.Camera.route) { inclusive = false }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Info, contentDescription = "Reportes") },
            label = { Text("Reportes") },
            selected = currentRoute == Screen.Reports.route,
            onClick = {
                navController.navigate(Screen.Reports.route) {
                    popUpTo(Screen.Camera.route) { inclusive = false }
                }
            }
        )
    }
}

