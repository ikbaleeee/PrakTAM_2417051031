package com.example.praktam_2417051031.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.praktam_2417051031.R
import com.example.praktam_2417051031.data.model.LostItem
import com.example.praktam_2417051031.data.repository.LostRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    repository: LostRepository,
    onLogout: () -> Unit
) {
    val currentUser = remember { repository.getSession() ?: "Me" }
    var userItems by remember { mutableStateOf<List<LostItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var displayName by remember { mutableStateOf(repository.getProfileDisplayName(currentUser)) }
    var bio by remember { mutableStateOf(repository.getProfileBio(currentUser)) }
    var avatarUrl by remember { mutableStateOf(repository.getProfileAvatarUrl(currentUser) ?: "") }
    var showEditDialog by remember { mutableStateOf(false) }

    var inputDisplayName by remember { mutableStateOf(displayName) }
    var inputBio by remember { mutableStateOf(bio) }
    var inputAvatarUrl by remember { mutableStateOf(avatarUrl) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            inputAvatarUrl = it.toString()
        }
    }

    LaunchedEffect(Unit) {
        val allItems = repository.getLostItems()
        userItems = allItems.filter {
            it.contact.contains(currentUser, ignoreCase = true) ||
            it.contact.contains("User: $currentUser", ignoreCase = true)
        }
        isLoading = false
    }

    val totalReports = userItems.size
    val totalResolved = userItems.count { it.isResolved }

    val initials = displayName.take(2).uppercase()
    val colors = listOf(Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7), Color(0xFF3F51B5), Color(0xFF009688))
    val avatarColor = remember(displayName) { colors[displayName.length % colors.size] }

    if (showEditDialog) {

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profil", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            if (inputAvatarUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = inputAvatarUrl,
                                    contentDescription = "Preview Avatar",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = initials,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }
                        
                        Button(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) {
                            Text("Pilih Foto Galeri", fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedTextField(
                        value = inputAvatarUrl,
                        onValueChange = { inputAvatarUrl = it },
                        label = { Text("URL Foto Profil (Opsional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = inputDisplayName,
                        onValueChange = { inputDisplayName = it },
                        label = { Text("Nama Lengkap") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = inputBio,
                        onValueChange = { inputBio = it },
                        label = { Text("Bio Profil") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputDisplayName.isNotBlank()) {
                            repository.saveProfileInfo(currentUser, inputDisplayName.trim(), inputBio.trim(), inputAvatarUrl.trim())
                            displayName = repository.getProfileDisplayName(currentUser)
                            bio = repository.getProfileBio(currentUser)
                            avatarUrl = repository.getProfileAvatarUrl(currentUser) ?: ""
                            showEditDialog = false
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(bottom = 90.dp, top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Profile Header Card spanning across all grid columns
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp, start = 8.dp, end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Avatar
                             Box(
                                 modifier = Modifier
                                     .size(80.dp)
                                     .clip(CircleShape)
                                     .background(avatarColor),
                                 contentAlignment = Alignment.Center
                             ) {
                                 if (avatarUrl.isNotEmpty()) {
                                     AsyncImage(
                                         model = avatarUrl,
                                         contentDescription = "Avatar",
                                         modifier = Modifier.fillMaxSize(),
                                         contentScale = ContentScale.Crop
                                     )
                                 } else {
                                     Text(
                                         text = initials,
                                         color = Color.White,
                                         fontWeight = FontWeight.Bold,
                                         fontSize = 28.sp
                                     )
                                 }
                             }

                            Spacer(modifier = Modifier.width(24.dp))

                            // Stats
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ProfileStatItem(count = totalReports.toString(), label = "Laporan")
                                ProfileStatItem(count = totalResolved.toString(), label = "Selesai")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Display Name
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        // Username alias
                        Text(
                            text = "@$currentUser",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        // Bio Info
                        Text(
                            text = bio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Buttons (Edit Profile & Logout)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                             Button(
                                 onClick = {
                                     inputDisplayName = displayName
                                     inputBio = bio
                                     inputAvatarUrl = avatarUrl
                                     showEditDialog = true
                                 },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Edit Profil", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { onLogout() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFECEC),
                                    contentColor = Color(0xFFD32F2F)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Log Out", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Unggahan Saya",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                        )
                    }
                }

                // Grid of items
                if (userItems.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Belum ada laporan barang yang Anda buat.",
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    items(userItems) { item ->
                        ProfileGridItem(item = item) {
                            navController.navigate("detail/${item.id}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStatItem(count: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
fun ProfileGridItem(item: LostItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.itemName,
            placeholder = painterResource(R.drawable.gambaerr),
            error = painterResource(R.drawable.gambaerr),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        if (item.isResolved) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Resolved",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "DITEMUKAN",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            val isLost = item.type == com.example.praktam_2417051031.ReportType.LOST
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(
                        color = if (isLost) Color(0xFFFF5252).copy(alpha = 0.9f) else Color(0xFF4CAF50).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isLost) "HILANG" else "TEMUAN",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
