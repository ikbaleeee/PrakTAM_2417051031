package Model

import com.example.praktam_2417051031.R
import com.example.praktam_2417051031.ReportType

object LostFoundSource {
    val dummyReports = listOf(
        LostItem(
            type = ReportType.LOST,
            itemName = "Dompet Hitam",
            description = "Dompet warna hitam berisi KTP & KTM. Kemungkinan jatuh di sekitar GKU.",
            location = "GKU UNILA",
            dateTime = "1 Maret 2026, 10:30",
            contact = "WA: 08xxxxxxxxxx",
            images = listOf(
                R.drawable.dompet1,
                R.drawable.dompet1,
                R.drawable.dompet1
            )
        ),
        LostItem(
            type = ReportType.LOST,
            itemName = "Kartu KTM",
            description = "KTM atas nama (isi sendiri). Terakhir terlihat di area perpustakaan.",
            location = "Perpustakaan UNILA",
            dateTime = "1 Maret 2026, 13:10",
            contact = "IG: @username_kamu",
            images = listOf(
                R.drawable.ktm1,
                R.drawable.ktm1,
                R.drawable.ktm1,
                R.drawable.ktm1
            )
        ),
        LostItem(
            type = ReportType.FOUND,
            itemName = "Kunci Motor",
            description = "Ditemukan kunci motor dengan gantungan warna merah.",
            location = "Parkiran FEB",
            dateTime = "1 Maret 2026, 14:00",
            contact = "IG: @lostfound_unila",
            images = listOf(
                R.drawable.kunci1,
                R.drawable.kunci1,
                R.drawable.kunci1,
                R.drawable.kunci1,
                R.drawable.kunci1
            )
        )
    )
}