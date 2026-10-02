package com.fitrah.clearpath.ui.reflect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.fitrah.clearpath.R
import com.fitrah.clearpath.data.repository.FitrahRepository
import com.fitrah.clearpath.databinding.FragmentReflectBinding
import com.fitrah.clearpath.util.ThemeManager
import kotlinx.coroutines.launch

class ReflectFragment : Fragment() {

    private var _binding: FragmentReflectBinding? = null
    private val binding get() = _binding!!

    private lateinit var themeManager: ThemeManager
    private lateinit var repository: FitrahRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReflectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        themeManager = ThemeManager(requireContext())
        repository = FitrahRepository(requireContext())

        setupThemeSelector()
        setupResetButton()
    }

    private fun setupThemeSelector() {
        when (themeManager.themeMode) {
            AppCompatDelegate.MODE_NIGHT_NO -> binding.radioLight.isChecked = true
            AppCompatDelegate.MODE_NIGHT_YES -> binding.radioDark.isChecked = true
            else -> binding.radioSystem.isChecked = true
        }

        binding.radioGroupTheme.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.radioLight -> AppCompatDelegate.MODE_NIGHT_NO
                R.id.radioDark -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            themeManager.themeMode = mode
        }
    }

    private fun setupResetButton() {
        binding.buttonResetProgress.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.reset_progress)
                .setMessage(R.string.reset_confirm)
                .setPositiveButton(R.string.confirm) { _, _ ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        repository.resetAllProgress()
                        Toast.makeText(requireContext(), "Curriculum progress reset", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ReflectFragment()
    }
}
