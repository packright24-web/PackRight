package uk.ac.tees.mad.packright

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import uk.ac.tees.mad.packright.data.local.AppDatabase
import uk.ac.tees.mad.packright.domain.Supabase.Repo.Repository
import uk.ac.tees.mad.packright.presentation.ViewModel.AppViewModel
import uk.ac.tees.mad.packright.presentation.ViewModel.AuthViewModelFactory
import uk.ac.tees.mad.packright.presentation.navigation.AppNav
import uk.ac.tees.mad.packright.ui.theme.PackRightTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Supabase Client
        uk.ac.tees.mad.packright.domain.Supabase.SupabaseClient.initialize(applicationContext)

        // Initialize Room Database
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "packright_db"
        ).build()

        // Initialize Repository
        val repository = Repository(
            categoryDao = database.categoryDao(),
            itemDao = database.itemDao()
        )

        enableEdgeToEdge()
        setContent {
            PackRightTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: AppViewModel = viewModel(
                        factory = AuthViewModelFactory(repository)
                    )

                    AppNav(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}