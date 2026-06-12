package com.example.praktam_2417051031.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import coil.compose.AsyncImage
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.praktam_2417051031.R
import com.example.praktam_2417051031.ReportType
import com.example.praktam_2417051031.data.model.LostItem
import com.example.praktam_2417051031.data.repository.LostRepository
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    repository: LostRepository,
    onSuccess: () -> Unit
) {
    val currentUser = remember { repository.getSession() ?: "Me" }
    
    var itemName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var dateTime by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("User: $currentUser") }
    var imageUrl by remember { mutableStateOf("") }
    var reportType by remember { mutableStateOf(ReportType.LOST) }

    var expandedLocationDropdown by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    // Activity launcher for picking local image
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUrl = it.toString()
        }
    }

    val locationsUnila = listOf(
        "GKU UNILA",
        "Perpustakaan Pusat",
        "Gedung Rektorat",
        "GSG UNILA",
        "Parkiran FEB",
        "FMIPA",
        "FKIP",
        "FISIP",
        "Fakultas Kedokteran",
        "Fakultas Teknik",
        "Fakultas Hukum",
        "Fakultas Pertanian"
    )

    val presetImages = listOf(
        "https://images.unsplash.com/photo-1579014134953-1580d7f123f3" to "Dompet",
        "https://images.unsplash.com/photo-1614267119077-51bdcbf9f77a?w=400" to "Kartu/KTM",
        "https://images.unsplash.com/photo-1710006548781-eff5670376fa" to "Kunci",
        "https://images.unsplash.com/photo-1692411643820-86e678337b5b?w=400" to "Botol Minum",
        "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400" to "Handphone"
    )

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onSuccess()
            },
            title = { Text("Laporan Berhasil") },
            text = { Text("Laporan barang Anda berhasil diunggah dan dapat dilihat oleh civitas akademika Universitas Lampung.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onSuccess()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text(
                text = "Lapor Kehilangan / Penemuan",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Bantu temukan barang hilang di lingkungan Universitas Lampung",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        if (errorMessage.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(errorMessage, color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isLost = reportType == ReportType.LOST
            val isFound = reportType == ReportType.FOUND

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (isLost) Color(0xFFFF5252) else Color.Transparent)
                    .clickable { reportType = ReportType.LOST },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Barang Hilang",
                    color = if (isLost) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (isFound) Color(0xFF4CAF50) else Color.Transparent)
                    .clickable { reportType = ReportType.FOUND },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Barang Temuan",
                    color = if (isFound) Color.White else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    label = { Text("Nama Barang") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi Barang") },
                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Lokasi (di sekitar UNILA)") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { expandedLocationDropdown = !expandedLocationDropdown }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    DropdownMenu(
                        expanded = expandedLocationDropdown,
                        onDismissRequest = { expandedLocationDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        locationsUnila.forEach { loc ->
                            DropdownMenuItem(
                                text = { Text(loc) },
                                onClick = {
                                    location = loc
                                    expandedLocationDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = dateTime,
                    onValueChange = { dateTime = it },
                    label = { Text("Waktu Kejadian (contoh: 8 Juni)") },
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Kontak Pemilik / Penemu") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Foto Barang:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { galleryLauncher.launch("image/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Buka Galeri", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        "Atau masukkan URL Web Gambar:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("URL Gambar") },
                        leadingIcon = { Icon(Icons.Default.AddCircle, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    if (imageUrl.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Preview Foto Terpilih:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.LightGray)
                        ) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Preview Gambar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(R.drawable.gambaerr),
                                error = painterResource(R.drawable.gambaerr)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Gunakan Gambar Preset Cepat:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        presetImages.forEach { (url, label) ->
                            val isSelected = imageUrl == url
                            SuggestionChip(
                                onClick = { imageUrl = url },
                                label = { Text(label) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                if (itemName.isEmpty() || description.isEmpty() || location.isEmpty() || dateTime.isEmpty() || contact.isEmpty()) {
                    errorMessage = "Harap isi semua kolom wajib!"
                } else {
                    errorMessage = ""
                    val finalImage = if (imageUrl.isEmpty()) {
                        "https://images.unsplash.com/photo-1579014134953-1580d7f123f3"
                    } else {
                        imageUrl
                    }
                    val newItem = LostItem(
                        id = UUID.randomUUID().toString(),
                        itemName = itemName,
                        description = description,
                        location = location,
                        dateTime = dateTime,
                        contact = contact,
                        imageUrl = finalImage,
                        type = reportType
                    )
                    scope.launch {
                        repository.addLostItem(newItem)
                        showSuccessDialog = true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp)
        ) {
            Icon(Icons.Default.Send, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Kirim Laporan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
