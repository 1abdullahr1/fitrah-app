package com.fitrah.clearpath.ui.glossary

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import com.fitrah.clearpath.R
import java.util.regex.Pattern

object GlossaryTextParser {

    private val GLOSSARY_PATTERN = Pattern.compile("\\[(.*?)\\|(.*?)\\]")

    fun parse(
        context: Context,
        rawText: String,
        onTermClicked: (term: String, definition: String) -> Unit
    ): CharSequence {
        val matcher = GLOSSARY_PATTERN.matcher(rawText)
        val builder = SpannableStringBuilder()
        var lastEnd = 0

        val highlightColor = ContextCompat.getColor(context, R.color.glossary_highlight)

        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()

            // Append text before this match
            if (start > lastEnd) {
                builder.append(rawText.substring(lastEnd, start))
            }

            val term = matcher.group(1)?.trim() ?: ""
            val definition = matcher.group(2)?.trim() ?: ""

            val spanStart = builder.length
            builder.append(term)
            val spanEnd = builder.length

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onTermClicked(term, definition)
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.color = highlightColor
                    ds.isUnderlineText = true
                }
            }

            builder.setSpan(
                clickableSpan,
                spanStart,
                spanEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            lastEnd = end
        }

        // Append remaining text after last match
        if (lastEnd < rawText.length) {
            builder.append(rawText.substring(lastEnd))
        }

        return builder
    }
}
