package com.pluto.plugins.preferences.ui

import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.pluto.plugins.preferences.R
import com.pluto.plugins.preferences.databinding.PlutoPrefItemSharedPrefKeyValueBinding
import com.pluto.utilities.extensions.color
import com.pluto.utilities.extensions.inflate
import com.pluto.utilities.list.DiffAwareAdapter
import com.pluto.utilities.list.DiffAwareHolder
import com.pluto.utilities.list.ListItem
import com.pluto.utilities.setOnDebounceClickListener
import com.pluto.utilities.spannable.createSpan

internal class KeyValueItemHolder(
    parent: ViewGroup,
    actionListener: DiffAwareAdapter.OnActionListener
) : DiffAwareHolder(parent.inflate(R.layout.pluto_pref___item_shared_pref_key_value), actionListener) {

    private val binding = PlutoPrefItemSharedPrefKeyValueBinding.bind(itemView)
    private val key = binding.key
    private val value = binding.value
    private val file = binding.file

    override fun onBind(item: ListItem) {
        if (item is SharedPrefKeyValuePair) {
            key.text = item.key
            file.visibility = if (item.isDefault) GONE else VISIBLE
            val fileName = item.prefLabel
            file.text = if (fileName != null) {
                if (fileName.length > MAX_FILENAME_LENGTH) {
                    "${fileName.substring(0, MAX_FILENAME_LENGTH - 2)}..."
                } else {
                    fileName
                }
            } else {
                itemView.context.createSpan {
                    append(fontColor(light(italic("null")), context.color(com.pluto.plugin.R.color.pluto___text_dark_40)))
                }
            }
            item.value?.let { value.text = it.toString() }

            itemView.setOnDebounceClickListener {
                onAction("click")
            }
        }
    }

    companion object {
        const val MAX_FILENAME_LENGTH = 18
    }
}
