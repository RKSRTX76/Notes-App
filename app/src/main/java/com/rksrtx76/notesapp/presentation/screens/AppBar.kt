package com.rksrtx76.notesapp.presentation.screens

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rksrtx76.notesapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title : String,
    navController: NavController,
    onDrawerIconClick:()-> Unit = {}
){
    TopAppBar(
        title = {
            Text(text = title, color = Color.Black)
        },
        colors = TopAppBarDefaults.topAppBarColors(Color.White),
        navigationIcon = {
            if(title != stringResource(id = R.string.note_app)){
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Arrow Back",
                        tint = Color.Black

                    )
                }
            }else{
                // NavDrawer
                IconButton(onClick = { onDrawerIconClick() }) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.Black
                    )
                }
            }
        }
    )
}