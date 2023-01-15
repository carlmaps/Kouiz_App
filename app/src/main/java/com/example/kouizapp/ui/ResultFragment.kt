package com.example.kouizapp.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.kouizapp.R
import com.example.kouizapp.database.KouizDB
import com.example.kouizapp.databinding.FragmentQuestionsBinding
import com.example.kouizapp.databinding.FragmentResultBinding
import com.example.kouizapp.model.KouizModel
import kotlinx.android.synthetic.main.fragment_questions.*
import kotlinx.android.synthetic.main.fragment_result.*
import kotlinx.android.synthetic.main.fragment_result.view.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.streams.toList


class ResultFragment : BaseFragment() {

    private val sharedViewModel: KouizModel by activityViewModels()
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedViewModel.username.observe(viewLifecycleOwner){
            val resMsg = "Congratulation, $it You have completed the Kouiz!"
            binding.tvResultMsg.text = resMsg
        }

        launch { getMetrics(view) }

        view.btnTryAgain.setOnClickListener{
            sharedViewModel.resetQNumber()
            launch {
                context?.let {
                    KouizDB(it).questionsDao().clearUserAnsweOption()
                    val action = ResultFragmentDirections.resultFragmentToQuestionsFragment()
                    Navigation.findNavController(view).navigate(action)
                }
            }
        }

        view.btnAnalysis.setOnClickListener{
            val action = ResultFragmentDirections.resultFragmentToResultAnalysisFragment()
            Navigation.findNavController(view).navigate(action)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {}
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private suspend fun getMetrics(view: View){
        async {
            context?.let { it ->
                val questions = KouizDB(it).questionsDao().getAllQuestion()
                val total = questions.size
                val correctAnswers = questions.stream().map { x -> x.answer }.toList()
                val userAnswers = questions.stream().map { y -> y.user_answer }.toList()
                val mistake = correctAnswers.filterNot { userAnswers.contains(it) }.size

                //display
                val dispCorrect = getString(R.string.number_of_correct) + "${total - mistake}"
                val dispScoreRatio = getString(R.string.score) + "${total - mistake}" + "/" + "$total"
                val dispTotalQuestion = getString(R.string.total_question) + "$total"
                val dispMistake = getString(R.string.number_of_mistakes) + "$mistake"
                binding.tvCorrect.text = dispCorrect
                binding.tvTotalQuestion.text = dispTotalQuestion
                binding.tvScore.text = dispScoreRatio
                binding.tvMistake.text = dispMistake

            }
        }.await()
    }
}


