package com.example.learningenglishapplication.Vocabulary;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.R;

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.VocabularyViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private OnItemInteractionListener listener;
    private OnFavoriteClickListener favoriteListener;

    private long highlightedVocabId = -1;
    private Handler handler = new Handler(Looper.getMainLooper());
    private static final long HIGHLIGHT_DURATION = 3000; // 3 giây

    public interface OnFavoriteClickListener {
        void onFavoriteClick(long vocabId, int isFavorite);
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener favoriteListener) {
        this.favoriteListener = favoriteListener;
    }

    public interface OnItemInteractionListener {
        void onItemClick(long vocabId);
        void onItemLongClick(long vocabId, String word);
    }

    public void setOnItemInteractionListener(OnItemInteractionListener listener) {
        this.listener = listener;
    }

    public VocabularyAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public static class VocabularyViewHolder extends RecyclerView.ViewHolder {
        public TextView wordTextView;
        public TextView meaningTextView;
        public ImageView starImageView;

        public VocabularyViewHolder(@NonNull View itemView) {
            super(itemView);
            wordTextView = itemView.findViewById(R.id.tv_word);
            meaningTextView = itemView.findViewById(R.id.tv_meaning);
            // Sửa lại ID từ iv_star sang iv_favorite
            starImageView = itemView.findViewById(R.id.iv_favorite);
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
        int isFavorite = mCursor.getInt(mCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_IS_FAVORITE));
        int learnedStatus = mCursor.getInt(mCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_LEARNED));

        holder.wordTextView.setText(word);
        holder.meaningTextView.setText(meaning);

        if (learnedStatus == 0) {
            holder.wordTextView.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
            holder.meaningTextView.setTextColor(ContextCompat.getColor(mContext, R.color.gray));
        } else {
            holder.wordTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
            holder.meaningTextView.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
        }

        // Thay đổi drawable của dấu sao thành icon trái tim
        if (isFavorite == 1) {
            holder.starImageView.setImageResource(R.drawable.ic_favorite); // Icon trái tim đầy
        } else {
            holder.starImageView.setImageResource(R.drawable.ic_favorite_border); // Icon trái tim rỗng
        }

        if (id == highlightedVocabId) {
            holder.itemView.setBackgroundResource(R.drawable.highlight_border);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.custom_background);
        }

        holder.starImageView.setOnClickListener(v -> {
            if (favoriteListener != null) {
                int newFavoriteStatus = (isFavorite == 1) ? 0 : 1;
                favoriteListener.onFavoriteClick(id, newFavoriteStatus);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(id);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(id, word);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    public void setHighlightedVocabId(long vocabId) {
        this.highlightedVocabId = vocabId;
        notifyDataSetChanged();
        if (vocabId != -1) {
            handler.postDelayed(() -> {
                this.highlightedVocabId = -1;
                notifyDataSetChanged();
            }, HIGHLIGHT_DURATION);
        }
    }

    public long getVocabIdAtPosition(int position) {
        if (mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_ID));
        }
        return -1;
    }

    public String getWordAtPosition(int position) {
        if (mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getString(mCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VOCAB_WORD));
        }
        return "";
    }

    public Cursor getCursor() {
        return mCursor;
    }
}