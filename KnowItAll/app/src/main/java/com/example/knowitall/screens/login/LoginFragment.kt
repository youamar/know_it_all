package com.example.knowitall.screens.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.knowitall.R
import com.example.knowitall.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        viewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(requireContext())
        ).get(LoginViewModel::class.java)
        // Set the viewmodel for databinding - this allows the bound layout access
        // to all the data in the ViewModel
        binding.loginViewModel = viewModel
        // Specify the fragment view as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.emails.observe(viewLifecycleOwner) { emails ->
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, emails)
            binding.editTextTextEmailAddress.setAdapter(adapter)
        }
        viewModel.isValidEmail.observe(viewLifecycleOwner) { isValid ->
            if (isValid) {
                Toast.makeText(requireContext(), R.string.valid_email, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), R.string.invalid_email, Toast.LENGTH_SHORT).show()
            }
        }
        binding.loginButton.setOnClickListener {
            val email = binding.editTextTextEmailAddress.text.toString()
            viewModel.validateEmail(email)
        }
    }
}