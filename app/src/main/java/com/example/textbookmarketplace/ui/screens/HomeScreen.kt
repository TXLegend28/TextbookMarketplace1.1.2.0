package com.example.textbookmarketplace.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.textbookmarketplace.domain.model.UiState
import com.example.textbookmarketplace.ui.components.*
import com.example.textbookmarketplace.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onBookClick: (String) -> Unit,
    onAddBook: () -> Unit,
    onWebSearch: () -> Unit,
    onMyListings: () -> Unit,
    onSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val textbooksState by viewModel.textbooks.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSeller by viewModel.isSeller.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "TM Marketplace",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        if (isSeller) {
                            DropdownMenuItem(
                                text = { Text("My Listings") },
                                onClick = { showMenu = false; onMyListings() },
                                leadingIcon = { Icon(Icons.Default.List, null) }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Web Search") },
                            onClick = {
                                showMenu = false
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://books.google.com/books?q=textbooks")
                                )
                                context.startActivity(intent)
                            },
                            leadingIcon = { Icon(Icons.Default.Public, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = { showMenu = false; onSettings() },
                            leadingIcon = { Icon(Icons.Default.Settings, null) }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (isSeller) {
                FloatingActionButton(
                    onClick = onAddBook,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Book",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::setSearchQuery,
                placeholder = { Text("Search books, authors, sellers...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.extraLarge,
                singleLine = true
            )

            when (val state = textbooksState) {
                is UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Empty -> {
                    EmptyState(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        },
                        title = "No Books Yet",
                        subtitle = "Be the first to list a textbook or check back soon!",
                        action = {
                            TextButton(onClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://books.google.com/books?q=textbooks")
                                )
                                context.startActivity(intent)
                            }) {
                                Text("Search on Google Books")
                            }
                        }
                    )
                }
                is UiState.Success -> {
                    val books = state.data
                    if (books.isEmpty() && searchQuery.isNotEmpty()) {
                        EmptyState(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.SearchOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp)
                                )
                            },
                            title = "No Results",
                            subtitle = "No books found for \"$searchQuery\"",
                            action = {
                                TextButton(onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://books.google.com/books?q=${Uri.encode(searchQuery)}")
                                    )
                                    context.startActivity(intent)
                                }) {
                                    Text("Search on Google Books")
                                }
                            }
                        )
                    } else if (books.isEmpty()) {
                        EmptyState(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Book,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp)
                                )
                            },
                            title = "No Textbooks",
                            subtitle = "No books available yet",
                            action = {
                                if (isSeller) {
                                    Button(onClick = onAddBook) {
                                        Text("Add First Book")
                                    }
                                } else {
                                    TextButton(onClick = {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://books.google.com/books?q=textbooks")
                                        )
                                        context.startActivity(intent)
                                    }) {
                                        Text("Browse Google Books")
                                    }
                                }
                            }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(books, key = { it.id }) { book ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn() + slideInVertically(),
                                    exit = fadeOut()
                                ) {
                                    TextbookCard(
                                        textbook = book,
                                        onClick = { onBookClick(book.id) }
                                    )
                                }
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}