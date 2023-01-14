package com.example.kouizapp.ui
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.OnBackPressedCallback
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
import kotlinx.coroutines.launch

class QuestionsFragment : BaseFragment() {

    private val sharedViewModel: KouizModel by activityViewModels()
    private var _binding: FragmentQuestionsBinding? = null
    private val binding get() = _binding!!

    lateinit var selectedRadioButton: RadioButton
    lateinit var question: Question
    var qCount: Long = 0
    var isPrevBtnDisabled: Boolean = true
    var score: Int = 0
    var selOptId: Int = 0

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

        sharedViewModel.qNumber.observe(viewLifecycleOwner) {
           qCount = it
        }

        sharedViewModel.score.observe(viewLifecycleOwner) {
            score = it
        }

        sharedViewModel.option.observe(viewLifecycleOwner){
            selOptId = it
        }

        disableNext(view)
        enableDiablePrevBtn(view)

        view.btnNxt.setOnClickListener{
            sharedViewModel.incrementQNumber()
            loadQuestion(view)

            if(isPrevBtnDisabled){
                enableDiablePrevBtn(view)
            }
            if(qCount > 15){
                launch {
                    context?.let {
                        val total = KouizDB(it).questionsDao().getCount()
                        sharedViewModel.setTotalQuestion(total)
                        val action = QuestionsFragmentDirections.questionsFragmentToResultFragment()
                        Navigation.findNavController(view).navigate(action)
                    }
                }
            }
            getAnswer(view)
        }

        view.btnHome.setOnClickListener{
            sharedViewModel.resetQNumberAndScore()
            sharedViewModel.setUserName("")
            val action = QuestionsFragmentDirections.questionsFragmentToHomeFragment()
            Navigation.findNavController(view).navigate(action)
        }

        view.btnPrev.setOnClickListener{
            sharedViewModel.decrementQNumber()
            sharedViewModel.decrementScore()
            loadQuestion(view)
            if(qCount <= 1L){
                enableDiablePrevBtn(view)
            }
        }

        var radioButton = view.findViewById<RadioGroup>(R.id.rgChoices)
        radioButton.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioOptA, R.id.radioOptB, R.id.radioOptC, R.id.radioOptD -> {
                    enableNext(view)
                    //selOptId = checkedId
                    sharedViewModel.saveOption(checkedId)
                }
            }
        }

        //Loading of Questions
        loadQuestion(view)
        return view
    }

    private fun getAnswer(view: View){
        val selectedRadioButtonId: Int = rgChoices.checkedRadioButtonId
        if(selectedRadioButtonId != -1){
            selectedRadioButton = view.findViewById<RadioButton>(selectedRadioButtonId)
            val strAnswer: String = selectedRadioButton.text.toString()
            if(strAnswer.equals(question.answer)){
                sharedViewModel.incrementScore()
            }

            //Update
            launch {
                context?.let {
                    val questionFromTable =
                        KouizDB(it).questionsDao().updateUserAnswerOption(strAnswer, selOptId, question.id)
                }
            }
        }
        else{
            Snackbar.make(view, "Please select an answer before proceeding", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun enableDiablePrevBtn(view: View){
        if(qCount <= 1L){
            view.btnPrev.isEnabled = false
            view.btnPrev.setBackgroundColor(resources.getColor(R.color.white))
            view.btnPrev.setTextColor(resources.getColor(R.color.black))
            isPrevBtnDisabled = true
        }
        else{
            view.btnPrev.isEnabled = true
            view.btnPrev.setBackgroundColor(resources.getColor(R.color.purple_200))
            view.btnPrev.setTextColor(resources.getColor(R.color.white))
            isPrevBtnDisabled = false
        }
    }

    private fun loadQuestion(view: View){
        launch {
            context?.let {
                val questionFromTable = KouizDB(it).questionsDao().getQuestionById(qCount)
                if (questionFromTable != null) {
                    view.rgChoices.clearCheck()
                    view.tvQuestion.text = questionFromTable.question
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