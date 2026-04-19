package pe.breaker.dkaviplay.data.provider

import pe.breaker.dkaviplay.data.remote.firebase.PersonaFirebase

interface FirestoreProvider {
    suspend fun getDocument(
        collection: String,
        documentId: String
    ): Map<String, Any>?

    suspend fun setDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any>
    )

    suspend fun updateDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any>
    )

    suspend fun deleteDocument(
        collection: String,
        documentId: String
    )

    suspend fun getCollection(
        collection: String,
        filters: Map<String, Any> = emptyMap()
    ): List<Map<String, Any>>

    // Para escalar a realtime
    fun observeDocument(
        collection: String,
        documentId: String,
        onChange: (Map<String, Any>?) -> Unit
    )
}