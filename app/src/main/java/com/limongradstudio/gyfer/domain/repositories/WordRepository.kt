package com.limongradstudio.gyfer.domain.repositories


import com.limongradstudio.gyfer.domain.models.Word
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    suspend fun addWord(word: Word)
    suspend fun getAllWords(): List<Word>
    suspend fun deleteWord(id: Int)
    suspend fun update(id: Int, rus: String, eng: String)
}