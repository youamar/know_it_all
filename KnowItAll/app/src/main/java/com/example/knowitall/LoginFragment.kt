package com.example.knowitall

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.knowitall.databinding.FragmentLoginBinding

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(inflater,
            R.layout.fragment_login, container, false)

        binding.loginButton.setOnClickListener { view: View ->
            val email = binding.editTextTextEmailAddress.text.toString()
            if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(context, "Valid address !", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Invalid address !", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

}