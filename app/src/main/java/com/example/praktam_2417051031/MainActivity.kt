package com.example.praktam_2417051031

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import coil.compose.AsyncImage
import com.example.praktam_2417051031.data.model.Comment
import com.example.praktam_2417051031.data.model.LostItem
import com.example.praktam_2417051031.data.repository.LostRepository
import com.example.praktam_2417051031.ui.screens.*
import com.example.praktam_2417051031.ui.theme.PrakTAM_2417051031Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PrakTAM_2417051031Theme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val context = LocalContext.current
    val repository = remember { LostRepository(context) }
    val startDestination = remember {
        if (repository.getSession() != null) "main" else "login"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                repository = repository,
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("register") {
            RegisterScreen(
                repository = repository,
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }
        composable("main") {
            MainContainer(navController, repository)
        }
        composable("detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            var item by remember { mutableStateOf<LostItem?>(null) }
            LaunchedEffect(id) {
                item = repository.getLostItems().find { it.id == id }
            }
            item?.let {
                DetailScreen(it, navController, repository)
            }
        }
        composable("chat/{contactName}") { backStackEntry ->
            val contactName = backStackEntry.arguments?.getString("contactName") ?: ""
            ChatDetailScreen(contactName, navController, repository)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(navController: NavHostController, repository: LostRepository) {
    var selectedTab by remember { mutableStateOf("home") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lost & Found UNILA", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)) },
                actions = {
                    IconButton(onClick = {
                        repository.clearSession()
                        navController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Log Out")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == "home",
                    onClick = { selectedTab = "home" },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == "lapor",
                    onClick = { selectedTab = "lapor" },
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Lapor") },
                    label = { Text("Lapor") }
                )
                NavigationBarItem(
                    selected = selectedTab == "favorit",
                    onClick = { selectedTab = "favorit" },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorit") },
                    label = { Text("Favorit") }
                )
                NavigationBarItem(
                    selected = selectedTab == "chat",
                    onClick = { selectedTab = "chat" },
                    icon = { Icon(Icons.Default.Send, contentDescription = "Chat") },
                    label = { Text("Chat") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                "home" -> HomeScreen(navController, repository)
                "lapor" -> ReportScreen(repository) {
                    selectedTab = "home"
                }
                "favorit" -> FavoritesScreen(navController, repository)
                "chat" -> ChatListScreen(navController, repository)
            }
        }
    }
}

@Composable
fun DetailScreen(
    item: LostItem,
    navController: NavHostController,
    repository: LostRepository
) {
    val currentUser = remember { repository.getSession() ?: "Me" }
    var isFavorite by remember { mutableStateOf(repository.isFavorite(item.id)) }
    var isResolved by remember { mutableStateOf(item.isResolved) }
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var newCommentText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(item.id) {
        comments = repository.getComments(item.id)
    }

    // Owner check (item uploaded by active user session)
    val isOwner = remember(item.contact, currentUser) {
        item.contact.contains(currentUser, ignoreCase = true) || 
        item.contact.contains("User: $currentUser", ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.itemName,
                    placeholder = painterResource(R.drawable.gambaerr),
                    error = painterResource(R.drawable.gambaerr),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                }

                IconButton(
                    onClick = {
                        repository.toggleFavorite(item.id)
                        isFavorite = repository.isFavorite(item.id)
                        scope.launch {
                            val msg = if (isFavorite) "Ditambahkan ke Favorit!" else "Dihapus dari Favorit!"
                            snackbarHostState.showSnackbar(msg)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Resolved status badge vs Lost/Found badge
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isLost = item.type == ReportType.LOST
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isLost) Color(0xFFFFECEC) else Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isLost) "Barang Hilang" else "Barang Temuan",
                            color = if (isLost) Color(0xFFD32F2F) else Color(0xFF388E3C),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (isResolved) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "SUDAH DITEMUKAN / SELESAI",
                                color = Color(0xFF388E3C),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = item.itemName,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                DetailRowItem(icon = Icons.Default.LocationOn, label = "Lokasi", value = item.location)
                DetailRowItem(icon = Icons.Default.DateRange, label = "Waktu", value = item.dateTime)
                DetailRowItem(icon = Icons.Default.Phone, label = "Kontak", value = item.contact)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Deskripsi",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Actions Buttons (owner resolution button vs contact options)
                if (isResolved) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF388E3C))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Barang ini sudah dikembalikan / diselesaikan oleh pemilik.",
                                color = Color(0xFF388E3C),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isOwner) {
                            Button(
                                onClick = {
                                    repository.resolveItem(item.id)
                                    isResolved = true
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Barang berhasil ditandai selesai!")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Tandai Sudah Ditemukan / Selesai", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        } else {
                            Button(
                                onClick = {
                                    val contactName = if (item.contact.startsWith("IG:")) {
                                        item.contact.substring(3).trim()
                                    } else if (item.contact.startsWith("WA:")) {
                                        "WhatsApp " + item.contact.substring(3).trim()
                                    } else if (item.contact.startsWith("User:")) {
                                        item.contact.substring(5).trim()
                                    } else {
                                        item.contact
                                    }
                                    navController.navigate("chat/$contactName")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(Color(0xFF405DE6), Color(0xFF833AB4))
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues()
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Hubungi Pemilik / Chat", fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Komentar (${comments.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                if (comments.isEmpty()) {
                    Text(
                        "Belum ada komentar. Tanyakan terkait barang di bawah ini.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    comments.forEach { comment ->
                        DetailCommentBubble(comment)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        placeholder = { Text("Tulis komentar...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (newCommentText.isNotBlank()) {
                                val cText = newCommentText
                                newCommentText = ""
                                val newComment = Comment(
                                    id = UUID.randomUUID().toString(),
                                    itemId = item.id,
                                    author = currentUser,
                                    text = cText,
                                    timestamp = "Baru saja"
                                )
                                repository.addComment(newComment)
                                comments = comments + newComment
                                scope.launch {
                                    snackbarHostState.showSnackbar("Komentar dikirim!")
                                }
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF405DE6), Color(0xFF833AB4))
                                ),
                                shape = CircleShape
                            )
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Kirim", tint = Color.White)
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
fun DetailRowItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray
        )
    }
}

@Composable
fun DetailCommentBubble(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        val initials = comment.author.take(2).uppercase()
        val colors = listOf(Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7), Color(0xFF3F51B5), Color(0xFF009688))
        val avatarColor = remember(comment.author) { colors[comment.author.length % colors.size] }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(avatarColor),
            contentAlignment = Alignment.Center
        ) {
            Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.weight(1f)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(comment.author, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(comment.timestamp, color = Color.Gray, fontSize = 10.sp)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(comment.text, fontSize = 13.sp)
            }
        }
    }
}