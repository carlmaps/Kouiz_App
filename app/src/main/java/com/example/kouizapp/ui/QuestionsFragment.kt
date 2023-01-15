package com.example.kouizapp.ui
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.kouizapp.R
import com.example.kouizapp.database.KouizDB
import com.example.kouizapp.database.Question
import com.example.kouizapp.databinding.FragmentQuestionsBinding
import com.example.kouizapp.model.KouizModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_questions.*
import kotlinx.android.synthetic.main.fragment_questions.view.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Collections.min
import kotlin.math.min
import kotlin.streams.toList

class QuestionsFragment : BaseFragment() {

    private val sharedViewModel: KouizModel by activityViewModels()
    private var _binding: FragmentQuestionsBinding? = null
    private val binding get() = _binding!!

    lateinit var selectedRadioButton: RadioButton
    lateinit var question: Question
    var qCount: Long = 0
    var selOptId: Int = 0

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentQuestionsBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedViewModel.username.observe(viewLifecycleOwner) {
            val msg = "Welcome to Kouiz, $it. Goodluck!"
            binding.tvMsg.text = msg
        }

        sharedViewModel.qNumber.observe(viewLifecycleOwner) { it ->
            qCount = it
            if(qCount <= 1L){
                view.btnPrev.isEnabled = false
                view.btnPrev.setBackgroundColor(resources.getColor(R.color.white))
                view.btnPrev.setTextColor(resources.getColor(R.color.black))
            }
            else if(qCount > 15){
                launch {
                    context?.let { it ->
                        val action = QuestionsFragmentDirections.questionsFragmentToResultFragment()
                        Navigation.findNavController(view).navigate(action)
                    }
                }
            }
            else{
                view.btnPrev.isEnabled = true
                view.btnPrev.setBackgroundColor(resources.getColor(R.color.purple_200))
                view.btnPrev.setTextColor(resources.getColor(R.color.white))
            }
        }

        sharedViewModel.option.observe(viewLifecycleOwner){
            selOptId = it
        }

        disableNext(view)

        view.btnNxt.setOnClickListener{
            launch{
                getAnswer(view)
            }
            sharedViewModel.incrementQNumber()
            loadQuestion(view)
        }

        view.btnHome.setOnClickListener{
            sharedViewModel.resetQNumber()
            sharedViewModel.setUserName("")
            val action = QuestionsFragmentDirections.questionsFragmentToHomeFragment()
            Navigation.findNavController(view).navigate(action)
        }

        view.btnPrev.setOnClickListener{
            sharedViewModel.decrementQNumber()
            loadQuestion(view)
        }

        var radioButton = view.findViewById<RadioGroup>(R.id.rgChoices)
        radioButton.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioOptA, R.id.radioOptB, R.id.radioOptC, R.id.radioOptD -> {
                    enableNext(view)
                    val rd = view.findViewById(checkedId) as RadioButton
                    if(rd.isChecked) {
                        sharedViewModel.saveOption(checkedId)
                    }
                }
            }
        }

        //Loading of Questions
        loadQuestion(view)
        return view
    }

    private suspend fun getAnswer(view: View){
        val selectedRadioButtonId: Int = rgChoices.checkedRadioButtonId
        if(selectedRadioButtonId != -1){
            selectedRadioButton = view.findViewById<RadioButton>(selectedRadioButtonId)
            val strAnswer: String = selectedRadioButton.text.toString()

            //Update
            async {
                context?.let {
                    val questionFromTable =
                        KouizDB(it).questionsDao().updateUserAnswerOption(strAnswer, selOptId, question.id)
                }
            }.await()
        }
        else{
            Snackbar.make(view, "Please select an answer before proceeding", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun loadQuestion(view: View){
        launch {
            context?.let {
                val questionFromTable = KouizDB(it).questionsDao().getQuestionById(qCount)
                if (questionFromTable != null) {
                    view.rgChoices.clearCheck()
                    val qNumber = questionFromTable.id.toString()
                    val qDesc = questionFromTable.question
                    val displayQuestion: String = "$qNumber. $qDesc"
                    view.tvQuestion.text = displayQuestion
                    view.radioOptA.text = questionFromTable.optA
                    view.radioOptB.text = questionFromTable.optB
                    view.radioOptC.text = questionFromTable.optC
                    view.radioOptD.text = questionFromTable.optD
                    question = questionFromTable
                    disableNext(view)

                    when(questionFromTable.selected_optId){
                        R.id.radioOptA -> {
                            view.radioOptA.isChecked = true
                        }
                        R.id.radioOptB -> {
                            view.radioOptB.isChecked = true
                        }
                        R.id.radioOptC -> {
                            view.radioOptC.isChecked = true
                        }
                        R.id.radioOptD -> {
                            view.radioOptD.isChecked = true
                        }
                    }
                }
            }
        }
    }

    private fun enableNext(view: View){
        view.btnNxt.isEnabled = true
        view.btnNxt.setBackgroundColor(resources.getColor(R.color.purple_200))
        view.btnNxt.setTextColor(resources.getColor(R.color.white))
    }

    private fun disableNext(view: View){
        view.btnNxt.isEnabled = false
        view.btnNxt.setBackgroundColor(resources.getColor(R.color.white))
        view.btnNxt.setTextColor(resources.getColor(R.color.black))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {}
        })
    }
}