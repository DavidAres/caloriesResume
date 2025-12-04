package com.caloriesresume.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.caloriesresume.app.ui.navigation.NavGraph
import com.caloriesresume.app.ui.theme.CaloriesResumeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val foodApiKey = BuildConfig.FOOD_API_KEY
        val openAIApiKey = BuildConfig.OPENAI_API_KEY
        
        setContent {
            CaloriesResumeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        foodApiKey = foodApiKey,
                        openAIApiKey = openAIApiKey
                    )
                }
            }
        }
    }
}
