package com.malikparteek.cumess.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

private const val TAG = "UpdatesRepo"

data class MenuUpdate(
    val id: String,
    val title: String,
    val content: String,
    val isRead: Boolean = false
)

class UpdatesRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val collectionName: String = "mess_updates"
) {

    /**
     * Observe updates in real-time. Emits Result.success(list) on success and Result.failure on errors.
     * No timestamp required — documents will be returned in Firestore's default order.
     */
    fun observeUpdates() = callbackFlow<Result<List<MenuUpdate>>> {
        Log.d(TAG, "observeUpdates() - listening to $collectionName")
        val registration: ListenerRegistration = firestore.collection(collectionName)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "listener error: ${error.message}", error)
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    trySend(Result.success(emptyList()))
                    return@addSnapshotListener
                }

                val items = snapshot.documents.mapNotNull { doc ->
                    try {
                        val id = doc.id
                        val title = doc.getString("title") ?: ""
                        val content = doc.getString("content") ?: ""
                        val isRead = doc.getBoolean("isRead") ?: false
                        MenuUpdate(id = id, title = title, content = content, isRead = isRead)
                    } catch (e: Exception) {
                        Log.w(TAG, "parse error ${doc.id}: ${e.message}")
                        null
                    }
                }
                trySend(Result.success(items))
            }

        awaitClose {
            registration.remove()
            Log.d(TAG, "listener removed")
        }
    }

    /**
     * Mark documents as read (batch).
     */
    suspend fun markAsRead(ids: List<String>) {
        if (ids.isEmpty()) return
        val batch = firestore.batch()
        val colRef = firestore.collection(collectionName)
        for (id in ids) {
            batch.update(colRef.document(id), "isRead", true)
        }
        batch.commit().await()
    }
}
