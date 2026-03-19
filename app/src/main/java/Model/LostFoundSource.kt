package Model

import com.example.praktam_2417051031.R
import com.example.praktam_2417051031.ReportType

object LostFoundSource {

    val dummyReports = listOf(

        LostItem(
            type = ReportType.LOST,
            itemName = "Dompet Hitam",
            description = "Dompet berisi KTP dan KTM, kemungkinan jatuh di sekitar GKU.",
            location = "GKU UNILA",
            dateTime = "1 Maret 2026, 10:30",
            contact = "WA: 08xxxxxxxx",
            images = listOf(R.drawable.dompet1)
        ),

        LostItem(
            type = ReportType.LOST,
            itemName = "Kartu KTM",
            description = "KTM mahasiswa hilang di sekitar perpustakaan.",
            location = "Perpustakaan UNILA",
            dateTime = "1 Maret 2026, 13:10",
            contact = "IG: @username",
            images = listOf(R.drawable.ktm1)
        ),

        LostItem(
            type = ReportType.FOUND,
            itemName = "Kunci Motor",
            description = "Ditemukan kunci motor dengan gantungan merah.",
            location = "Parkiran FEB",
            dateTime = "1 Maret 2026, 14:00",
            contact = "IG: @lostfound_unila",
            images = listOf(R.drawable.kunci1)
        ),

        LostItem(
            type = ReportType.FOUND,
            itemName = "Botol Minum",
            description = "Botol minum biru ditemukan di ruang kelas.",
            location = "Gedung B FKIP",
            dateTime = "2 Maret 2026, 09:00",
            contact = "WA: 08xxxxxxxx",
            images = listOf(R.drawable.botol1)
        )
    )
}