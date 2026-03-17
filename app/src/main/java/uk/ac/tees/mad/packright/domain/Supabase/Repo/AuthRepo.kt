package uk.ac.tees.mad.packright.domain.Supabase.Repo

import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uk.ac.tees.mad.packright.domain.Supabase.SupabaseClient
import uk.ac.tees.mad.packright.model.ResultState
import uk.ac.tees.mad.packright.model.UserData

class AuthRepository {

    private val auth get() = SupabaseClient.client.auth

    fun loginUser(userData: UserData): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)

        val email = userData.email
        val password = userData.password

        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            emit(ResultState.error("Email or password cannot be empty"))
            return@flow
        }

        try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val userId = auth.currentUserOrNull()?.id
                ?: throw Exception("User session not created")

            emit(ResultState.Succes(userId))

        } catch (e: Exception) {
            emit(ResultState.error(e.message ?: "Login failed"))
        }
    }
    fun getUserid(): Flow<ResultState<String?>> = flow {
        emit(ResultState.Loading)

        try {
            val user = auth.currentUserOrNull()

            if (user != null) {
                emit(ResultState.Succes(user.id))
            } else {
                emit(ResultState.Succes(null))
            }

        } catch (e: Exception) {
            emit(ResultState.error(e.message ?: "Session check failed"))
        }
    }

    fun createUser(userData: UserData): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)

        val email = userData.email
        val password = userData.password

        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            emit(ResultState.error("Email or password cannot be empty"))
            return@flow
        }

        try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            val userId = auth.currentUserOrNull()?.id
                ?: throw Exception("User session not created")

            emit(ResultState.Succes(userId))

        } catch (e: Exception) {
            emit(ResultState.error(e.message ?: "Sign up failed"))
        }
    }

    fun signOut(): Flow<ResultState<Unit>> = flow {
        emit(ResultState.Loading)

        try {
            auth.signOut()
            emit(ResultState.Succes(Unit))
        } catch (e: Exception) {
            emit(ResultState.error(e.message ?: "Sign out failed"))
        }
    }
}
