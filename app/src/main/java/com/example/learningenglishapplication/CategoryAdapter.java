package com.example.learningenglishapplication;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public CategoryAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView countTextView; // Bạn có thể thêm logic để đếm số từ sau

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_category_name);
            countTextView = itemView.findViewById(R.id.tv_word_count);
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

        holder.nameTextView.setText(name);
        holder.countTextView.setText(description); // Tạm dùng description để hiển thị
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