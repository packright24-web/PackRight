package uk.ac.tees.mad.packright.presentation.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uk.ac.tees.mad.packright.domain.Supabase.Repo.AuthRepository
import uk.ac.tees.mad.packright.model.ResultState
import uk.ac.tees.mad.packright.model.UserData

class AppViewModel(
    private val repo: AuthRepository
) : ViewModel() {


    private val _loginScreenState = mutableStateOf(LogInScreenState())
    val loginScreenState = _loginScreenState

    fun loginUser(userData: UserData) {
        viewModelScope.launch {
            repo.loginUser(userData).collect { result ->
                when (result) {
                    ResultState.Loading -> {
                        _loginScreenState.value = LogInScreenState(isLoading = true)
                    }
                    is ResultState.Succes -> {
                        _loginScreenState.value = LogInScreenState(
                            success = true,
                            userdata = result.data
                        )
                    }
                    is ResultState.error -> {
                        _loginScreenState.value = LogInScreenState(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun resetLoginState() {
        _loginScreenState.value = LogInScreenState()
    }


    private val _signupScreenState = mutableStateOf(SignUpScreenState())
    val signupScreenState = _signupScreenState

    fun registerUser(userData: UserData) {
        viewModelScope.launch {
            repo.createUser(userData).collect { result ->
                when (result) {
                    ResultState.Loading -> {
                        _signupScreenState.value = SignUpScreenState(isLoading = true)
                    }
                    is ResultState.Succes -> {
                        _signupScreenState.value = SignUpScreenState(
                            success = true,
                            userdata = result.data
                        )
                    }
                    is ResultState.error -> {
                        _signupScreenState.value = SignUpScreenState(
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    fun resetSignupState() {
        _signupScreenState.value = SignUpScreenState()
    }


    fun logoutUser() {
        viewModelScope.launch {
            repo.signOut().collect { _ -> }
        }
    }
}


data class SignUpScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userdata: String? = null,
    val success: Boolean = false
)

data class LogInScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userdata: String? = null,
    val success: Boolean = false
)