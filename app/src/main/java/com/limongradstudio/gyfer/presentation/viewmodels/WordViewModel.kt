package com.limongradstudio.gyfer.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.limongradstudio.gyfer.domain.AppResult
import com.limongradstudio.gyfer.domain.AppResult.Success
import com.limongradstudio.gyfer.domain.models.Word
import com.limongradstudio.gyfer.domain.repositories.WordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WordViewModel(private val repository: WordRepository) : ViewModel() {

    private val _words = MutableStateFlow<AppResult<List<Word>>>(AppResult.Loading)
    val words = _words.asStateFlow()

    private val _operationStatus = MutableStateFlow<AppResult<Nothing>?>(null)
    val operationStatus = _operationStatus.asStateFlow()

    init {
        getAll()
    }

    private fun getAll() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _words.value = AppResult.Loading
                _words.value = Success(repository.getAllWords())
            }catch (e: Exception){
                _words.value = AppResult.Failure(e)

            }
        }
    }

    fun insertWord(word: Word) {
        _operationStatus.value = AppResult.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addWord(word)
                _operationStatus.value = AppResult.Success(null)
                getAll()
            } catch (e: Exception) {
                _operationStatus.value = AppResult.Failure(e)
            }

        }

    }

    fun delete(id: Int) {
        _operationStatus.value = AppResult.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteWord(id)
                _operationStatus.value = AppResult.Success(null)
                getAll()
            } catch (e: Exception) {
                _operationStatus.value = AppResult.Failure(e)
            }
        }
    }

    fun update(id: Int, word: Word) {
        _operationStatus.value = AppResult.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.update(id, word.rus, word.eng)
                _operationStatus.value = AppResult.Success(null)
                getAll()
            } catch (e: Exception) {
                _operationStatus.value = AppResult.Failure(e)
            }
        }
    }
}




