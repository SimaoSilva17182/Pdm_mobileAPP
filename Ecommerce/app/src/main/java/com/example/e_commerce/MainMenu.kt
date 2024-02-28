package com.example.e_commerce

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.e_commerce.databinding.FragmentMainMenuBinding

class MainMenu : Fragment() {

    private lateinit var binding: FragmentMainMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCarrinho.setOnClickListener {
            navigateToCarrinho()
        }

        binding.btnCompras.setOnClickListener {
            navigateToCompras()
        }

        binding.btnLogout.setOnClickListener {
            navigateToLogout()
        }
    }

    /**
     * function to navigate from main menu to login
     */
    private fun navigateToLogout() {
        findNavController().navigate(R.id.action_mainMenu_to_login)
    }

    /**
     * function to navigate from main menu to addCart
     */
    private fun navigateToCompras() {
        findNavController().navigate(R.id.action_mainMenu_to_addToCart)
    }

    /**
     * function to navigate from main menu to cart
     */
    private fun navigateToCarrinho() {
        findNavController().navigate(R.id.action_mainMenu_to_cart)
    }
}