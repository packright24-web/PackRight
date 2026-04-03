package uk.ac.tees.mad.packright.domain.Supabase.Repo

import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage

import io.github.jan.supabase.storage.upload

import io.github.jan.supabase.storage.Bucket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uk.ac.tees.mad.packright.data.local.CategoryDao
import uk.ac.tees.mad.packright.data.local.CategoryEntity
import uk.ac.tees.mad.packright.data.local.ItemDao
import uk.ac.tees.mad.packright.data.local.ItemEntity
import uk.ac.tees.mad.packright.domain.Supabase.SupabaseClient
import uk.ac.tees.mad.packright.model.ResultState
import uk.ac.tees.mad.packright.model.UserData
import java.util.UUID

class Repository(
    private val categoryDao: CategoryDao,
    private val itemDao: ItemDao
) {

    private val auth get() = SupabaseClient.client.auth
    private val postgrest get() = SupabaseClient.client
    private val storage get() = SupabaseClient.client.storage

    fun uploadProfileImage(byteArray: ByteArray): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            val userId = auth.currentUserOrNull()?.id
                ?: throw Exception("User not logged in")

            val fileName = "profile_$userId.jpg"
            val bucket = storage.from("profiles")
            
            bucket.upload(fileName, byteArray, upsert = true)
            val url = bucket.publicUrl(fileName)
            
            emit(ResultState.Succes(url))
        } catch (e: Exception) {
            val userMsg = when {
                e.localizedMessage?.contains("Bucket not found") == true -> 
                    "Storage bucket 'profiles' not found. Please create it in your Supabase Dashboard."
                e.localizedMessage?.contains("row-level security") == true ->
                    "Permission denied! Please add a Storage Policy to your 'profiles' bucket in Supabase."
                else -> e.message ?: "Upload failed"
            }
            emit(ResultState.error(userMsg))
        }
    }

    fun getProfileImageUrl(): Flow<String?> = flow {
        try {
            val userId = auth.currentUserOrNull()?.id
            if (userId != null) {
                val fileName = "profile_$userId.jpg"
                emit(storage.from("profiles").publicUrl(fileName))
            } else {
                emit(null)
            }
        } catch (e: Exception) {
            emit(null)
        }
    }

    fun loginUser(userData: UserData): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            auth.signInWith(Email) {
                email = userData.email!!
                password = userData.password!!
            }

            val userId = auth.currentUserOrNull()?.id
                ?: throw Exception("User session not created")

            emit(ResultState.Succes(userId))

        } catch (e: Exception) {
            emit(ResultState.error(e.message ?: "Login failed"))
        }
    }

    fun createUser(userData: UserData): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)
        try {
            auth.signUpWith(Email) {
                email = userData.email!!
                password = userData.password!!
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



    fun addCategory(name: String): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)

        try {
            val userId = auth.currentUserOrNull()?.id
                ?: throw Exception("User not logged in")

            val category = CategoryEntity(
                categoryId = UUID.randomUUID().toString(),
                userId = userId,
                categoryName = name,
                isSynced = false
            )


            categoryDao.insertCategory(category)


            try {
                postgrest.from("categories").insert(category)
                categoryDao.insertCategory(category.copy(isSynced = true))
            } catch (e: Exception) {

            }

            emit(ResultState.Succes("Category created"))

        } catch (e: Exception) {
            emit(ResultState.error(e.message ?: "Failed to create category"))
        }
    }



    fun addItem(categoryId: String, name: String): Flow<ResultState<String>> = flow {
        emit(ResultState.Loading)

        try {
            val userId = auth.currentUserOrNull()?.id
                ?: throw Exception("User not logged in")

            val item = ItemEntity(
                itemId = UUID.randomUUID().toString(),
                categoryOwnerId = categoryId,
                userId = userId,
                itemName = name,
                isPacked = false,
                isSynced = false
            )

            itemDao.insertItem(item)

            try {
                postgrest.from("items").insert(item)
                itemDao.insertItem(item.copy(isSynced = true))
            } catch (e: Exception) {
                // Offline
            }

            emit(ResultState.Succes("Item added"))

        } catch (e: Exception) {
            emit(ResultState.error(e.message ?: "Failed to add item"))
        }
    }

    fun toggleItem(item: ItemEntity): Flow<ResultState<Unit>> = flow {
        emit(ResultState.Loading)

        try {
            val updated = item.copy(
                isPacked = !item.isPacked,
                isSynced = false
            )

            itemDao.updateItem(updated)

            try {
                postgrest.from("items")
                    .update(updated) {
                        filter { eq("item_id", item.itemId) }
                    }

                itemDao.updateItem(updated.copy(isSynced = true))
            } catch (e: Exception) {
                // Offline
            }

            emit(ResultState.Succes(Unit))

        } catch (e: Exception) {
            emit(ResultState.error(e.message ?: "Toggle failed"))
        }
    }

    fun deleteItem(item: ItemEntity): Flow<ResultState<Unit>> = flow {
        emit(ResultState.Loading)

        try {
            itemDao.deleteItem(item)

            try {
                postgrest.from("items")
                    .delete {
                        filter { eq("item_id", item.itemId) }
                    }
            } catch (e: Exception) {
                // Offline
            }

            emit(ResultState.Succes(Unit))

        } catch (e: Exception) {
            emit(ResultState.error(e.message ?: "Delete failed"))
        }
    }


    fun getAllCategories(): Flow<List<CategoryEntity>> {
        val userId = auth.currentUserOrNull()?.id
        
        if (userId == null) return kotlinx.coroutines.flow.emptyFlow()

        return categoryDao.getAllCategories(userId)
    }



    fun getItemsByCategory(categoryId: String): Flow<List<ItemEntity>> {
        return itemDao.getItemsByCategory(categoryId)
    }



    fun getCategoriesWithItems() =
        categoryDao.getCategoriesWithItems()
}