package pe.breaker.dkaviplay.data.provider

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class FirestoreProviderAndroid : FirestoreProvider {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun getDocument(
        collection: String,
        documentId: String
    ): Map<String, Any>? {
        val snap = db.collection(collection).document(documentId).get().await()
        return if (snap.exists()) snap.data else null
    }

    override suspend fun setDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any>
    ) {
        db.collection(collection).document(documentId).set(data).await()
    }

    override suspend fun updateDocument(
        collection: String,
        documentId: String,
        data: Map<String, Any>
    ) {
        db.collection(collection).document(documentId).update(data).await()
    }

    override suspend fun deleteDocument(
        collection: String,
        documentId: String
    ) {
        db.collection(collection).document(documentId).delete().await()
    }

    override suspend fun getCollection(
        collection: String,
        filters: Map<String, Any>
    ): List<Map<String, Any>> {
        var query = db.collection(collection) as com.google.firebase.firestore.Query
        filters.forEach { (field, value) ->
            query = query.whereEqualTo(field, value)
        }
        return query.get().await().documents.mapNotNull { it.data }
    }

    override fun observeDocument(
        collection: String,
        documentId: String,
        onChange: (Map<String, Any>?) -> Unit
    ) {
        db.collection(collection).document(documentId)
            .addSnapshotListener { snap, _ ->
                onChange(if (snap?.exists() == true) snap.data else null)
            }
    }
}