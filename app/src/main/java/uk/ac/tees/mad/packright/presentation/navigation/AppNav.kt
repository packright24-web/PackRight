package uk.ac.tees.mad.packright.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import uk.ac.tees.mad.packright.presentation.ViewModel.AppViewModel
@Composable
fun AppNav(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

//        composable(Routes.SPLASH) {
//            SplashScreen(
//                onNavigateToLogin = {
//                    navController.navigate(Routes.LOGIN) {
//                        popUpTo(Routes.SPLASH) { inclusive = true }
//                    }
//                }
//            )
//        }

        composable(Routes.LOGIN) {
            uk.ac.tees.mad.packright.presentation.screens.AuthScreen(
                appViewModel = viewModel,
                onAuthSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

//        composable(Routes.HOME) {
//            HomeScreen(
//                viewModel = viewModel,
//                onCategoryClick = { categoryId ->
//                    navController.navigate(Routes.itemsRoute(categoryId))
//                }
//            )
//        }

//        composable(
//            route = Routes.ITEMS,
//            arguments = listOf(
//                navArgument("categoryId") {
//                    type = NavType.StringType
//                }
//            )
//        ) { backStackEntry ->
//
//            val categoryId =
//                backStackEntry.arguments?.getString("categoryId") ?: ""
//
//            AddEditItemsScreen(
//                categoryId = categoryId,
//                viewModel = viewModel,
//                onBack = { navController.popBackStack() }
//            )
//        }
    }
}