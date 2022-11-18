package com.example.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

//Ubah GameViewModel menjadi subclass dari ViewModel. ViewModel adalah class abstrak, jadi perlu memperluasnya untuk menggunakannya di aplikasi
class GameViewModel : ViewModel() {

    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    //menjadi val karena nilai objek LiveData/MutableLiveData akan tetap sama, dan hanya data yang disimpan dalam objek yang akan berubah.
    private val _currentScrambledWord = MutableLiveData<String>()
    //Menambahkan properti pendukung. _currentScrambledWord hanya dapat diakses dan diedit dalam GameViewModel. Pengontrol UI, GameFragment, dapat membaca nilainya menggunakan properti hanya baca, currentScrambledWord.
    //
   // val currentScrambledWord: LiveData<String>
     //   get() = _currentScrambledWord

    //kode berikut untuk mengubah cara variabel currentScrambledWord dideklarasikan
    //Variabel ini sekarang menjadi LiveData<Spannable> bukan LiveData<String>. Anda tidak perlu khawatir dengan memahami semua detail tentang cara kerjanya, namun penerapannya menggunakan transformasi LiveData untuk mengonversi kata acak saat ini String menjadi string Spannable yang dapat ditangani dengan tepat oleh layanan aksesibilitas.
    val currentScrambledWord: LiveData<Spannable> = Transformations.map(_currentScrambledWord) {
        if (it == null) {
            SpannableString("")
        } else {
            val scrambledWord = it.toString()
            val spannable: Spannable = SpannableString(scrambledWord)
            spannable.setSpan(
                TtsSpan.VerbatimBuilder(scrambledWord).build(),
                0,
                scrambledWord.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            spannable
        }
    }
    //MutableList<String> yang disebut wordsList, untuk menyimpan daftar kata yang Anda gunakan dalam game, untuk menghindari pengulangan.
    private var wordsList: MutableList<String> = mutableListOf()
    //currentWord untuk menyimpan kata yang ingin disusun oleh pemain. Gunakan kata kunci lateinit karena Anda akan menginisialisasi properti ini nanti.
    private lateinit var currentWord: String

    init {
        Log.d("GameFragment", "GameViewModel created!")
        getNextWord()
    }

    //ganti metode onCleared(). ViewModel akan dihancurkan saat fragmen yang terkait dilepas, atau saat aktivitas selesai. Tepat sebelum ViewModel dihancurkan, callback onCleared() dipanggil.
    //Tambahkan laporan log di dalam onCleared() untuk melacak siklus proses GameViewModel.
    override fun onCleared() {
        super.onCleared()
        Log.d("GameFragment", "GameViewModel destroyed!")
    }

    //Untuk mereset data aplikasi, di GameViewModel, tambahkan metode yang disebut reinitializeData(). Tetapkan skor dan jumlah kata menjadi 0. Hapus daftar kata dan panggil metode getNextWord().
    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }

    //tambahkan metode bantuan yang disebut isUserWordCorrect() yang menampilkan Boolean dan mengambil String, kata dari pemain, sebagai parameter.
    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    private fun getNextWord() {
        //Dapatkan kata acak dari allWordsList dan tetapkan ke currentWord.
        currentWord = allWordsList.random()

        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }
        if (wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            //Untuk mengakses data dalam objek LiveData, gunakan properti value
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = (_currentWordCount.value)?.inc()
            wordsList.add(currentWord)
        }
    }

    //tambahkan metode pribadi baru yang disebut increaseScore() tanpa parameter dan nilai return. Tingkatkan variabel score sebesar SCORE_INCREASE.
    //ubah referensi _score dan _currentWordCount menjadi _score.value dan _currentWordCount.value. Android Studio akan menampilkan error karena _score tidak lagi berupa bilangan bulat, namun LiveData
    //fungsi Kotlin plus() untuk meningkatkan nilai _score, yang menjalankan penambahan dengan keamanan null.
    private fun increaseScore() {
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    //Dapatkan kata berikutnya dari daftar dan tampilkan true jika jumlah kata kurang dari MAX_NO_OF_WORDS.
    fun nextWord(): Boolean {
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }

}