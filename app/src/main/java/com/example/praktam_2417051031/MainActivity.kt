package com.example.praktam_2417051031

import Model.LostFoundSource
import Model.LostItem
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import com.example.praktam_2417051031.ui.theme.PrakTAM_2417051031Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PrakTAM_2417051031Theme {
                LostFoundScreen()
            }
        }
    }
}

@Composable
fun LostFoundScreen() {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // HEADER
        item {
            Text(
                text = "Halo UNILA - Lost & Found",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // 🔥 LAZY ROW (MODUL 6 KELIATAN)
        item {
            Text(
                text = "Preview Barang",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(LostFoundSource.dummyReports) { item ->
                    MiniLostItemCard(item)
                }
            }
        }

        // LIST UTAMA
        items(LostFoundSource.dummyReports) { item ->
            LostItemCard(item)
        }
    }
}

@Composable
fun MiniLostItemCard(item: LostItem) {

    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(12.dp)
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
                fontWeight = FontWeight.Bold,
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
        shape = RoundedCornerShape(16.dp)
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
                        tint = if (isFavorite) Color.Red else Color.White
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
                    Color.Red
                else
                    Color(0xFF2E7D32),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.itemName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
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

                Text(text = item.description)
                Text(text = "Lokasi: ${item.location}")
                Text(text = "Waktu: ${item.dateTime}")
                Text(text = "Kontak: ${item.contact}")

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
                    Text("- $it")
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