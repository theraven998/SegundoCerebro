package com.raven.segundocerebro.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ContainerDao {
    @Query("SELECT * FROM containers WHERE type = :type ORDER BY updatedAt DESC")
    fun byType(type: ParaType): Flow<List<Container>>

    @Query("SELECT * FROM containers ORDER BY name")
    fun all(): Flow<List<Container>>

    @Query("SELECT * FROM containers WHERE id = :id")
    fun byId(id: String): Flow<Container?>

    @Query("SELECT COUNT(*) FROM notes WHERE containerId = :id")
    fun noteCount(id: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM containers WHERE type = :type")
    fun countByType(type: ParaType): Flow<Int>

    @Query("SELECT * FROM containers")
    suspend fun allOnce(): List<Container>

    @Upsert
    suspend fun upsert(container: Container)

    @Upsert
    suspend fun upsertAll(containers: List<Container>)

    @Delete
    suspend fun delete(container: Container)
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC LIMIT :limit")
    fun recent(limit: Int): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE containerId IS NULL ORDER BY createdAt DESC")
    fun inbox(): Flow<List<Note>>

    @Query("SELECT COUNT(*) FROM notes WHERE containerId IS NULL")
    fun inboxCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM notes WHERE type = :type")
    fun countByType(type: ParaType): Flow<Int>

    @Query("SELECT * FROM notes WHERE containerId = :containerId ORDER BY updatedAt DESC")
    fun byContainer(containerId: String): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun favorites(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun byId(id: String): Flow<Note?>

    @Query(
        """
        SELECT * FROM notes
        WHERE title LIKE '%' || :q || '%'
           OR content LIKE '%' || :q || '%'
           OR summary LIKE '%' || :q || '%'
           OR tags LIKE '%' || :q || '%'
        ORDER BY updatedAt DESC
        """
    )
    fun search(q: String): Flow<List<Note>>

    @Query("SELECT * FROM notes")
    suspend fun allOnce(): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<Note>)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)
}
