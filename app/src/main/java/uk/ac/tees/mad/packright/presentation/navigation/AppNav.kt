package uk.ac.tees.mad.packright.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import uk.ac.tees.mad.packright.presentation.ViewModel.AppViewModel
import uk.ac.tees.mad.packright.presentation.screens.AddEditItemsScreen
import uk.ac.tees.mad.packright.presentation.screens.HomeScreen
import uk.ac.tees.mad.packright.presentation.screens.ProfileScreen
import uk.ac.tees.mad.packright.presentation.screens.SplashScreen
import uk.ac.tees.mad.packright.presentation.screens.AuthScreen

@Composable
fun AppNav(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
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

        composable(Routes.LOGIN) {
            AuthScreen(
                appViewModel = viewModel,
                onAuthSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            val categories by viewModel.categories.collectAsState()
            
            HomeScreen(
                categories = categories,
                onCategoryClick = { categoryId ->
                    navController.navigate(Routes.itemsRoute(categoryId))
                },
                onProfileClick = {
                    navController.navigate(Routes.PROFILE)
                },
                onAddCategory = { name ->
                    viewModel.addCategory(name)
                }
            )
        }

        composable(
            route = Routes.ITEMS,
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val items by viewModel.getItems(categoryId).collectAsState()

            AddEditItemsScreen(
                categoryId = categoryId,
                items = items,
                onBack = { navController.popBackStack() },
                onAddItem = { itemName ->
                    viewModel.addItem(categoryId, itemName)
                },
                onToggleItem = { item ->
                    viewModel.toggleItem(item)
                },
                onDeleteItem = { item ->
                    viewModel.deleteItem(item)
                }
            )
        }

        composable(Routes.PROFILE) {
            val logoutState by viewModel.logoutState
            
            if (logoutState) {
                // Once logout finishes, reset state and navigate to login
                viewModel.resetLogoutState()
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
            }

            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = { viewModel.logoutUser() }
            )
        }
    }
}