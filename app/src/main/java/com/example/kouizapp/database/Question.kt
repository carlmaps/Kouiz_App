package com.example.kouizapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "tbl_question")
data class Question(

    @PrimaryKey(autoGenerate = true)
    var id:Long,

    @ColumnInfo(name = "question")
    val question: String,

    @ColumnInfo(name = "optA")
    val optA: String,

    @ColumnInfo(name = "optB")
    val optB: String,

    @ColumnInfo(name = "optC")
    val optC: String,

    @ColumnInfo(name = "optD")
    val optD: String,

    @ColumnInfo(name = "answer")
    val answer: String,

    @ColumnInfo(name = "user_answer")
    val user_answer: String?,

    @ColumnInfo(name = "selected_optId")
    val selected_optId: Int?) : Serializable




