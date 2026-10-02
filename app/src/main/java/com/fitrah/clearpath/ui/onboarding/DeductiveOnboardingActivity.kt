package com.fitrah.clearpath.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.fitrah.clearpath.MainActivity
import com.fitrah.clearpath.R
import com.fitrah.clearpath.databinding.ActivityOnboardingBinding
import com.fitrah.clearpath.util.OnboardingManager

class DeductiveOnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingManager: OnboardingManager
    private var currentStep = 0

    private data class Premise(
        val titleRes: Int,
        val textRes: Int,
        val actionRes: Int,
        val iconRes: Int
    )

    private val premises = listOf(
        Premise(
            R.string.premise_1_title,
            R.string.premise_1_text,
            R.string.premise_1_action,
            R.drawable.ic_compass_clean
        ),
        Premise(
            R.string.premise_2_title,
            R.string.premise_2_text,
            R.string.premise_2_action,
            R.drawable.ic_scale_justice
        ),
        Premise(
            R.string.premise_3_title,
            R.string.premise_3_text,
            R.string.premise_3_action,
            R.drawable.ic_reflection_pause
        ),
        Premise(
            R.string.premise_4_title,
            R.string.premise_4_text,
            R.string.premise_4_action,
            R.drawable.ic_book_source
        ),
        Premise(
            R.string.premise_5_title,
            R.string.premise_5_text,
            R.string.premise_5_action,
            R.drawable.ic_nav_journey
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onboardingManager = OnboardingManager(this)

        updateStepUi(animate = false)

        binding.buttonAction.setOnClickListener {
            if (currentStep < premises.size - 1) {
                currentStep++
                updateStepUi(animate = true)
            } else {
                finishOnboarding()
            }
        }

        binding.buttonSkip.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun updateStepUi(animate: Boolean) {
        val premise = premises[currentStep]
        val progress = ((currentStep + 1) * 100) / premises.size

        binding.textStepIndicator.text = "Premise ${currentStep + 1} of ${premises.size}"
        binding.progressIndicator.setProgressCompat(progress, animate)

        if (animate) {
            binding.cardPremise.animate()
                .alpha(0f)
                .setDuration(120)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction {
                    binding.imagePremiseIcon.setImageResource(premise.iconRes)
                    binding.textPremiseTitle.setText(premise.titleRes)
                    binding.textPremiseContent.setText(premise.textRes)
                    binding.buttonAction.setText(premise.actionRes)

                    binding.cardPremise.animate()
                        .alpha(1f)
                        .setDuration(180)
                        .start()
                }
                .start()
        } else {
            binding.imagePremiseIcon.setImageResource(premise.iconRes)
            binding.textPremiseTitle.setText(premise.titleRes)
            binding.textPremiseContent.setText(premise.textRes)
            binding.buttonAction.setText(premise.actionRes)
        }
    }

    private fun finishOnboarding() {
        onboardingManager.isOnboardingCompleted = true
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
