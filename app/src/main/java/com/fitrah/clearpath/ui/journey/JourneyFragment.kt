package com.fitrah.clearpath.ui.journey

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fitrah.clearpath.R
import com.fitrah.clearpath.data.model.JourneyNode
import com.fitrah.clearpath.data.repository.FitrahRepository
import com.fitrah.clearpath.databinding.FragmentJourneyBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class JourneyFragment : Fragment() {

    private var _binding: FragmentJourneyBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: FitrahRepository
    private lateinit var adapter: JourneyNodeAdapter
    private var allNodes: List<JourneyNode> = emptyList()
    private var selectedModuleId: Int = 0 // 0 means All

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJourneyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repository = FitrahRepository(requireContext())

        setupRecyclerView()
        setupModuleFilterChips()
        observeJourneyData()
    }

    private fun setupRecyclerView() {
        adapter = JourneyNodeAdapter { node ->
            val intent = Intent(requireContext(), NodeDetailActivity::class.java).apply {
                putExtra(NodeDetailActivity.EXTRA_NODE_ID, node.id)
            }
            startActivity(intent)
        }

        binding.recyclerViewNodes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewNodes.setHasFixedSize(true)
        binding.recyclerViewNodes.adapter = adapter
    }

    private fun setupModuleFilterChips() {
        binding.chipGroupModules.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
            selectedModuleId = when (checkedIds.first()) {
                R.id.chipMod1 -> 1
                R.id.chipMod2 -> 2
                R.id.chipMod3 -> 3
                R.id.chipMod4 -> 4
                R.id.chipMod5 -> 5
                else -> 0
            }
            filterNodes()
        }
    }

    private fun observeJourneyData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.checkAndSeedDatabase()
            repository.allJourneyNodes.collectLatest { nodes ->
                allNodes = nodes
                updateProgressHeader(nodes)
                filterNodes()
            }
        }
    }

    private fun filterNodes() {
        val filtered = if (selectedModuleId == 0) {
            allNodes
        } else {
            allNodes.filter { it.moduleId == selectedModuleId }
        }
        adapter.submitList(filtered)
    }

    private fun updateProgressHeader(nodes: List<JourneyNode>) {
        val total = nodes.size
        if (total == 0) return
        val completed = nodes.count { it.isCompleted }
        val percent = (completed * 100) / total

        binding.curriculumProgressIndicator.setProgressCompat(percent, true)
        binding.textProgressSummary.text = getString(R.string.nodes_completed_format, completed, total)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = JourneyFragment()
    }
}
