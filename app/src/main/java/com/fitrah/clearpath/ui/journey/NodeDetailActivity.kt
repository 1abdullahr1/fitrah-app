package com.fitrah.clearpath.ui.journey

import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.fitrah.clearpath.R
import com.fitrah.clearpath.data.model.JourneyNode
import com.fitrah.clearpath.data.repository.FitrahRepository
import com.fitrah.clearpath.databinding.ActivityNodeDetailBinding
import com.fitrah.clearpath.ui.glossary.GlossaryBottomSheetDialogFragment
import com.fitrah.clearpath.ui.glossary.GlossaryTextParser
import kotlinx.coroutines.launch

class NodeDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNodeDetailBinding
    private lateinit var repository: FitrahRepository
    private var currentNode: JourneyNode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNodeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = FitrahRepository(this)

        val nodeId = intent.getIntExtra(EXTRA_NODE_ID, 1)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.textBody.movementMethod = LinkMovementMethod.getInstance()
        binding.textBody.highlightColor = Color.TRANSPARENT

        loadNode(nodeId)

        binding.buttonCompleteAction.setOnClickListener {
            completeCurrentNode()
        }
    }

    private fun loadNode(nodeId: Int) {
        lifecycleScope.launch {
            val node = repository.getNodeById(nodeId)
            if (node == null) {
                finish()
                return@launch
            }
            currentNode = node
            bindNodeUi(node)
        }
    }

    private fun bindNodeUi(node: JourneyNode) {
        binding.toolbar.title = "Module ${node.moduleId}"

        binding.imageHeader.load(node.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.bg_chip_rounded)
            error(R.drawable.bg_chip_rounded)
        }

        binding.textStepTag.text = "Module ${node.moduleId} • Step ${node.stepOrder}"
        binding.textTitle.text = node.title
        binding.textSubtitle.text = node.subtitle
        binding.textSummary.text = "\"${node.summary}\""

        // Parse in-line glossary markup
        val parsedBody = GlossaryTextParser.parse(this, node.bodyText) { term, definition ->
            val dialog = GlossaryBottomSheetDialogFragment.newInstance(term, definition)
            dialog.show(supportFragmentManager, "GlossaryDialog")
        }
        binding.textBody.text = parsedBody

        binding.textKeyTakeaway.text = node.keyTakeaway
        binding.textReflectionPrompt.text = node.reflectionPrompt

        if (node.isCompleted) {
            binding.buttonCompleteAction.text = getString(R.string.action_unlock)
            binding.buttonCompleteAction.isEnabled = false
        } else {
            binding.buttonCompleteAction.text = node.actionButtonText
            binding.buttonCompleteAction.isEnabled = true
        }
    }

    private fun completeCurrentNode() {
        val node = currentNode ?: return
        lifecycleScope.launch {
            val nextId = if (node.id < 15) node.id + 1 else null
            repository.completeAndUnlock(node.id, nextId)
            Toast.makeText(this@NodeDetailActivity, "Step completed", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }
    }

    companion object {
        const val EXTRA_NODE_ID = "extra_node_id"
    }
}
