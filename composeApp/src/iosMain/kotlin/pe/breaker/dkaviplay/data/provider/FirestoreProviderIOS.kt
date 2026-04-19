package pe.breaker.dkaviplay.data.provider

//import cocoapods.FirebaseFirestore.*
//import kotlin.coroutines.resume
//import kotlin.coroutines.resumeWithException
//import kotlin.coroutines.suspendCoroutine
//
//class FirestoreProviderIOS : FirestoreProvider {
//    private val db = FIRFirestore.firestore()
//
//    override suspend fun getDocument(
//        collection: String,
//        documentId: String
//    ): Map<String, Any>? = suspendCoroutine { cont ->
//        db.collectionWithPath(collection)
//            .documentWithPath(documentId)
//            .getDocumentWithCompletion { snap, error ->
//                if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
//                else cont.resume(snap?.data() as? Map<String, Any>)
//            }
//    }
//
//    override suspend fun setDocument(
//        collection: String,
//        documentId: String,
//        data: Map<String, Any>
//    ): Unit = suspendCoroutine { cont ->
//        db.collectionWithPath(collection)
//            .documentWithPath(documentId)
//            .setData(data) { error ->
//                if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
//                else cont.resume(Unit)
//            }
//    }
//
//    override suspend fun updateDocument(
//        collection: String,
//        documentId: String,
//        data: Map<String, Any>
//    ): Unit = suspendCoroutine { cont ->
//        db.collectionWithPath(collection)
//            .documentWithPath(documentId)
//            .updateData(data) { error ->
//                if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
//                else cont.resume(Unit)
//            }
//    }
//
//    override suspend fun deleteDocument(
//        collection: String,
//        documentId: String
//    ): Unit = suspendCoroutine { cont ->
//        db.collectionWithPath(collection)
//            .documentWithPath(documentId)
//            .deleteDocumentWithCompletion { error ->
//                if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
//                else cont.resume(Unit)
//            }
//    }
//
//    override suspend fun getCollection(
//        collection: String,
//        filters: Map<String, Any>
//    ): List<Map<String, Any>> = suspendCoroutine { cont ->
//        var query: FIRQuery = db.collectionWithPath(collection)
//        filters.forEach { (field, value) ->
//            query = query.queryWhereField(field, isEqualTo = value)
//        }
//        query.getDocumentsWithCompletion { snap, error ->
//            if (error != null) cont.resumeWithException(Exception(error.localizedDescription))
//            else cont.resume(
//                snap?.documents?.mapNotNull { it.data() as? Map<String, Any> } ?: emptyList()
//            )
//        }
//    }
//
//    override fun observeDocument(
//        collection: String,
//        documentId: String,
//        onChange: (Map<String, Any>?) -> Unit
//    ) {
//        db.collectionWithPath(collection)
//            .documentWithPath(documentId)
//            .addSnapshotListener { snap, _ ->
//                onChange(snap?.data() as? Map<String, Any>)
//            }
//    }
//}