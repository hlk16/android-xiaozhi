package com.lhht.xiaozhi.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.lhht.xiaozhi.R
import com.lhht.xiaozhi.models.WsUrl

class WsUrlAdapter : RecyclerView.Adapter<WsUrlAdapter.ViewHolder>() {
    private val wsUrls = mutableListOf<WsUrl>()

    fun setData(urls: List<WsUrl>) {
        wsUrls.clear()
        wsUrls.addAll(urls)
        notifyDataSetChanged()
    }

    fun getData(): List<WsUrl> = wsUrls.toList()

    fun addUrl(url: WsUrl) {
        wsUrls.add(url)
        notifyItemInserted(wsUrls.size - 1)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val urlInput: EditText = view.findViewById(R.id.wsUrlInput)
        private val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
        private var textWatcher: TextWatcher? = null

        fun bind(wsUrl: WsUrl) {
            // Remove previous TextWatcher to avoid duplicate listeners
            textWatcher?.let { urlInput.removeTextChangedListener(it) }

            urlInput.setText(wsUrl.url)

            // Create and add new TextWatcher
            textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    wsUrl.url = s.toString()
                }
            }
            urlInput.addTextChangedListener(textWatcher)

            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    wsUrls.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ws_url, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(wsUrls[position])
    }

    override fun getItemCount(): Int = wsUrls.size
} 