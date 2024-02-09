package com.example.myapplication

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate

import com.example.myapplication.R

import com.example.myapplication.databinding.FragmentConfiUsuarioBinding

class UserConfi : Fragment() {

    private var _binding: FragmentConfiUsuarioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfiUsuarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener el modo actual desde SharedPreferences
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val currentTheme = sharedPref?.getInt("current_theme", AppCompatDelegate.MODE_NIGHT_NO)

        // Aplicar el tema actual
        AppCompatDelegate.setDefaultNightMode(currentTheme ?: AppCompatDelegate.MODE_NIGHT_NO)

        // Configurar el Switch y la ImageView
        val switchDayNight = binding.switchDayNight
        val imageView = binding.icConfiSwitch

        // Establecer el estado inicial del Switch y la imagen
        switchDayNight.isChecked = (currentTheme == AppCompatDelegate.MODE_NIGHT_YES)
        updateImage(switchDayNight.isChecked, imageView)

        // Manejar cambios en el Switch
        switchDayNight.setOnCheckedChangeListener { _, isChecked ->
            // Cambiar el modo diurno/nocturno
            if (isChecked) {
                // Modo nocturno
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // Modo diurno
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            // Guardar el estado actual en SharedPreferences
            with(sharedPref?.edit()) {
                this?.putInt("current_theme", AppCompatDelegate.getDefaultNightMode())
                this?.apply()
            }

            // Actualizar la imagen según el estado del Switch
            updateImage(isChecked, imageView)
        }
    }

    private fun updateImage(isNightMode: Boolean, imageView: ImageView) {
        val imageResource = if (isNightMode) R.drawable.ic_moon else R.drawable.ic_sum
        imageView.setImageResource(imageResource)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}