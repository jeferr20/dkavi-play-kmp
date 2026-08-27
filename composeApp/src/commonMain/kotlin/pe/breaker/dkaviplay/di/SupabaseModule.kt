package pe.breaker.dkaviplay.di

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SettingsSessionManager
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import org.koin.dsl.module
import pe.breaker.dkaviplay.data.util.ConstatesCloud.SUPABASE_ANON_KEY
import pe.breaker.dkaviplay.data.util.ConstatesCloud.SUPABASE_URL

val supabaseModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            httpEngine = getEngine().create()
            install(Auth){
                alwaysAutoRefresh = true
                sessionManager = SettingsSessionManager()
            }
            install(Postgrest)
            install(Functions)
            install(Realtime)
        }
    }

    single { get<SupabaseClient>().pluginManager.getPlugin(Auth) }
    single { get<SupabaseClient>().pluginManager.getPlugin(Postgrest) }
    single { get<SupabaseClient>().pluginManager.getPlugin(Functions) }
    single { get<SupabaseClient>().pluginManager.getPlugin(Realtime) }
}