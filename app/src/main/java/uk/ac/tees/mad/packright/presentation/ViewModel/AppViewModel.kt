package uk.ac.tees.mad.packright.presentation.ViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uk.ac.tees.mad.packright.data.local.ItemEntity
import uk.ac.tees.mad.packright.domain.Supabase.Repo.Repository
import uk.ac.tees.mad.packright.model.ResultState
import uk.ac.tees.mad.packright.model.UserData

class AppViewModel(
    private val repo: Repository
) : ViewModel() {

    private val _loginScreenState = mutableStateOf(LogInScreenState())
    val loginScreenState = _loginScreenState

    private val _signupScreenState = mutableStateOf(SignUpScreenState())
    val signupScreenState = _signupScreenState

    private val _logoutState = mutableStateOf(false)
    val logoutState = _logoutState

    fun loginUser(userData: UserData) {
        viewModelScope.launch {
            repo.loginUser(userData).collect { result ->
                when (result) {
                    ResultState.Loading ->
                        _loginScreenState.value =
                            _loginScreenState.value.copy(isLoading = true, error = null)

                    is ResultState.Succes ->
                        _loginScreenState.value =
                            _loginScreenState.value.copy(
                                isLoading = false,
                                success = true,
                                userdata = result.data
                            )

                    is ResultState.error ->
                        _loginScreenState.value =
                            _loginScreenState.value.copy(
                                isLoading = false,
                                error = result.message
                            )
                }
            }
        }
    }

    fun registerUser(userData: UserData) {
        viewModelScope.launch {
            repo.createUser(userData).collect { result ->
                when (result) {
                    ResultState.Loading ->
                        _signupScreenState.value =
                            _signupScreenState.value.copy(isLoading = true, error = null)

                    is ResultState.Succes ->
                        _signupScreenState.value =
                            _signupScreenState.value.copy(
                                isLoading = false,
                                success = true,
                                userdata = result.data
                            )

                    is ResultState.error ->
                        _signupScreenState.value =
                            _signupScreenState.value.copy(
                                isLoading = false,
                                error = result.message
                            )
                }
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            repo.signOut().collect {
                if (it is ResultState.Succes) _logoutState.value = true
            }
        }
    }

    fun resetLoginState() { _loginScreenState.value = LogInScreenState() }
    fun resetSignupState() { _signupScreenState.value = SignUpScreenState() }
    fun resetLogoutState() { _logoutState.value = false }



    private val _homeScreenState = mutableStateOf(HomeScreenState())
    val homeScreenState = _homeScreenState

    val categories =
        repo.getCategoriesWithItems()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun addCategory(name: String) {
        viewModelScope.launch {
            repo.addCategory(name).collect { result ->
                when (result) {
                    ResultState.Loading ->
                        _homeScreenState.value =
                            _homeScreenState.value.copy(isLoading = true, error = null)

                    is ResultState.Succes ->
                        _homeScreenState.value =
                            _homeScreenState.value.copy(
                                isLoading = false,
                                successMessage = result.data
                            )

                    is ResultState.error ->
                        _homeScreenState.value =
                            _homeScreenState.value.copy(
                                isLoading = false,
                                error = result.message
                            )
                }
            }
        }
    }

    fun resetHomeState() {
        _homeScreenState.value = HomeScreenState()
    }
    val allCategories =
        repo.getAllCategories()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )
    fun getItems(categoryId: String) =
        repo.getItemsByCategory(categoryId)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )


    private val _itemScreenState = mutableStateOf(ItemScreenState())
    val itemScreenState = _itemScreenState

    fun addItem(categoryId: String, name: String) {
        viewModelScope.launch {
            repo.addItem(categoryId, name).collect { result ->
                handleItemResult(result)
            }
        }
    }

    fun toggleItem(item: ItemEntity) {
        viewModelScope.launch {
            repo.toggleItem(item).collect { result ->
                handleItemResult(result)
            }
        }
    }

    fun deleteItem(item: ItemEntity) {
        viewModelScope.launch {
            repo.deleteItem(item).collect { result ->
                handleItemResult(result)
            }
        }
    }

    private fun handleItemResult(result: ResultState<*>) {
        when (result) {
            ResultState.Loading ->
                _itemScreenState.value =
                    _itemScreenState.value.copy(isLoading = true, error = null)

            is ResultState.Succes ->
                _itemScreenState.value =
                    _itemScreenState.value.copy(
                        isLoading = false,
                        success = true
                    )

            is ResultState.error ->
                _itemScreenState.value =
                    _itemScreenState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
        }
    }

    fun resetItemScreenState() {
        _itemScreenState.value = ItemScreenState()
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

data class HomeScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

data class ItemScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)