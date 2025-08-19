package com.example.learningenglishapplication.Quiz;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglishapplication.R;

import java.util.List;

public class MatchingWordAdapter extends RecyclerView.Adapter<MatchingWordAdapter.WordViewHolder> {

    private List<MatchingQuizActivity.MatchingItem> items;
    private MatchingQuizActivity.OnItemClickListener listener;

    public MatchingWordAdapter(List<MatchingQuizActivity.MatchingItem> items, MatchingQuizActivity.OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matching_word, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        MatchingQuizActivity.MatchingItem item = items.get(position);
        holder.tvWord.setText(item.getText());
        
        // Xử lý trạng thái được chọn
        if (item.isSelected()) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#E3F2FD")); // Màu xanh nhạt
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        }
        
        // Xử lý trạng thái đúng/sai sau khi kiểm tra
        if (item.isCorrect() != null) {
            holder.ivMatchStatus.setVisibility(View.VISIBLE);
            if (item.isCorrect()) {
                holder.ivMatchStatus.setImageResource(android.R.drawable.ic_menu_add); // Icon đúng
                holder.cardView.setCardBackgroundColor(Color.parseColor("#E8F5E9")); // Màu xanh lá nhạt
            } else {
                holder.ivMatchStatus.setImageResource(android.R.drawable.ic_menu_close_clear_cancel); // Icon sai
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FFEBEE")); // Màu đỏ nhạt
            }
        } else {
            holder.ivMatchStatus.setVisibility(View.GONE);
        }
        
        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            // Chỉ cho phép click nếu chưa kiểm tra kết quả
            if (item.isCorrect() == null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView tvWord;
        ImageView ivMatchStatus;
        CardView cardView;

        WordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tv_matching_word);
            ivMatchStatus = itemView.findViewById(R.id.iv_match_status);
            cardView = (CardView) itemView;
        }
    }
}