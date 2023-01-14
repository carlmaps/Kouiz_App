package com.example.kouizapp.database

import androidx.room.*

@Dao
interface QuestionDao {

    @Query("SELECT * FROM tbl_question WHERE id=:id")
    public abstract suspend fun getQuestionById(id: Long): Question?

    @Query("SELECT * FROM tbl_question ORDER BY id ASC")
    suspend fun getAllQuestion():List<Question>

    @Query("SELECT COUNT(*) FROM tbl_question")
    suspend fun getCount():Int

    @Query("UPDATE tbl_question SET user_answer=:user_answer, selected_optId=:selectedOpt WHERE id=:id")
    suspend fun updateUserAnswerOption(user_answer: String, selectedOpt: Int, id: Long)

    @Query("UPDATE tbl_question SET user_answer=null, selected_optId=null")
    suspend fun clearUserAnsweOption()

    @Insert
    suspend fun addQuestion(question:Question)

    @Insert
    suspend fun addMultipleNotes(vararg note: Question)

    @Update
    suspend fun updateQuestion(question:Question)

    @Delete
    suspend fun deleteQuestion(question: Question)
}