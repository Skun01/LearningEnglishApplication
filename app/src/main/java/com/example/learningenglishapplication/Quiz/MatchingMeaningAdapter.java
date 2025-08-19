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

public class MatchingMeaningAdapter extends RecyclerView.Adapter<MatchingMeaningAdapter.MeaningViewHolder> {

    private List<MatchingQuizActivity.MatchingItem> items;
    private MatchingQuizActivity.OnItemClickListener listener;

    public MatchingMeaningAdapter(List<MatchingQuizActivity.MatchingItem> items, MatchingQuizActivity.OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MeaningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matching_meaning, parent, false);
        return new MeaningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeaningViewHolder holder, int position) {
        MatchingQuizActivity.MatchingItem item = items.get(position);
        holder.tvMeaning.setText(item.getText());
        
        // Xử lý trạng thái được chọn
        if (item.isSelected()) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFF8E1")); // Màu vàng nhạt
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

    static class MeaningViewHolder extends RecyclerView.ViewHolder {
        TextView tvMeaning;
        ImageView ivMatchStatus;
        CardView cardView;

        MeaningViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMeaning = itemView.findViewById(R.id.tv_matching_meaning);
            ivMatchStatus = itemView.findViewById(R.id.iv_match_status);
            cardView = (CardView) itemView;
        }
    }
}