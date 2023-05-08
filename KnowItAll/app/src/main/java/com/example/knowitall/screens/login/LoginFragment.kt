package com.example.knowitall.screens.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.knowitall.R
import com.example.knowitall.databinding.FragmentLoginBinding

//FIXME (QHB) :remove those default comments
/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        viewModel = ViewModelProvider(this, LoginViewModelFactory(requireContext())).get(LoginViewModel::class.java)
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
        viewModel.getEmails()
        viewModel.emails.observe(viewLifecycleOwner) { emails ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, emails)
            binding.editTextTextEmailAddress.setAdapter(adapter)
        }
    }
}