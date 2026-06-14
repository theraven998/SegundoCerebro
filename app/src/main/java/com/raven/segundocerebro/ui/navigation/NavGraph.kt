package com.raven.segundocerebro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.raven.segundocerebro.ui.screens.ContainerScreen
import com.raven.segundocerebro.ui.screens.HomeScreen
import com.raven.segundocerebro.ui.screens.InboxScreen
import com.raven.segundocerebro.ui.screens.NoteEditorScreen
import com.raven.segundocerebro.ui.screens.ParaListScreen
import com.raven.segundocerebro.ui.screens.SearchScreen

@Composable
fun AppNavGraph() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onOpenInbox = { nav.navigate(Routes.INBOX) },
                onOpenPara = { nav.navigate(Routes.para(it.name)) },
                onOpenNote = { nav.navigate(Routes.editor(noteId = it)) },
                onNewNote = { nav.navigate(Routes.editor()) },
                onSearch = { nav.navigate(Routes.SEARCH) }
            )
        }
        composable(Routes.INBOX) {
            InboxScreen(
                onBack = { nav.popBackStack() },
                onOpenNote = { nav.navigate(Routes.editor(noteId = it)) }
            )
        }
        composable(Routes.SEARCH) {
            SearchScreen(
                onBack = { nav.popBackStack() },
                onOpenNote = { nav.navigate(Routes.editor(noteId = it)) }
            )
        }
        composable(
            Routes.PARA,
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) {
            ParaListScreen(
                onBack = { nav.popBackStack() },
                onOpenContainer = { nav.navigate(Routes.container(it)) }
            )
        }
        composable(
            Routes.CONTAINER,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            ContainerScreen(
                onBack = { nav.popBackStack() },
                onOpenNote = { nav.navigate(Routes.editor(noteId = it)) },
                onNewNote = { cid -> nav.navigate(Routes.editor(containerId = cid)) }
            )
        }
        composable(
            Routes.EDITOR,
            arguments = listOf(
                navArgument("noteId") { type = NavType.StringType; defaultValue = "" },
                navArgument("containerId") { type = NavType.StringType; defaultValue = "" }
            )
        ) {
            NoteEditorScreen(onBack = { nav.popBackStack() })
        }
    }
}
