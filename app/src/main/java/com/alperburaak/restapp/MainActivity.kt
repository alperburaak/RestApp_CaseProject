package com.alperburaak.restapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alperburaak.restapp.ui.auth.AuthViewModel
import com.alperburaak.restapp.ui.order.OrderViewModel
import com.alperburaak.restapp.ui.pages.HomeScreen
import com.alperburaak.restapp.ui.pages.LoginScreen
import com.alperburaak.restapp.ui.pages.SplashScreen
import com.alperburaak.restapp.ui.rest.RestaurantViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Routes.SPLASH
                ) {


                    composable(Routes.SPLASH) {
                        val authViewModel: AuthViewModel = koinViewModel()

                        SplashScreen(
                            authViewModel = authViewModel,
                            onNavigateToLogin = {
                                navController.navigate(Routes.AUTH) {
                                    popUpTo(Routes.SPLASH) { inclusive = true }
                                }
                            },
                            onNavigateToHome = {
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.SPLASH) { inclusive = true }
                                }
                            }
                        )
                    }


                    composable(Routes.AUTH) {
                        val authViewModel: AuthViewModel = koinViewModel()

                        LoginScreen(
                            authViewModel = authViewModel,
                            onLoginSuccess = {
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.AUTH) { inclusive = true }
                                }
                            }
                        )
                    }


                    composable(Routes.HOME) {
                        val authViewModel: AuthViewModel = koinViewModel()
                        val orderViewModel: OrderViewModel = koinViewModel()
                        val restaurantViewModel: RestaurantViewModel = koinViewModel()
                        HomeScreen(
                            authViewModel = authViewModel,
                            restaurantViewModel = restaurantViewModel,
                            orderViewModel = orderViewModel,
                            onLogout = {
                                navController.navigate(Routes.AUTH) {
                                    popUpTo(Routes.HOME) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}