package Model

import android.content.Context

object LostFoundSource {

    fun getResourceId(context: Context, imageName: String): Int {
        return context.resources.getIdentifier(
            imageName,
            "drawable",
            context.packageName
        )
    }
}