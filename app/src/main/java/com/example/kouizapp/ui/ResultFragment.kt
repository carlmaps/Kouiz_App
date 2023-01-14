package com.example.kouizapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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
import kotlinx.coroutines.launch


class ResultFragment : BaseFragment() {

    private val sharedViewModel: KouizModel by activityViewModels()
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    var totalQuestion: Int = 0
    var correctAns: Int = 0
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

        sharedViewModel.score.observe(viewLifecycleOwner){
            val dispScore = binding.tvCorrect.text.toString() + it
            correctAns = it
            binding.tvCorrect.text = dispScore
        }


        sharedViewModel.totalQuestion.observe(viewLifecycleOwner){
            totalQuestion = it
            val dispScoreRatio = binding.tvScore.text.toString() + correctAns + "/" +it
            val dispTotalQuestion = binding.tvTotalQuestion.text.toString() + it
            val dispMistake = binding.tvMistake.text.toString() + "${totalQuestion - correctAns}"

            binding.tvTotalQuestion.text = dispTotalQuestion
            binding.tvScore.text = dispScoreRatio
            binding.tvMistake.text = dispMistake
        }

        view.btnTryAgain.setOnClickListener{
            sharedViewModel.resetQNumberAndScore()
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

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        val correct = arguments?.getInt("score")
//        val total = arguments?.getInt("total")
//        val mistake = (total?:0) - (correct?: 0)
//        (tvCorrect.text.toString() + correct.toString()).also { tvCorrect.text = it }
//        (tvMistake.text.toString() + mistake.toString()).also { tvMistake.text = it }
//        (tvTotalQuestion.text.toString() + total.toString()).also { tvTotalQuestion.text = it }
//        (tvScore.text.toString() + correct.toString() + "/" + total.toString()).also { tvScore.text = it }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {}
        })
    }
}


