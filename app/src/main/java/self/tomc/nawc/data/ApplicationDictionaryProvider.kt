package self.tomc.nawc.data

import android.content.Context
import androidx.compose.ui.text.toUpperCase
import self.tomc.nawc.R
import java.util.*

class ApplicationDictionaryProvider(private val context: Context) : DictionaryProvider {
    override fun getWordDictionary(): List<String> = context.resources.getStringArray(R.array.dictionary)
        .asList().map { it.toUpperCase(Locale.ROOT) }
}