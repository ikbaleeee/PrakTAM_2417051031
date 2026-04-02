package com.example.praktam_2417051031

import Model.LostFoundSource
import Model.LostItem
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.praktam_2417051031.ui.theme.PrakTAM_2417051031Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PrakTAM_2417051031Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LostFoundScreen()
                }
            }
        }
    }
}

@Composable
fun LostFoundScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        item {
            Text(
                text = "Halo UNILA - Lost & Found",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            Text(
                text = "Preview Barang",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(LostFoundSource.dummyReports) {
                    MiniLostItemCard(it)
                }
            }
        }

        items(LostFoundSource.dummyReports) {
            LostItemCard(it)
        }
    }
}

@Composable
fun MiniLostItemCard(item: LostItem) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            val image = item.images.firstOrNull()

            if (image != null) {
                Image(
                    painter = painterResource(id = image),
                    contentDescription = item.itemName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                text = item.itemName,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

@Composable
fun LostItemCard(item: LostItem) {

    var isFavorite by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var komentar by remember { mutableStateOf("") }
    var listKomentar by remember { mutableStateOf(listOf<String>()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(modifier = Modifier.padding(12.dp)) {

            val image = item.images.firstOrNull()

            Box {
                if (image != null) {
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = item.itemName,
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
                        imageVector = if (isFavorite)
                            Icons.Filled.Favorite
                        else
                            Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (item.type == ReportType.LOST)
                    "❌ Barang Hilang"
                else
                    "✅ Barang Ditemukan",
                color = if (item.type == ReportType.LOST)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.itemName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (expanded) "Sembunyikan Detail" else "Lihat Detail")
            }

            if (expanded) {

                Spacer(modifier = Modifier.height(8.dp))

                Text(item.description, style = MaterialTheme.typography.bodyMedium)
                Text("Lokasi: ${item.location}", style = MaterialTheme.typography.bodySmall)
                Text("Waktu: ${item.dateTime}", style = MaterialTheme.typography.bodySmall)
                Text("Kontak: ${item.contact}", style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = komentar,
                    onValueChange = { komentar = it },
                    label = { Text("Tulis komentar...") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (komentar.isNotEmpty()) {
                            listKomentar = listKomentar + komentar
                            komentar = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kirim Komentar")
                }

                listKomentar.forEach {
                    Text("- $it", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Hubungi Pemilik")
            }
        }
    }
}