package org.revelar.photoviewer.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.revelar.photoviewer.ui.components.PermissionGate
import org.revelar.photoviewer.ui.gallery.GalleryScreen
import org.revelar.photoviewer.ui.viewer.ViewerScreen

@Composable
fun RevelarNav() {
    val navController = rememberNavController()

    PermissionGate {
        NavHost(navController = navController, startDestination = "gallery") {
            composable("gallery") {
                GalleryScreen(
                    onOpenPhoto = { id -> navController.navigate("viewer/$id") }
                )
            }
            composable(
                route = "viewer/{photoId}",
                arguments = listOf(navArgument("photoId") { type = NavType.LongType })
            ) { entry ->
                val photoId = entry.arguments?.getLong("photoId") ?: return@composable
                ViewerScreen(
                    photoId = photoId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
