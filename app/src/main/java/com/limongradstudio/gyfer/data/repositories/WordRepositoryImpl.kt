package com.limongradstudio.gyfer.data.repositories

import com.example.Database
import com.limongradstudio.gyfer.domain.models.Word
import com.limongradstudio.gyfer.domain.repositories.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WordRepositoryImpl(private val database: Database) : WordRepository {
    override suspend fun addWord(word: Word) {
        database.wordsQueries.insert(word.rus, word.eng)
    }

    override suspend fun getAllWords(): List<Word> =
        database.wordsQueries.getAll().executeAsList().map { wd -> Word(wd.id, wd.rus, wd.eng) }


    override suspend fun deleteWord(id: Int) {
        database.wordsQueries.delete(id.toLong())
    }

    override suspend fun update(id: Int, rus: String, eng: String) {
        database.wordsQueries.transaction {
            val existingWord = database.wordsQueries.getWordById(id.toLong()).executeAsOneOrNull()
            if (existingWord != null) {
                // If the word exists, update it
                database.wordsQueries.update(rus, eng, id.toLong())
            } else {
                // If the word does not exist, insert it
                database.wordsQueries.insert(rus, eng)
            }
        }
    }
}