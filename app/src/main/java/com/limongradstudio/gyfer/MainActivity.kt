package com.limongradstudio.gyfer

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.limongradstudio.gyfer.domain.AppResult
import com.limongradstudio.gyfer.domain.models.Word
import com.limongradstudio.gyfer.presentation.viewmodels.WordViewModel
import com.limongradstudio.gyfer.ui.theme.GyferTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GyferTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    TopAppBar(title = { Text("Koin & SqlDelight") })
                }

                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Home()
                    }
                }
            }
        }
    }
}


@Composable
private fun Home() {
    val vm = koinViewModel<WordViewModel>()
    val state = vm.words.collectAsState()
    val opStatus = vm.operationStatus.collectAsState()
    var isUpdating by remember { mutableStateOf(false) }
    var updateId by remember { mutableStateOf(0L) }
    var rus by remember { mutableStateOf("") }
    var eng by remember { mutableStateOf("") }


    Column(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val ctx = LocalContext.current
        TextField(value = rus, onValueChange = { rus = it }, placeholder = { Text("Russian") })
        Spacer(modifier = Modifier.size(12.dp))
        TextField(value = eng, onValueChange = { eng = it }, placeholder = { Text("English") })
        Spacer(modifier = Modifier.size(12.dp))

        ElevatedButton(onClick = {
            if (isUpdating) {
                vm.update(updateId.toInt(), Word(updateId, rus, eng))
                isUpdating = false
            }
            vm.insertWord(Word(null, rus, eng))

        }) {
            Text(if(isUpdating) "Update" else "Add")
        }



        Spacer(modifier = Modifier.size(12.dp))
        DisplayList(
            modifier = Modifier.fillMaxWidth(),
            state = state.value,
            onUpdate = { id ->
                (state.value as AppResult.Success<List<Word>>).data?.let {
                    it.find { w -> w.id == id }?.also { found ->
                        isUpdating = true
                        updateId = found.id!!
                        rus = found.rus
                        eng = found.eng
                    }
                }
            },
            onDelete = { id ->
                vm.delete(id.toInt())
            }
        )

        LaunchedEffect(key1 = opStatus.value) {
            when (opStatus.value) {
                is AppResult.Failure -> {
                    if ((opStatus.value as AppResult.Failure).error is SQLiteConstraintException) {
                        Toast.makeText(ctx, "Duplicate entry not allowed", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(ctx, "Something went wrong!", Toast.LENGTH_SHORT).show()
                    }

                }

                AppResult.Loading -> {
                    Toast.makeText(ctx, "Processing", Toast.LENGTH_SHORT).show()
                }

                is AppResult.Success -> {
                    Toast.makeText(ctx, "Success", Toast.LENGTH_SHORT).show()
                }

                null -> {}
            }
        }
    }
}

@Composable
fun DisplayList(
    modifier: Modifier = Modifier, state: AppResult<List<Word>>,
    onUpdate: (id: Long) -> Unit, onDelete: (id: Long) -> Unit
) {
    val ctx = LocalContext.current
    when (state) {
        is AppResult.Failure -> {
            Toast.makeText(ctx, "Error getting words", Toast.LENGTH_SHORT).show()
        }

        AppResult.Loading -> {
            Toast.makeText(ctx, "Loading", Toast.LENGTH_SHORT).show()
        }

        is AppResult.Success -> {
            LazyColumn(
                modifier = modifier,
                rememberLazyListState()
            ) {
                state.data?.let {
                    items(it) { w ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(text = w.rus)
                            Text(text = w.eng)
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "edit",
                                modifier = Modifier.clickable {
                                    onUpdate(w.id!!)
                                })
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "edit",
                                modifier = Modifier.clickable {
                                    onDelete(w.id!!)
                                })
                        }
                    }
                }
            }
        }
    }
}
