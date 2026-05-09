package com.example.textbookmarketplace.ui.screens

import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    filePath: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var pages by remember { mutableStateOf<List<android.graphics.Bitmap>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(filePath) {
        try {
            var fileToRender: File? = null

            // If it's a remote URL, download it first
            if (filePath.startsWith("http")) {
                fileToRender = File(context.cacheDir, "temp_pdf_${System.currentTimeMillis()}.pdf")
                URL(filePath).openStream().use { input ->
                    fileToRender.outputStream().use { output -> input.copyTo(output) }
                }
            } else {
                // Local file
                fileToRender = File(filePath)
            }

            if (fileToRender != null && fileToRender.exists()) {
                val fd = ParcelFileDescriptor.open(fileToRender, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(fd)
                val bitmaps = mutableListOf<android.graphics.Bitmap>()

                for (i in 0 until renderer.pageCount) {
                    val page = renderer.openPage(i)
                    val bitmap = android.graphics.Bitmap.createBitmap(page.width, page.height, android.graphics.Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bitmaps.add(bitmap)
                    page.close()
                }

                renderer.close()
                fd.close()

                // Clean up downloaded file
                if (filePath.startsWith("http")) fileToRender.delete()

                pages = bitmaps
                isLoading = false
            } else {
                error = "File not found"
                isLoading = false
            }
        } catch (e: Exception) {
            error = "Failed to load PDF: ${e.message}"
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Read Book") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = MaterialTheme.colorScheme.onPrimary)
            )
        }
    ) { padding ->
        when {
            isLoading -> Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            error != null -> Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text(error!!, color = MaterialTheme.colorScheme.error) }
            pages.isEmpty() -> Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text("No pages to display") }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.surface), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(pages) { bitmap ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Image(bitmap = bitmap.asImageBitmap(), contentDescription = "PDF Page", modifier = Modifier.fillMaxWidth().heightIn(min = 400.dp))
                        }
                    }
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }
    }

    DisposableEffect(Unit) { onDispose { pages.forEach { it.recycle() } } }
}