package pe.breaker.dkaviplay.utli

import android.content.Context
import java.lang.ref.WeakReference

object ContextProvider {
    private var contextRef: WeakReference<Context>? = null

    fun initialize(context: Context) {
        contextRef = WeakReference(context.applicationContext)
    }

    fun getContext(): Context {
        return contextRef?.get()
            ?: throw IllegalStateException("ContextProvider no ha sido inicializado. Llamar a initialize(this) en Application.onCreate()")
    }
}