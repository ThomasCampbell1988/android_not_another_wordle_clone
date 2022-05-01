package self.tomc.nawc.data

interface DictionaryProvider {
    fun getWordDictionary(): List<String>
}