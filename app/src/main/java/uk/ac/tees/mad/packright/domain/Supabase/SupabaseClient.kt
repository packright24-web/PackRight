package uk.ac.tees.mad.packright.domain.Supabase

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {

    private const val SUPABASE_URL = "https://rdbadfcazapunynatrdb.supabase.co"
    private const val SUPABASE_KEY = ""

    lateinit var client: io.github.jan.supabase.SupabaseClient
        private set

    fun initialize(context: android.content.Context) {
        client = createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Postgrest)
            install(Auth)


        }
    }
}

