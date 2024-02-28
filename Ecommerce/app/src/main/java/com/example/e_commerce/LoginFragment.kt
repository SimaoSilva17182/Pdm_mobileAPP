package com.example.e_commerce

import Data.AppDatabase
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        /* testar se os valores estao a ser guardados na base de dados local
        GlobalScope.launch(Dispatchers.Main) {
            val db = AppDatabase.getDatabase(requireActivity())
            val purchaseDao = db.purchaseDao()
            val purchases = purchaseDao.getAllPurchases()

            purchases.forEach {
                Log.d("LoginFragment", "Purchase: $it")
            }
        }*/

        binding.btnLogin.setOnClickListener {
            val username = binding.Username.text.toString()
            val password = binding.Password.text.toString()

            if(username.isNotEmpty() && password.isNotEmpty()) {
                authenticateUser(username, password)
            } else {
                Toast.makeText(requireContext(), R.string.AskUser, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRegister.setOnClickListener {
            navigateToRegistration()
        }
    }

    /**
     * Function to navigate from login to registration
     */
    private fun navigateToRegistration() {
        findNavController().navigate(R.id.action_login_to_registration)
    }

    /**
     * Function that checks if the user is authenticated
     */
    private fun authenticateUser(username: String, password: String) {
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    navigateToMainPage()
                } else {
                    Toast.makeText(requireContext(), R.string.AuthenticationFailed, Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Function to navigate from login to main menu
     */
    private fun navigateToMainPage() {
        findNavController().navigate(R.id.action_login_to_mainMenu)
    }

    /**
     * Function for when user comes back to this fragment(like on backPress()) the data is removed
     */
    override fun onResume() {
        super.onResume()

        cleanData()
    }

    /**
     * Clear data
     */
    private fun cleanData() {
        binding.Username.text.clear()
        binding.Password.text.clear()
    }
}