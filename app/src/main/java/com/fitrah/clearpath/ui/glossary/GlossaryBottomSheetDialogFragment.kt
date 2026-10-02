package com.fitrah.clearpath.ui.glossary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.fitrah.clearpath.data.repository.FitrahRepository
import com.fitrah.clearpath.databinding.BottomSheetGlossaryBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class GlossaryBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetGlossaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetGlossaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val term = arguments?.getString(ARG_TERM) ?: ""
        val definition = arguments?.getString(ARG_DEFINITION) ?: ""

        binding.textGlossaryTerm.text = term
        binding.textDefinition.text = definition
        binding.textPlainEnglishBadge.text = term

        binding.buttonClose.setOnClickListener {
            dismiss()
        }

        // Query database for enriched term details if available
        viewLifecycleOwner.lifecycleScope.launch {
            val repository = FitrahRepository(requireContext())
            val entity = repository.getGlossaryTerm(term)
            if (entity != null) {
                binding.textArabicScript.text = entity.arabicScript
                binding.textPlainEnglishBadge.text = entity.plainEnglishTitle
                binding.textDefinition.text = entity.simpleDefinition
                binding.textDetailedContext.text = entity.detailedExplanation
                binding.textDetailedContext.visibility = View.VISIBLE
            } else {
                binding.textArabicScript.visibility = View.GONE
                binding.textDetailedContext.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TERM = "arg_term"
        private const val ARG_DEFINITION = "arg_definition"

        fun newInstance(term: String, definition: String): GlossaryBottomSheetDialogFragment {
            return GlossaryBottomSheetDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TERM, term)
                    putString(ARG_DEFINITION, definition)
                }
            }
        }
    }
}
