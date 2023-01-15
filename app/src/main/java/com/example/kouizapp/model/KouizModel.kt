package com.example.kouizapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class KouizModel : ViewModel(){

    private val _qNumber = MutableLiveData<Long>(1)
    val qNumber: LiveData<Long> = _qNumber

    private val _username = MutableLiveData<String>("")
    val username: LiveData<String> = _username

    private val _option = MutableLiveData<Int>(0)
    val option: LiveData<Int> = _option

    fun incrementQNumber(){
        _qNumber.value = (_qNumber.value)?.plus(1)
    }

    fun decrementQNumber(){
        _qNumber.value = (_qNumber.value)?.minus(1)
    }

    fun setUserName(usernm: String){
        _username.value = usernm
    }

    fun resetQNumber(){
        _qNumber.value = 1
    }

    fun saveOption(opt: Int){
        _option.value = opt
    }
}