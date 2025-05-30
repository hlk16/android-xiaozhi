package com.lhht.xiaozhi.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.lhht.xiaozhi.R;
import java.util.ArrayList;

public class WsUrlAdapter extends RecyclerView.Adapter<WsUrlAdapter.ViewHolder> {
    private final ArrayList<String> wsUrls;
    private final OnUrlDeleteListener listener;
    private int selectedPosition = 0; // 当前选中的位置
    private String selectedUrl = ""; // 当前选中的URL

    public interface OnUrlDeleteListener {
        void onDelete(String url);
    }

    public WsUrlAdapter(ArrayList<String> wsUrls, String currentUrl, OnUrlDeleteListener listener) {
        this.wsUrls = new ArrayList<>(wsUrls);
        this.listener = listener;
        // 设置初始选中位置
        this.selectedUrl = currentUrl;
        for (int i = 0; i < wsUrls.size(); i++) {
            if (wsUrls.get(i).equals(currentUrl)) {
                selectedPosition = i;
                break;
            }
        }
    }

    public void addUrl(String url) {
        wsUrls.add(url);
        notifyItemInserted(wsUrls.size() - 1);
    }

    public void removeUrl(String url) {
        int position = wsUrls.indexOf(url);
        if (position != -1) {
            wsUrls.remove(position);
            notifyItemRemoved(position);
            // 如果删除的是选中的项，重置选择
            if (position == selectedPosition) {
                selectedPosition = 0;
                selectedUrl = wsUrls.isEmpty() ? "" : wsUrls.get(0);
            } else if (position < selectedPosition) {
                selectedPosition--;
            }
        }
    }

    public ArrayList<String> getUrls() {
        return new ArrayList<>(wsUrls);
    }

    public String getSelectedUrl() {
        return selectedUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ws_url, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(wsUrls.get(position), position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return wsUrls.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextInputEditText urlInput;
        private final ImageButton deleteButton;
        private final RadioButton selectButton;
        private TextWatcher textWatcher;

        ViewHolder(View view) {
            super(view);
            urlInput = view.findViewById(R.id.wsUrlInput);
            deleteButton = view.findViewById(R.id.deleteButton);
            selectButton = view.findViewById(R.id.selectButton);
        }

        void bind(String url, boolean isSelected) {
            if (textWatcher != null) {
                urlInput.removeTextChangedListener(textWatcher);
            }

            urlInput.setText(url);
            selectButton.setChecked(isSelected);

            textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        wsUrls.set(pos, s.toString());
                        if (pos == selectedPosition) {
                            selectedUrl = s.toString();
                        }
                    }
                }
            };
            urlInput.addTextChangedListener(textWatcher);

            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    String url1 = wsUrls.get(position);
                    if (listener != null) {
                        listener.onDelete(url1);
                    }
                }
            });

            selectButton.setOnClickListener(v -> {
                int newPosition = getAdapterPosition();
                if (newPosition != RecyclerView.NO_POSITION && newPosition != selectedPosition) {
                    int oldPosition = selectedPosition;
                    selectedPosition = newPosition;
                    selectedUrl = wsUrls.get(newPosition);
                    notifyItemChanged(oldPosition);
                    notifyItemChanged(newPosition);
                }
            });
        }
    }
} 