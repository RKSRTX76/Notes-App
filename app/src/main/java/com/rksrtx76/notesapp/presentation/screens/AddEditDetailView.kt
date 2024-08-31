package com.rksrtx76.notesapp.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.rksrtx76.notesapp.R
import com.rksrtx76.notesapp.data.model.Note
import com.rksrtx76.notesapp.presentation.NotesViewModel
import kotlinx.coroutines.launch

@Composable
fun AddEditDetailView(
    documentId: String?,
    userId : String,
    viewModel: NotesViewModel,
    navController: NavController
){
    val notesState by viewModel.notesState.collectAsState()

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = documentId) {
        if(documentId != null){
            viewModel.getNotes(userId)
        }
    }


   val note = notesState.notes.firstOrNull{
       it.documentId == documentId
   }
    var title by remember {
        mutableStateOf(note?.title ?: "")
    }
    var description by remember{
        mutableStateOf(note?.description ?: "")
    }
    val context = LocalContext.current

    // update textfields if notes already exists
    LaunchedEffect(key1 = note) {
        note?.let {
            title = it.title
            description = it.description
        }
    }



    Scaffold(
        topBar = {
            AppBar(title =
                    if(documentId != null) stringResource(R.string.update_note) else stringResource(R.string.add_note),
                navController = navController
        )
        },
    ) {
        Column(modifier = Modifier
            .padding(it)
            .wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Spacer(modifier = Modifier.height(10.dp))

            NoteTextField(label = "Title",
                value = title,
                onValueChanged = {
                    title = it
                } )

            Spacer(modifier = Modifier.height(10.dp))

            NoteTextField(label = "Description",
                value = description,
                onValueChanged = {
                    description = it
                } )

            Spacer(modifier = Modifier.height(10.dp))

            Button(colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Color.Black
            ),onClick={
                if(title.isNotEmpty() &&
                    description.isNotEmpty()){
                    if(documentId != null){
                        viewModel.updateNote(
                            Note(
                                userId = note?.userId ?: "",
                                documentId = documentId,
                                title = title.trim(),
                                description = description.trim()
                            )
                        )
                    }else{
                        //  AddWish
                        viewModel.addNote(
                            Note(
                                userId = userId,
                                title = title.trim(),
                                description = description.trim()
                            )
                        )
                        Toast.makeText(context, "Note has been created", Toast.LENGTH_SHORT).show()

                    }
                }else{
                    Toast.makeText(context, "Enter fields to create a Note", Toast.LENGTH_SHORT).show()
                }
                scope.launch {

                    navController.navigateUp()
                }

            }){
                Text(
                    text = if (documentId != null) stringResource(id = R.string.update_note)
                    else stringResource(
                        id = R.string.add_note
                    ),
                    style = TextStyle(
                        fontSize = 18.sp
                    )
                )
            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteTextField(
    label: String,
    value: String,
    onValueChanged: (String) -> Unit
){
    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        label = { Text(text = label, color = Color.Black.copy(alpha = 0.2f)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp),
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            // using predefined Color
            focusedTextColor = Color.Black,
            // using our own colors in Res.Values.Color
            focusedBorderColor = colorResource(id = R.color.black),
            unfocusedBorderColor = colorResource(id = R.color.black).copy(alpha = 0.8f),
            cursorColor = colorResource(id = R.color.black),
            focusedLabelColor = colorResource(id = R.color.black),
            unfocusedLabelColor = colorResource(id = R.color.black),
        )


    )
}


