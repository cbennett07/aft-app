package com.aftcalculator.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aftcalculator.android.ui.screens.CalculatorScreen
import com.aftcalculator.android.ui.screens.HomeScreen
import com.aftcalculator.android.ui.screens.ResultsScreen
import com.aftcalculator.android.ui.theme.AFTCalculatorTheme
import com.aftcalculator.android.viewmodels.CalculatorViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AFTCalculatorTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AftCalculatorApp()
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Calculator : Screen("calculator")
    data object Results : Screen("results")
}

@Composable
fun AftCalculatorApp(
    viewModel: CalculatorViewModel = viewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                uiState = uiState,
                onAgeChange = viewModel::updateAge,
                onGenderChange = viewModel::updateGender,
                onMosCategoryChange = viewModel::updateMosCategory,
                onStartCalculator = {
                    navController.navigate(Screen.Calculator.route)
                }
            )
        }

        composable(Screen.Calculator.route) {
            CalculatorScreen(
                uiState = uiState,
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCalculate = {
                    viewModel.calculateScore()
                    navController.navigate(Screen.Results.route)
                }
            )
        }

        composable(Screen.Results.route) {
            uiState.result?.let { score ->
                ResultsScreen(
                    score = score,
                    onDismiss = {
                        navController.popBackStack()
                    },
                    onReset = {
                        viewModel.resetCalculator()
                        navController.popBackStack(Screen.Home.route, inclusive = false)
                    }
                )
            }
        }
    }
}
