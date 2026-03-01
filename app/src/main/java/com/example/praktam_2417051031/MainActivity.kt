package com.example.praktam_2417051031

import Model.LostFoundSource
import Model.LostItem
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.praktam_2417051031.ui.theme.PrakTAM_2417051031Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PrakTAM_2417051031Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LostFoundScreen(innerPadding)
                }
            }
        }
    }
}

enum class ReportType { LOST, FOUND }

@Composable
fun LostFoundScreen(innerPadding: PaddingValues) {
    var selectedTab by remember { mutableStateOf(ReportType.LOST) }

    // data dummy
    val allReports = remember { LostFoundSource.dummyReports }

    // list sesuai tab
    val reports = remember(selectedTab, allReports) {
        allReports.filter { it.type == selectedTab }
    }

    // item yang sedang dipilih untuk detail
    var selectedItem by remember(selectedTab) { mutableStateOf(reports.firstOrNull()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        Text(
            text = "Lost & Found - Universitas Lampung",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        // Tabs Hilang / Ditemukan
        TabRow(selectedTabIndex = if (selectedTab == ReportType.LOST) 0 else 1) {
            Tab(
                selected = selectedTab == ReportType.LOST,
                onClick = {
                    selectedTab = ReportType.LOST
                    selectedItem = allReports.firstOrNull { it.type == ReportType.LOST }
                },
                text = { Text("Hilang") }
            )
            Tab(
                selected = selectedTab == ReportType.FOUND,
                onClick = {
                    selectedTab = ReportType.FOUND
                    selectedItem = allReports.firstOrNull { it.type == ReportType.FOUND }
                },
                text = { Text("Ditemukan") }
            )
        }

        Spacer(Modifier.height(12.dp))

        // Layout: kiri list, kanan detail
        Row(modifier = Modifier.fillMaxSize()) {

            // List laporan (kiri)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                items(reports) { item ->
                    ReportCard(
                        item = item,
                        onClick = { selectedItem = item }
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // Detail (kanan)
            if (selectedItem != null) {
                ReportDetail(
                    item = selectedItem!!,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            } else {
                Text(
                    text = "Belum ada laporan.",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ReportCard(item: LostItem, onClick: () -> Unit) {
    // ambil gambar pertama sebagai thumbnail
    val thumbnail = item.images.firstOrNull()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            if (thumbnail != null) {
                Image(
                    painter = painterResource(id = thumbnail),
                    contentDescription = item.itemName,
                    modifier = Modifier.size(72.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text(text = item.itemName, style = MaterialTheme.typography.titleMedium)
                Text(text = "Lokasi: ${item.location}")
                Text(text = "Kontak: ${item.contact}")
            }
        }
    }
}

@Composable
fun ReportDetail(item: LostItem, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Slider gambar horizontal (3–5 gambar)
            LazyRow {
                items(item.images) { image ->
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = item.itemName,
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(width = 260.dp, height = 180.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(text = item.itemName, style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(6.dp))

            val jenis = if (item.type == ReportType.LOST) "Hilang" else "Ditemukan"
            Text(text = "Jenis: $jenis")
            Text(text = "Lokasi: ${item.location}")
            Text(text = "Waktu: ${item.dateTime}")

            Spacer(Modifier.height(10.dp))

            Text(text = "Deskripsi:", style = MaterialTheme.typography.titleMedium)
            Text(text = item.description)

            Spacer(Modifier.height(12.dp))

            Text(text = "Kontak:", style = MaterialTheme.typography.titleMedium)
            Text(text = item.contact)
        }
    }
}