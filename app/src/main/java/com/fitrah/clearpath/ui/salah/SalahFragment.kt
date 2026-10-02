package com.fitrah.clearpath.ui.salah

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.fitrah.clearpath.R
import com.fitrah.clearpath.data.model.SalahStep
import com.fitrah.clearpath.data.repository.FitrahRepository
import com.fitrah.clearpath.databinding.FragmentSalahBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SalahFragment : Fragment() {

    private var _binding: FragmentSalahBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: FitrahRepository
    private var steps: List<SalahStep> = emptyList()
    private var currentIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalahBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = FitrahRepository(requireContext())

        binding.buttonPrevStep.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                renderCurrentStep(animate = true)
            }
        }

        binding.buttonNextStep.setOnClickListener {
            if (currentIndex < steps.size - 1) {
                currentIndex++
                renderCurrentStep(animate = true)
            } else {
                // Loop back to start or finish
                currentIndex = 0
                renderCurrentStep(animate = true)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repository.allSalahSteps.collectLatest { list ->
                if (list.isNotEmpty()) {
                    steps = list
                    renderCurrentStep(animate = false)
                }
            }
        }
    }

    private fun renderCurrentStep(animate: Boolean) {
        if (steps.isEmpty()) return
        val step = steps[currentIndex]
        val total = steps.size
        val progress = ((currentIndex + 1) * 100) / total

        binding.textStepIndicator.text = getString(R.string.step_indicator_format, currentIndex + 1, total)
        binding.salahProgressIndicator.setProgressCompat(progress, animate)

        binding.buttonPrevStep.isEnabled = currentIndex > 0
        binding.buttonNextStep.text = if (currentIndex == total - 1) {
            getString(R.string.action_continue)
        } else {
            getString(R.string.action_next)
        }

        if (animate) {
            binding.cardPosture.animate()
                .alpha(0f)
                .setDuration(100)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction {
                    applyStepData(step)
                    binding.cardPosture.animate()
                        .alpha(1f)
                        .setDuration(150)
                        .start()
                }
                .start()
        } else {
            applyStepData(step)
        }
    }

    private fun applyStepData(step: SalahStep) {
        binding.textPostureName.text = step.postureName
        binding.textArabicName.text = step.arabicName
        binding.textPostureDescription.text = step.postureDescription
        binding.textArabicRecitation.text = step.arabicRecitation
        binding.textTransliteration.text = step.transliteration
        binding.textMeaning.text = step.englishMeaning
        binding.textGuidanceTip.text = step.guidanceTip
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = SalahFragment()
    }
}
