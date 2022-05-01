package self.tomc.nawc.data

import android.content.Context
import self.tomc.nawc.R

class ApplicationDictionaryProvider(private val context: Context) : DictionaryProvider {
    override fun getWordDictionary(): List<String> = context.resources.getStringArray(R.array.dictionary).asList()
}