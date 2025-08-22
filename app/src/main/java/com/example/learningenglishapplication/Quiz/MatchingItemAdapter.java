package com.example.learningenglishapplication.Quiz;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglishapplication.R;

import java.util.List;

public class MatchingItemAdapter extends RecyclerView.Adapter<MatchingItemAdapter.MatchingItemViewHolder> {

    private List<MatchingQuizActivity.MatchingItem> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MatchingQuizActivity.MatchingItem item);
    }

    public MatchingItemAdapter(List<MatchingQuizActivity.MatchingItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MatchingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_matching, parent, false);
        return new MatchingItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchingItemViewHolder holder, int position) {
        MatchingQuizActivity.MatchingItem item = items.get(position);
        holder.tvText.setText(item.getText());

        // Cập nhật trạng thái hiển thị dựa trên trạng thái của item
        if (item.isMatched()) {
            // Nếu đã ghép đúng, ẩn item
            holder.cardView.setVisibility(View.INVISIBLE);
        } else {
            // Nếu chưa ghép đúng, hiển thị item
            holder.cardView.setVisibility(View.VISIBLE);
            
            // Cập nhật màu sắc dựa trên trạng thái
            if (item.isIncorrect()) {
                // Nếu ghép sai, sử dụng drawable với màu đỏ
                holder.cardView.setBackgroundResource(R.drawable.matching_incorrect_background);
                holder.tvText.setTextColor(Color.WHITE);
            } else if (item.isSelected()) {
                // Nếu đang được chọn, sử dụng drawable với màu xanh lá
                holder.cardView.setBackgroundResource(R.drawable.matching_selected_background);
                holder.tvText.setTextColor(Color.WHITE);
            } else {
                // Nếu không được chọn, sử dụng drawable với màu mặc định
                if (item.getType() == MatchingQuizActivity.MatchingItem.TYPE_WORD) {
                    holder.cardView.setBackgroundResource(R.drawable.matching_word_background); // Màu xanh dương cho từ
                } else {
                    holder.cardView.setBackgroundResource(R.drawable.matching_meaning_background); // Màu cam cho nghĩa
                }
                holder.tvText.setTextColor(Color.WHITE);
            }
        }

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (!item.isMatched()) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MatchingItemViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvText;

        public MatchingItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_matching_item);
            tvText = itemView.findViewById(R.id.tv_matching_text);
        }
    }
}