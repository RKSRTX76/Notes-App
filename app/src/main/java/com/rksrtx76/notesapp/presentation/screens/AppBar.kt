package com.rksrtx76.notesapp.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.identity.Identity
import com.rksrtx76.notesapp.R
import com.rksrtx76.notesapp.presentation.authentication.GoogleAuthClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title : String,
//    onBackNavClicked:() -> Unit = {},
    navController: NavController,
    onDrawerIconClick:()-> Unit = {}
){
    TopAppBar(
        title = {
            Text(text = title, color = Color.White)
        },
        colors = TopAppBarDefaults.topAppBarColors(colorResource(id = R.color.app_bar_color)),
        navigationIcon = {
            if(title != stringResource(id = R.string.note_app)){
                // we are inside todo item
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Arrow Back",
                        tint = Color.White

                    )
                }
            }else{
                // NavDrawer
                IconButton(onClick = { onDrawerIconClick() }) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }
            }
        }
    )
}