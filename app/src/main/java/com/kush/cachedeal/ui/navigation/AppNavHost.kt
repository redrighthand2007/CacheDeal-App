package com.kush.cachedeal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kush.cachedeal.ui.auth.AuthScreen
import com.kush.cachedeal.ui.deals.DealsScreen
import com.kush.cachedeal.ui.home.HomeScreen
import com.kush.cachedeal.ui.itemdetail.ItemDetailScreen
import com.kush.cachedeal.ui.mylistings.MyListingsScreen
import com.kush.cachedeal.ui.offers.OffersScreen

import com.kush.cachedeal.ui.postitem.PostItemScreen
import com.kush.cachedeal.ui.profile.ProfileScreen
import com.kush.cachedeal.ui.splash.SplashScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = SplashRoute
    ) {
        composable<SplashRoute> {
            SplashScreen(navController = navController)
        }
        composable<AuthRoute> {
            AuthScreen(navController = navController)
        }
        composable<SignUpRoute> {
            com.kush.cachedeal.ui.auth.SignUpScreen(navController = navController)
        }
        composable<LoginRoute> {
            com.kush.cachedeal.ui.auth.LoginScreen(navController = navController)
        }
        composable<HomeRoute> {
            HomeScreen(navController = navController)
        }
        composable<PostItemRoute> {
            PostItemScreen(navController = navController)
        }
        composable<ItemDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ItemDetailRoute>()
            ItemDetailScreen(
                itemId = route.itemId,
                navController = navController
            )
        }
        composable<MyListingsRoute> {
            MyListingsScreen(navController = navController)
        }
        composable<OffersRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<OffersRoute>()
            OffersScreen(
                itemId = route.itemId,
                navController = navController
            )
        }
        composable<DealsRoute> {
            DealsScreen(navController = navController)
        }
        composable<ProfileRoute> {
            ProfileScreen(navController = navController)
        }
    }
}
