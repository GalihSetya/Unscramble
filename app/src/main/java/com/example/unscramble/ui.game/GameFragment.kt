/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.unscramble.ui.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.unscramble.R
import com.example.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragment where the game is played, contains the game logic.
 */
class GameFragment : Fragment() {


    //private var score = 0
    //private var currentWordCount = 0
   // private var currentScrambledWord = "test"
    private val viewModel: GameViewModel by viewModels()

    // Binding object instance with access to the views in the game_fragment.xml layout
    private lateinit var binding: GameFragmentBinding

    // Create a ViewModel the first time the fragment is created.
    // If the fragment is re-created, it receives the same GameViewModel instance created by the
    // first fragment

    //setelah mendapatkan referensi ke objek binding, tambahkan laporan log untuk mencatat pembuatan fragmen. Callback onCreateView() akan dipicu saat fragmen dibuat untuk pertama kalinya dan juga setiap kali fragmen dibuat ulang untuk peristiwa seperti perubahan konfigurasi.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        Log.d("GameFragment", "GameFragment created/re-created!")
        Log.d("GameFragment", "Word: ${viewModel.currentScrambledWord} " +
                "Score: ${viewModel.score} WordCount: ${viewModel.currentWordCount}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // inisialisasi variabel tata letak gameViewModel dan maxNoOfWords.
        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS

        // Setup a click listener for the Submit and Skip buttons.
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }
        binding.wordCount.text = getString(

            R.string.word_count, 0, MAX_NO_OF_WORDS
        )
        binding.lifecycleOwner = viewLifecycleOwner

        // Lampirkan observer untuk currentScrambledWord LiveData. Pada GameFragment di akhir callback onViewCreated(), panggil metode observe() pada currentScrambledWord.
       //Teruskan viewLifecycleOwner sebagai parameter pertama ke metode observe(). viewLifecycleOwner merepresentasikan siklus proses Tampilan Fragment. Parameter ini membantu LiveData mengetahui siklus proses GameFragment dan memberi tahu observer hanya jika GameFragment dalam status aktif (STARTED atau RESUMED).
        //Tambahkan lambda sebagai parameter kedua dengan newWord sebagai parameter fungsi. newWord akan berisi nilai kata acak yang baru.
       // viewModel.score.observe(viewLifecycleOwner,
         //   { newScore ->
         //       binding.score.text = getString(R.string.score, newScore)
       //     })
      //  viewModel.currentWordCount.observe(viewLifecycleOwner,
       //     { newWordCount ->
       //         binding.wordCount.text =
      //              getString(R.string.word_count, newWordCount, MAX_NO_OF_WORDS)
       //     })

    }

    /*
   * Checks the user's word, and updates the score accordingly.
   * Displays the next scrambled word.
   */
    private fun onSubmitWord() {
        //  currentScrambledWord = getNextScrambledWord()
        //  currentWordCount++
        // score += SCORE_INCREASE
        //  binding.wordCount.text = getString(R.string.word_count, currentWordCount, MAX_NO_OF_WORDS)
        //  binding.score.text = getString(R.string.score, score)
        //  setErrorTextField(false)
        //   updateNextWordOnScreen()

        //buat val yang bernama playerWord. Simpan kata pemain di dalamnya, dengan mengekstraknya dari kolom teks dalam variabel binding.
        val playerWord = binding.textInputEditText.text.toString()
        //validasikan kata pemain. Tambahkan pernyataan if untuk memeriksa kata pemain menggunakan metode isUserWordCorrect(), dengan meneruskan playerWord.
        //Di dalam blok if, reset kolom teks, panggil setErrorTextField dengan memasukkan false.

        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if (!viewModel.nextWord()) {
                showFinalScoreDialog()
            }
            //Tambahkan blok else ke blok if di atas, lalu panggil setErrorTextField() yang meneruskan true.
        } else {
            setErrorTextField(true)
        }

    }

    /*
     * Skips the current word without changing the score.
     * Increases the word count.
     */
    private fun onSkipWord() {
        //currentScrambledWord = getNextScrambledWord()
        // currentWordCount++
        // binding.wordCount.text = getString(R.string.word_count, currentWordCount, MAX_NO_OF_WORDS)
        //setErrorTextField(false)
        // updateNextWordOnScreen()
        if (viewModel.nextWord()) {
            setErrorTextField(false)
        } else {
            showFinalScoreDialog()
        }
    }


    //Untuk membuat MaterialAlertDialog, gunakan class MaterialAlertDialogBuilder untuk membuat bagian dialog langkah demi langkah. Panggil konstruktor MaterialAlertDialogBuilder yang meneruskan konten menggunakan metode requireContext() fragmen. Metode requireContext() menampilkan nilai non-null Context.
    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored, viewModel.score.value))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                exitGame()
            }
            .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                restartGame()
            }
            .show()
    }

    /*
     * Gets a random word for the list of words and shuffles the letters in it.
     */


    /*
     * Re-initializes the data in the ViewModel and updates the views with the new data, to
     * restart the game.
     */
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }

    /*
     * Exits the game.
     */
    private fun exitGame() {
        activity?.finish()
    }

    //ganti metode callback onDetach(), yang akan dipanggil saat aktivitas dan fragmen yang terkait dihancurkan
   // override fun onDetach() {
     //   super.onDetach()
       // Log.d("GameFragment", "GameFragment destroyed!")
   // }

    /*
    * Sets and resets the text field error status.
    */
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }

}
