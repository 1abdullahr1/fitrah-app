package com.fitrah.clearpath.ui.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fitrah.clearpath.R
import com.fitrah.clearpath.data.model.FaqItem
import com.fitrah.clearpath.data.repository.FitrahRepository
import com.fitrah.clearpath.databinding.FragmentGuideBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GuideFragment : Fragment() {

    private var _binding: FragmentGuideBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: FitrahRepository
    private lateinit var adapter: FaqAdapter

    private var allFaqs: List<FaqItem> = emptyList()
    private var selectedCategory: String = ""
    private var searchQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGuideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = FitrahRepository(requireContext())

        setupRecyclerView()
        setupSearchAndFilters()
        observeFaqData()
    }

    private fun setupRecyclerView() {
        adapter = FaqAdapter()
        binding.recyclerViewFaq.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFaq.setHasFixedSize(true)
        binding.recyclerViewFaq.adapter = adapter
    }

    private fun setupSearchAndFilters() {
        binding.editSearch.doAfterTextChanged { text ->
            searchQuery = text?.toString()?.trim() ?: ""
            filterFaqs()
        }

        binding.chipGroupCategories.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            selectedCategory = when (checkedIds.first()) {
                R.id.chipWork -> "Workplace"
                R.id.chipSocial -> "Social & Family"
                R.id.chipPrayer -> "Prayer Practicalities"
                R.id.chipSects -> "Sects & Clarity"
                R.id.chipDoubts -> "Addressing Doubts"
                else -> ""
            }
            filterFaqs()
        }
    }

    private fun observeFaqData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.allFaqItems.collectLatest { list ->
                allFaqs = list
                filterFaqs()
            }
        }
    }

    private fun filterFaqs() {
        val filtered = allFaqs.filter { item ->
            val matchesCategory = selectedCategory.isEmpty() || item.category == selectedCategory
            val matchesSearch = searchQuery.isEmpty() ||
                    item.question.contains(searchQuery, ignoreCase = true) ||
                    item.conciseAnswer.contains(searchQuery, ignoreCase = true) ||
                    item.detailedAdvice.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }

        adapter.submitList(filtered)
        binding.textEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = GuideFragment()
    }
}
