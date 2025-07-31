package com.example.learningenglishapplication.Vocabulary;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.R;

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.VocabularyViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private OnItemInteractionListener listener;

    // Interface để xử lý cả click ngắn và dài
    public interface OnItemInteractionListener {
        void onItemClick(long vocabId); // Có thể dùng sau này
        void onItemLongClick(long vocabId, String word);
    }

    public void setOnItemInteractionListener(OnItemInteractionListener listener) {
        this.listener = listener;
    }

    public VocabularyAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }



    // ViewHolder chứa các View của một item
    public static class VocabularyViewHolder extends RecyclerView.ViewHolder {
        public TextView wordTextView;
        public TextView meaningTextView;
        // Bạn có thể thêm ImageView cho ngôi sao ở đây nếu muốn

        public VocabularyViewHolder(@NonNull View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(R.id.tv_word);
            meaningTextView = itemView.findViewById(R.id.tv_meaning);
        }
    }

    @NonNull
    @Override
    public VocabularyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_vocabulary, parent, false);
        return new VocabularyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VocabularyViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        long id = mCursor.getLong(mCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_ID));
        String word = mCursor.getString(mCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_WORD));
        String meaning = mCursor.getString(mCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_MEANING));

        holder.wordTextView.setText(word);
        holder.meaningTextView.setText(meaning);

        // THÊM SỰ KIỆN LONG CLICK
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(id, word);
                return true; // Đã xử lý sự kiện
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    // Phương thức để cập nhật dữ liệu cho RecyclerView một cách hiệu quả
    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        if (newCursor != null) {
            // Thông báo cho adapter có dữ liệu mới để vẽ lại
            notifyDataSetChanged();
        }
    }


}