package com.rksrtx76.notesapp.presentation.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rksrtx76.notesapp.R
import com.rksrtx76.notesapp.Screen
import com.rksrtx76.notesapp.data.model.Note
import com.rksrtx76.notesapp.data.model.ui_states.UserData
import com.rksrtx76.notesapp.presentation.NotesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    navController: NavController,
    userId: String,
    viewModel: NotesViewModel,
    userData: UserData?,
    onSignOut: () -> Unit
) {

    val notesState by viewModel.notesState.collectAsState()
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawer(
                userData = userData,
                onSignOut = onSignOut
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    title = stringResource(id = R.string.note_app),
                    navController = navController,
                    onDrawerIconClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier.padding(16.dp),
                    contentColor = Color.White,
                    containerColor = Color.Black,
                    shape = RoundedCornerShape(32.dp),
                    onClick = {
                        navController.navigate("${Screen.ADD_SCREEN}/null")
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Icon(imageVector = Icons.Default.Create, contentDescription = null)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = stringResource(R.string.add_task))
                    }
                }
            }
        ) {
            LaunchedEffect(key1 = userId) {
                try{
                    viewModel.getNotes(userId)
                }catch (e: Exception){
                    Toast.makeText(
                        context,
                        "Error Loading notes",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            when{
                notesState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                notesState.error != null -> {
                    Toast.makeText(
                        context,
                        "An unknown error occured",
                        Toast.LENGTH_LONG
                    ).show()
                }

                notesState.notes.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No notes available. Add some notes!")
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        items(notesState.notes, key = { note ->
                            note.documentId
                        }) { note ->
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = {
                                    if (it == SwipeToDismissBoxValue.StartToEnd || it == SwipeToDismissBoxValue.EndToStart) {
                                        viewModel.deleteNote(note.documentId)
                                    }
                                    true
                                }
                            )
                            SwipeToDismissBox(
                                state = dismissState,
                                backgroundContent = {
                                    val color by animateColorAsState(
                                        if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) Color.Red else Color.Transparent
                                    )
                                    val alignment = Alignment.CenterEnd
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(color)
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = alignment
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            tint = Color.White,
                                            contentDescription = null
                                        )
                                    }
                                },
                                content = {
                                    NoteItem(
                                        note = note,
                                        onClick = {
                                            navController.navigate("${Screen.ADD_SCREEN}/${note.documentId}")
                                        }
                                    )

                                },
                                enableDismissFromStartToEnd = true,
                                enableDismissFromEndToStart = true
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteItem(
    note : Note,
    onClick : () -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(Color.White)
    ){
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = note.title,
                fontWeight = FontWeight.ExtraBold
            )
            Text(text = note.description)
        }
    }
}