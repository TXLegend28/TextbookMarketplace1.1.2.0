package com.example.textbookmarketplace.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.textbookmarketplace.domain.model.Textbook
import com.example.textbookmarketplace.ui.components.ConditionBadge
import com.example.textbookmarketplace.ui.theme.GoldPrimary
import com.example.textbookmarketplace.ui.viewmodel.HomeViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: String,
    onBack: () -> Unit,
    onChat: (String) -> Unit,
    onBuy: () -> Unit,
    onReadPdf: (String) -> Unit,
    onOpenDocx: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var textbook by remember { mutableStateOf<Textbook?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val currentUser by viewModel.currentUser.collectAsState()

    // Load book details
    LaunchedEffect(bookId) {
        isLoading = true
        textbook = viewModel.getTextbookById(bookId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (textbook == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Book not found", color = MaterialTheme.colorScheme.error)
            }
        } else {
            val book = textbook!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Cover Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    if (book.localImagePath.isNotEmpty() || book.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = if (book.localImagePath.isNotEmpty()) File(book.localImagePath) else book.imageUrl,
                            contentDescription = book.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp).align(Alignment.Center),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = book.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(text = "by ${book.author}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "R${String.format("%.2f", book.price)}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        ConditionBadge(condition = book.condition)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    DetailRow("ISBN", book.isbn)
                    DetailRow("Edition", book.edition.ifEmpty { "N/A" })
                    DetailRow("Course", book.course.ifEmpty { "General" })
                    DetailRow("Copies Available", "${book.copies}")

                    if (book.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                        Text(text = book.description, style = MaterialTheme.typography.bodyMedium)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Seller Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)

                    DetailRow("Name", book.sellerName)
                    DetailRow("Email", book.sellerEmail)
                    if (book.bankName.isNotEmpty()) {
                        DetailRow("Bank", book.bankName)
                        DetailRow("Account", book.accountNumber)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Buttons Row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:${book.sellerEmail}")
                                    putExtra(Intent.EXTRA_SUBJECT, "Interested in buying one of the available books")
                                    putExtra(Intent.EXTRA_TEXT, "Hi, I'm interested in: ${book.title}\nISBN: ${book.isbn}\nPrice: R${book.price}")
                                }
                                context.startActivity(Intent.createChooser(intent, "Send email"))
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Contact")
                        }

                        OutlinedButton(
                            onClick = { onChat(book.sellerId) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Chat")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Digital Content Buttons
                    if (book.digitalFileType == "pdf" && book.digitalFilePath.isNotEmpty()) {
                        Button(
                            onClick = { onReadPdf(book.digitalFilePath) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Read PDF")
                        }
                    } else if (book.digitalFileType == "docx" && book.digitalFilePath.isNotEmpty()) {
                        Button(
                            onClick = { onOpenDocx(book.digitalFilePath) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open DOCX")
                        }
                    }

                    // Buy Button (ONLY visible if current user is NOT the seller)
                    if (currentUser.id != book.sellerId) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onBuy,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Buy Now - R${String.format("%.2f", book.price)}")
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}