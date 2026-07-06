package pe.breaker.dkaviplay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.koin.android.ext.android.inject
import org.koin.compose.KoinContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import pe.breaker.dkaviplay.presentation.navigation.NotificationHandler

class MainActivity : ComponentActivity() {
    private val handler: NotificationHandler by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        loadKoinModules(module {
            single<Context> { this@MainActivity }
        })

        checkIntent(intent)

        setContent {
            KoinContext {
                App()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unloadKoinModules(module {
            single<Context> { applicationContext }
        })
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        checkIntent(intent)
    }

    private fun checkIntent(intent: Intent?) {
        val action = intent?.getStringExtra("action")
        val id = intent?.getStringExtra("id")

        if (action != null) {
            val data = mapOf(
                "action" to action,
                "id" to (id ?: "")
            )

            handler.handleNotificationAction(data)

            intent.removeExtra("action")
            intent.removeExtra("id")

            setIntent(Intent())
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}