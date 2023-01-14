package com.example.kouizapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.kouizapp.R
import com.example.kouizapp.database.KouizDB
import com.example.kouizapp.databinding.FragmentHomeBinding
import com.example.kouizapp.databinding.FragmentQuestionsBinding
import com.example.kouizapp.model.KouizModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment() {
    private val sharedViewModel: KouizModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        binding.btnStart.setOnClickListener{
            var username = binding.eTxtName.text.toString()
            sharedViewModel.setUserName(binding.eTxtName.text.toString())
            if(username.isEmpty()) {
                Snackbar.make(it, "Name is empty. Please input your name to start", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
            else{
                sharedViewModel.setUserName(username)
                launch {
                    context?.let {
                        KouizDB(it).questionsDao().clearUserAnsweOption()
                        val action = HomeFragmentDirections.homeFragmentToQuestionsFragment()
                        Navigation.findNavController(binding.root).navigate(action)
                    }
                }
            }
        }
        return binding.root
    }
}