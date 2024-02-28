package com.example.e_commerce

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        val btnCancel: Button = binding.btnCancelRegister
        val btnConfirm: Button = binding.btnConfirm

        btnConfirm.setOnClickListener {

            val name = binding.Name.text.toString()
            val email = binding.Email.text.toString()
            val password = binding.PasswordRegister.text.toString()
            val confirmPassword = binding.ConfirmPassword.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password.length >= 6) {
                    if (isEmailValid(email)) {
                        if (password == confirmPassword) {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(requireActivity()) { task ->
                                    if (task.isSuccessful) {
                                        //In case of success show a AlertDialog to inform the user that the register its done
                                        showSuccessDialog()
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            findNavController().navigate(R.id.action_register_to_login)
                                        }, 6000000)
                                    } else {
                                        //Failed cases
                                        val errorMessage = task.exception?.message ?: getString(R.string.ErrorReg)

                                        when {
                                            errorMessage.contains(getString(R.string.Error_Email)) ->
                                                showToast(getString(R.string.Error_Email_Justi))

                                            errorMessage.contains(getString(R.string.Error_Pass)) ->
                                                showToast(getString(R.string.Error_Pass_Justi))

                                            errorMessage.contains(getString(R.string.Error_Invalid_Email)) ->
                                                showToast(getString(R.string.Error_Invalid_Email_Justi))

                                            else  -> showToast(errorMessage)
                                        }
                                    }
                                }
                        } else {
                            showToast(getString(R.string.Error_PassMatch))
                        }
                    } else {
                        showToast(getString(R.string.Error_EmailFormat))
                    }
                } else {
                    showToast(getString(R.string.Error_Caracters))
                }
            } else {
                showToast(getString(R.string.Error_EmptyFields))
            }
        }

        btnCancel.setOnClickListener {
            cleanData()
            navigateToSelf()
        }
    }

    /**
     * Function to navigate from register to himself this is use for cancel button
     */
    private fun navigateToSelf() {
        findNavController().navigate(R.id.action_toSelf)
    }

    /**
     * Function to validate email format
     */
    private fun isEmailValid(email: String): Boolean {
        //https://pt.stackoverflow.com/questions/1386/express%C3%A3o-regular-para-valida%C3%A7%C3%A3o-de-e-mail
        val emailRegex = Regex("^[a-z0-9.]+@[a-z0-9]+\\.[a-z]+(?:\\.[a-z]+)?\$")
        return emailRegex.matches(email)
    }

    /**
     * Function to create messages used in the failed cases
     */
    private fun showToast(message: String) {

        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Function to create AlertDialog
     */
    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.RegistrationSuccess)
        builder.setMessage(R.string.RegistrationSuccess)

        builder.setPositiveButton(R.string.Ok) {_, _ ->
            findNavController().navigate(R.id.action_register_to_login)
        }

        val dialog = builder.create()
        dialog.show()
    }

    /**
     * Clear data
     */
    private fun cleanData() {
        binding.Name.text.clear()
        binding.Email.text.clear()
        binding.PasswordRegister.text.clear()
        binding.ConfirmPassword.text.clear()
    }

    /**
     * Function for when user comes back to this fragment(like on backPress()) the data is removed
     */
    override fun onResume() {
        super.onResume()

        cleanData()
    }
}