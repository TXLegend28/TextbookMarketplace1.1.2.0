package com.example.textbookmarketplace.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.textbookmarketplace.domain.model.UiState
import com.example.textbookmarketplace.ui.viewmodel.AddBookViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddBookScreen(
    onBookAdded: () -> Unit,
    onBack: () -> Unit,
    viewModel: AddBookViewModel = hiltViewModel()
) {
    val addState by viewModel.addState.collectAsState()

    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var edition by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var copies by remember { mutableStateOf("1") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var condition by remember { mutableStateOf("Good") }
    var category by remember { mutableStateOf("General") }
    var localImageUri by remember { mutableStateOf<Uri?>(null) }
    var hasDocument by remember { mutableStateOf(false) }

    // Seller Info (including email for contact)
    var sellerName by remember { mutableStateOf("") }
    var sellerEmail by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var accountNumber by remember { mutableStateOf("") }

    val conditions = listOf("New", "Like New", "Good", "Fair", "Poor")
    val categories = listOf("General", "Engineering", "Medicine", "Law", "Business", "IT", "Science")

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { localImageUri = it; viewModel.setImage(it) }
    }

    val docPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { viewModel.setDocument(it); hasDocument = true }
    }

    LaunchedEffect(addState) {
        if (addState is UiState.Success) { viewModel.resetState(); onBookAdded() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("List a Textbook") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Image Picker Card
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(140.dp).clip(RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                        if (localImageUri != null) {
                            AsyncImage(model = localImageUri, contentDescription = "Book cover", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = { imagePicker.launch("image/*") }) {
                        Icon(Icons.Default.Image, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Choose Cover Image")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Document Picker
            OutlinedButton(onClick = { docPicker.launch(arrayOf("application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")) }, modifier = Modifier.fillMaxWidth()) {
                Icon(imageVector = if (hasDocument) Icons.Default.CheckCircle else Icons.Default.AttachFile, contentDescription = null, tint = if (hasDocument) MaterialTheme.colorScheme.primary else LocalContentColor.current)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (hasDocument) "Document Attached" else "Attach PDF or DOCX")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Book Details
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title *") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Author *") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = isbn, onValueChange = { isbn = it }, label = { Text("ISBN *") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                OutlinedTextField(value = edition, onValueChange = { edition = it }, label = { Text("Edition") }, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(value = course, onValueChange = { course = it }, label = { Text("Course") }, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                OutlinedTextField(value = copies, onValueChange = { copies = it }, label = { Text("Copies *") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price (R) *") }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Category", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { item -> FilterChip(selected = category == item, onClick = { category = item }, label = { Text(item) }) }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Condition", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                conditions.forEach { item -> FilterChip(selected = condition == item, onClick = { condition = item }, label = { Text(item) }) }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth().height(120.dp), maxLines = 5)

            // --- SELLER & PAYMENT INFO SECTION ---
            Spacer(modifier = Modifier.height(24.dp))
            Text("Seller & Payment Information", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("This info will be shown to buyers for contact and payment", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = sellerName,
                onValueChange = { sellerName = it },
                label = { Text("Seller Name *") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = sellerEmail,
                onValueChange = { sellerEmail = it },
                label = { Text("Seller Email * (for buyer contact)") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("Buyers will email this address when interested") }
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = bankName,
                onValueChange = { bankName = it },
                label = { Text("Bank Name") },
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("Optional - for payment reference") }
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = accountNumber,
                onValueChange = { accountNumber = it },
                label = { Text("Account Number") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    val copiesInt = copies.toIntOrNull() ?: 1
                    val priceDouble = price.toDoubleOrNull() ?: 0.0
                    viewModel.addTextbook(
                        title = title, author = author, isbn = isbn, edition = edition,
                        copies = copiesInt, price = priceDouble, course = course, condition = condition,
                        description = description, category = category,
                        sellerName = sellerName, sellerEmail = sellerEmail,
                        bankName = bankName, accountNumber = accountNumber
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = addState !is UiState.Loading
            ) {
                if (addState is UiState.Loading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("List Textbook", style = MaterialTheme.typography.titleMedium)
                }
            }

            if (addState is UiState.Error) {
                Text(text = (addState as UiState.Error).message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}