package com.example.praktam_2417051031

import Model.LostFoundSource
import Model.LostItem
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.praktam_2417051031.ui.theme.PrakTAM_2417051031Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            LostFoundScreen(navController)
        }

        composable("detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")

            val item = LostFoundSource.dummyReports.find { it.id == id }

            if (item != null) {
                DetailScreen(item, navController)
            }
        }
    }
}

@Composable
fun LostFoundScreen(navController: NavHostController) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text("Halo UNILA - Lost & Found", style = MaterialTheme.typography.headlineMedium)
        }

        item {
            Text("Preview Barang")

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(LostFoundSource.dummyReports) {
                    MiniCard(it, navController)
                }
            }
        }

        items(LostFoundSource.dummyReports) {
            LostItemCard(it, navController)
        }
    }
}

@Composable
fun MiniCard(item: LostItem, navController: NavHostController) {

    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { navController.navigate("detail/${item.id}") }
    ) {
        Column {
            val image = item.images.firstOrNull()

            if (image != null) {
                Image(
                    painter = painterResource(image),
                    contentDescription = null,
                    modifier = Modifier.height(90.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Text(item.itemName, modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun LostItemCard(item: LostItem, navController: NavHostController) {

    var isFavorite by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var komentar by remember { mutableStateOf("") }
    var listKomentar by remember { mutableStateOf(listOf<String>()) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(modifier = Modifier.padding(12.dp)) {

            val image = item.images.firstOrNull()

            Box {
                if (image != null) {
                    Image(
                        painter = painterResource(image),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(item.itemName, style = MaterialTheme.typography.titleLarge)

            Button(
                onClick = { navController.navigate("detail/${item.id}") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lihat Detail")
            }

            Button(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Komentar")
            }

            if (expanded) {

                TextField(
                    value = komentar,
                    onValueChange = { komentar = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (komentar.isNotEmpty()) {
                            listKomentar = listKomentar + komentar
                            komentar = ""

                            scope.launch {
                                snackbarHostState.showSnackbar("Komentar berhasil ditambahkan!")
                            }
                        }
                    }
                ) {
                    Text("Kirim")
                }

                listKomentar.forEach {
                    Text("- $it")
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun DetailScreen(item: LostItem, navController: NavHostController) {

    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.padding(16.dp)) {

            val image = item.images.firstOrNull()

            if (image != null) {
                Image(
                    painter = painterResource(image),
                    contentDescription = item.itemName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(item.itemName, style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(8.dp))

            Text(item.description)
            Text("Lokasi: ${item.location}")
            Text("Waktu: ${item.dateTime}")
            Text("Kontak: ${item.contact}")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        delay(2000)
                        snackbarHostState.showSnackbar("Berhasil menghubungi pemilik!")
                        isLoading = false
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Hubungi Pemilik")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Kembali")
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}