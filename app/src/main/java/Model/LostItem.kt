package Model

import androidx.annotation.DrawableRes
import com.example.praktam_2417051031.ReportType

data class LostItem(
    val type: ReportType,
    val itemName: String,
    val description: String,
    val location: String,
    val dateTime: String,
    val contact: String,
    @DrawableRes val images: List<Int>
)