package com.example.textbookmarketplace.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.textbookmarketplace.ui.screens.*
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object AddBook : Screen("add_book/{bookId}") {
        fun createRoute(bookId: String = "new") = "add_book/$bookId"
    }
    object BookDetail : Screen("book_detail/{bookId}") {
        fun createRoute(bookId: String) = "book_detail/$bookId"
    }
    object MyListings : Screen("my_listings")
    object Settings : Screen("settings")
    object Chat : Screen("chat/{bookId}/{sellerId}") {
        fun createRoute(bookId: String, sellerId: String) = "chat/$bookId/$sellerId"
    }
    object Payment : Screen("payment/{bookId}") {
        fun createRoute(bookId: String) = "payment/$bookId"
    }
    object PdfViewer : Screen("pdf_viewer/{filePath}") {
        fun createRoute(filePath: String): String {
            val encodedPath = URLEncoder.encode(filePath, StandardCharsets.UTF_8.toString())
            return "pdf_viewer/$encodedPath"
        }
    }
}

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = Screen.Login.route,
    onLoginSuccess: () -> Unit,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    onLoginSuccess()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            val context = LocalContext.current
            HomeScreen(
                onBookClick = { bookId ->
                    navController.navigate(Screen.BookDetail.createRoute(bookId))
                },
                onAddBook = { navController.navigate(Screen.AddBook.createRoute("new")) },
                onMyListings = { navController.navigate(Screen.MyListings.route) },
                onWebSearch = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://books.google.com/books?q=textbooks")
                    )
                    context.startActivity(intent)
                },
                onSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(
            route = Screen.AddBook.route,
            arguments = listOf(navArgument("bookId") {
                type = NavType.StringType
                defaultValue = "new"
            })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: "new"
            AddBookScreen(
                bookId = if (bookId == "new") null else bookId,
                onBookAdded = {
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.BookDetail.route,
            arguments = listOf(navArgument("bookId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            val context = LocalContext.current
            BookDetailScreen(
                bookId = bookId,
                onBack = { navController.popBackStack() },
                onReadPdf = { filePath ->
                    navController.navigate(Screen.PdfViewer.createRoute(filePath))
                },
                onOpenDocx = { filePath ->
                    try {
                        val file = File(filePath)
                        if (file.exists()) {
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                file
                            )
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(uri, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(intent, "Open DOCX"))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                onChat = { sellerId ->
                    navController.navigate(Screen.Chat.createRoute(bookId, sellerId))
                },
                onBuy = {
                    navController.navigate(Screen.Payment.createRoute(bookId))
                }
            )
        }

        composable(Screen.MyListings.route) {
            MyListingsScreen(
                onEdit = { bookId ->
                    navController.navigate(Screen.AddBook.createRoute(bookId))
                },
                onBookClick = { bookId ->
                    navController.navigate(Screen.BookDetail.createRoute(bookId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onLogout = {
                    onLogout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("bookId") { type = NavType.StringType },
                navArgument("sellerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            val sellerId = backStackEntry.arguments?.getString("sellerId") ?: ""
            ChatScreen(
                bookId = bookId,
                sellerId = sellerId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Payment.route,
            arguments = listOf(navArgument("bookId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            PaymentScreen(
                bookId = bookId,
                onPaymentSuccess = {
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PdfViewer.route,
            arguments = listOf(navArgument("filePath") { type = NavType.StringType })
        ) { backStackEntry ->
            val filePath = backStackEntry.arguments?.getString("filePath") ?: ""
            PdfViewerScreen(
                filePath = filePath,
                onBack = { navController.popBackStack() }
            )
        }
    }
}