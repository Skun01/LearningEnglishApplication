package com.example.learningenglishapplication.category;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningenglishapplication.Data.DataHelper.VocabularyDataHelper;
import com.example.learningenglishapplication.Data.DatabaseHelper;
import com.example.learningenglishapplication.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private final VocabularyDataHelper vocabularyDataHelper;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(long categoryId, String categoryName);
        void onItemLongClick(long categoryId, String categoryName);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public CategoryAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        vocabularyDataHelper = new VocabularyDataHelper(dbHelper);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView descriptionTextView;
        public TextView countTextView; // Bạn có thể thêm logic để đếm số từ sau
        public ProgressBar progressBar;
        public TextView progressPercentTextView;
        public com.google.android.material.button.MaterialButton btnStartCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_category_name);
            descriptionTextView=itemView.findViewById(R.id.description_vocabulary);
            countTextView = itemView.findViewById(R.id.tv_word_count);
            progressBar = itemView.findViewById(R.id.progress_category);
            progressPercentTextView = itemView.findViewById(R.id.tv_progress_percent);
            btnStartCategory = itemView.findViewById(R.id.btn_start_category);
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        String name = mCursor.getString(mCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_NAME));
        String description = mCursor.getString(mCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_DESCRIPTION));
        long id = mCursor.getLong(mCursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAT_ID));

        holder.nameTextView.setText(name);
        holder.descriptionTextView.setText(description);
        int learnedVocabulary=vocabularyDataHelper.countLearnedVocabularies(id);
        int notLearnedVoc=vocabularyDataHelper.countUnlearnedVocabularies(id);
        int total = learnedVocabulary + notLearnedVoc;
        holder.countTextView.setText(
                mContext.getString(R.string.category_progress_label, learnedVocabulary, total)
        );

// Set thanh tiến độ
        if (total > 0) {
            holder.progressBar.setMax(total);
            holder.progressBar.setProgress(learnedVocabulary);

            int percent = Math.round(learnedVocabulary * 100f / total);
            holder.progressPercentTextView.setText(
                    mContext.getString(R.string.category_progress_percent, percent)
            );
        } else {
            holder.progressBar.setMax(1);
            holder.progressBar.setProgress(0);
            holder.progressPercentTextView.setText(
                    mContext.getString(R.string.category_progress_percent, 0)
            );
        }
//        String res=learnedVocabulary+"/"+(learnedVocabulary+notLearnedVoc)+" số từ đã học";
//        holder.countTextView.setText(res);

        // Sự kiện click ngắn (giữ nguyên)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(id, name);
            }
        });

        // THÊM SỰ KIỆN LONG CLICK
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(id, name);
                return true; // Trả về true để báo rằng sự kiện đã được xử lý
            }
            return false;
        });
        
        // Thêm sự kiện click cho nút "Học ngay"
        holder.btnStartCategory.setOnClickListener(v -> {
            if (listener != null) {
                // Chuyển đến QuizSetupActivity với category đã chọn
                Intent intent = new Intent(mContext, com.example.learningenglishapplication.Quiz.QuizSetupActivity.class);
                // Thêm thông tin category vào intent
                intent.putExtra("SELECTED_CATEGORY_ID", id);
                intent.putExtra("SELECTED_CATEGORY_NAME", name);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    // Phương thức để cập nhật dữ liệu khi có thay đổi
    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
}