package com.example.kouizapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import com.example.kouizapp.R
import com.example.kouizapp.database.Question
import kotlinx.android.synthetic.main.result_analysis.view.*

class ResultAnalysisAdapter(private val context: Context, private val dataSource: ArrayList<Question>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.result_analysis, parent, false)

        val question = getItem(position) as Question
        val qNumber = question.id.toString()
        val qDesc = question.question
        val ans = question.answer
        val usrAnswer = question.user_answer
        val dispAnswer = rowView.tvRAAnswer.text.toString() + " " + ans
        val dispUserAns = rowView.tvRAUserAnswer.text.toString() + " " + usrAnswer

        val displayQuestion: String = "$qNumber. $qDesc"

        rowView.tvRAQuestion.text = displayQuestion
        rowView.tvRAAnswer.text = dispAnswer
        rowView.tvRAUserAnswer.text= dispUserAns

        if(ans == usrAnswer){
            //set textColor and backgound to make it more visible
            rowView.tvRAUserAnswer.setBackgroundColor(context.resources.getColor(R.color.correct))
            rowView.tvRAUserAnswer.setTextColor(context.resources.getColor(R.color.incorrect))
        }
        else{
            rowView.tvRAUserAnswer.setBackgroundColor(context.resources.getColor(R.color.incorrect))
            rowView.tvRAUserAnswer.setTextColor(context.resources.getColor(R.color.incorrect))
        }

        return rowView
    }
}

