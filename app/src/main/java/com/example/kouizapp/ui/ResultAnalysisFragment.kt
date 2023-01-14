package com.example.kouizapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.kouizapp.R
import com.example.kouizapp.database.KouizDB
import com.example.kouizapp.database.Question
import com.example.kouizapp.model.KouizModel
import kotlinx.android.synthetic.main.fragment_questions.*
import kotlinx.android.synthetic.main.fragment_result_analysis.view.*
import kotlinx.coroutines.launch


class ResultAnalysisFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_result_analysis, container, false)

        launch {
            context?.let {
                val questions = KouizDB(it).questionsDao().getAllQuestion()
                val arrQuestions = ArrayList(questions)
                val adapter = context?.let { ResultAnalysisAdapter(it, arrQuestions ) }
                view.lvAnalysis.adapter = adapter
            }
        }

        return view
    }
}