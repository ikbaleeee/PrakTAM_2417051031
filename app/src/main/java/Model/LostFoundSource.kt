package Model

import com.example.praktam_2417051031.R
import com.example.praktam_2417051031.ReportType

object LostFoundSource {

    val dummyReports = listOf(

        LostItem("1", ReportType.LOST, "Dompet Hitam",
            "Dompet berisi KTP dan KTM",
            "GKU UNILA", "1 Maret", "WA:08xx",
            listOf(R.drawable.dompet1)),

        LostItem("2", ReportType.LOST, "Kartu KTM",
            "KTM hilang di perpustakaan",
            "Perpustakaan", "1 Maret", "IG:@user",
            listOf(R.drawable.ktm1)),

        LostItem("3", ReportType.FOUND, "Kunci Motor",
            "Ditemukan kunci motor",
            "Parkiran FEB", "2 Maret", "IG:@lf",
            listOf(R.drawable.kunci1)),

        LostItem("4", ReportType.FOUND, "Botol Minum",
            "Botol ditemukan di kelas",
            "FKIP", "2 Maret", "WA:08xx",
            listOf(R.drawable.botol1))
    )
}